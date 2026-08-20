package com.felipelaurindo.mamaocomacucar.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipelaurindo.mamaocomacucar.data.model.*
import com.felipelaurindo.mamaocomacucar.data.repository.FirestoreRepository
import com.felipelaurindo.mamaocomacucar.util.calculateDistance
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL

data class TreeWithDistance(
    val tree: TreeItem,
    val distance: Double
)

data class UserBadge(
    val title: String,
    val desc: String,
    val icon: String
)

data class NextBadgeInfo(
    val nextTitle: String,
    val nextIcon: String,
    val targetCount: Int,
    val currentCount: Int,
    val progress: Float
)


class MapViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    // Trees
    private val _trees = MutableStateFlow<List<TreeItem>>(emptyList())
    val trees: StateFlow<List<TreeItem>> = _trees.asStateFlow()

    private val _selectedTree = MutableStateFlow<TreeItem?>(null)
    val selectedTree: StateFlow<TreeItem?> = _selectedTree.asStateFlow()

    private val _updates = MutableStateFlow<List<CommentUpdate>>(emptyList())
    val updates: StateFlow<List<CommentUpdate>> = _updates.asStateFlow()

    // Location
    private val _userLocation = MutableStateFlow(Pair(-23.5575, -46.6624)) // São Paulo default
    val userLocation: StateFlow<Pair<Double, Double>> = _userLocation.asStateFlow()

    private val _mapCenter = MutableStateFlow(Pair(-23.5575, -46.6624))
    val mapCenter: StateFlow<Pair<Double, Double>> = _mapCenter.asStateFlow()

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("todos")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    // UI state
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isAddingTree = MutableStateFlow(false)
    val isAddingTree: StateFlow<Boolean> = _isAddingTree.asStateFlow()

    // Creator usernames cache
    private val _creatorUsernames = MutableStateFlow<Map<String, String>>(emptyMap())
    val creatorUsernames: StateFlow<Map<String, String>> = _creatorUsernames.asStateFlow()

    private var treesListener: ListenerRegistration? = null
    private var updatesListener: ListenerRegistration? = null

    init {
        subscribeToTrees()
    }

    private fun subscribeToTrees() {
        treesListener = repository.subscribeTrees { trees ->
            _trees.value = trees
            fetchMissingUsernames(trees.map { it.createdBy })
        }
    }

    fun selectTree(tree: TreeItem?) {
        _selectedTree.value = tree
        if (tree != null) {
            _mapCenter.value = Pair(tree.latitude, tree.longitude)
            subscribeToUpdates(tree.id)
        } else {
            updatesListener?.remove()
            _updates.value = emptyList()
        }
    }

    private fun subscribeToUpdates(treeId: String) {
        updatesListener?.remove()
        updatesListener = repository.subscribeUpdates(treeId) { updates ->
            _updates.value = updates
            fetchMissingUsernames(updates.map { it.createdBy })
        }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setStatusFilter(filter: String) { _statusFilter.value = filter }
    fun setUserLocation(lat: Double, lng: Double) {
        _userLocation.value = Pair(lat, lng)
    }
    fun setMapCenter(lat: Double, lng: Double) { _mapCenter.value = Pair(lat, lng) }
    fun setIsAddingTree(adding: Boolean) { _isAddingTree.value = adding }

    fun showToast(message: String) {
        _toastMessage.value = message
        viewModelScope.launch {
            kotlinx.coroutines.delay(4000)
            _toastMessage.value = null
        }
    }

    fun dismissToast() { _toastMessage.value = null }

    fun getFilteredTrees(): List<TreeWithDistance> {
        val loc = _userLocation.value
        val query = _searchQuery.value.lowercase()
        val filter = _statusFilter.value

        return _trees.value
            .map { tree ->
                TreeWithDistance(
                    tree = tree,
                    distance = calculateDistance(loc.first, loc.second, tree.latitude, tree.longitude)
                )
            }
            .filter { tw ->
                val matchesSearch = query.isEmpty() ||
                    tw.tree.name.lowercase().contains(query) ||
                    tw.tree.species.lowercase().contains(query) ||
                    tw.tree.createdByName.lowercase().contains(query)

                val matchesStatus = filter == "todos" || tw.tree.currentStatus.value == filter

                matchesSearch && matchesStatus
            }
            .sortedBy { it.distance }
    }

    fun addTree(
        species: String,
        name: String,
        status: TreeStatus,
        latitude: Double,
        longitude: Double,
        createdBy: String,
        createdByName: String
    ) {
        viewModelScope.launch {
            try {
                val tree = TreeItem(
                    species = species,
                    name = name.ifEmpty { "Pé de $species" },
                    currentStatus = status,
                    latitude = latitude,
                    longitude = longitude,
                    createdBy = createdBy,
                    createdByName = createdByName,
                    mainImage = "",
                    createdAt = java.time.Instant.now().toString()
                )
                repository.addTree(tree)
                _isAddingTree.value = false
                showToast("🌳 Fruteira cadastrada com sucesso no mapa comunitário!")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error adding tree", e)
                showToast("Não foi possível salvar a fruteira. Tente novamente.")
            }
        }
    }

    fun submitReport(
        treeId: String,
        status: TreeStatus,
        createdBy: String,
        createdByName: String
    ) {
        viewModelScope.launch {
            try {
                val statusLabels = mapOf(
                    "vazio" to "Vazio",
                    "florindo" to "Florindo",
                    "crescendo" to "Verde",
                    "pronto" to "Maduro"
                )
                val autoComment = "Fase atualizada para ${statusLabels[status.value] ?: status.value}."

                val update = CommentUpdate(
                    treeId = treeId,
                    comment = autoComment,
                    statusAtReport = status,
                    createdBy = createdBy,
                    createdByName = createdByName,
                    createdAt = java.time.Instant.now().toString()
                )
                repository.addUpdate(update)

                // Update selected tree locally
                _selectedTree.value = _selectedTree.value?.copy(currentStatus = status)
                showToast("✅ Fase da fruteira atualizada com sucesso!")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error submitting report", e)
                showToast("Não foi possível registrar a atualização.")
            }
        }
    }

    fun searchLocation(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            try {
                val url = "https://nominatim.openstreetmap.org/search?format=json&q=${
                    java.net.URLEncoder.encode(query, "UTF-8")
                }&limit=1"
                val response = URL(url).readText()
                val arr = JSONArray(response)
                if (arr.length() > 0) {
                    val obj = arr.getJSONObject(0)
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")
                    _mapCenter.value = Pair(lat, lon)
                } else {
                    showToast("Localização não encontrada. Tente buscar por outro endereço ou ponto de referência.")
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error searching location", e)
                showToast("Não foi possível realizar a busca de localização.")
            }
        }
    }

    private fun fetchMissingUsernames(uids: List<String>) {
        val current = _creatorUsernames.value
        val missing = uids.distinct().filter { it !in current }
        if (missing.isEmpty()) return

        viewModelScope.launch {
            val newEntries = mutableMapOf<String, String>()
            for (uid in missing) {
                try {
                    val profile = repository.getUserProfile(uid)
                    newEntries[uid] = if (profile?.username != null) "@${profile.username}"
                        else "@${uid.removePrefix("user-")}"
                } catch (_: Exception) { }
            }
            if (newEntries.isNotEmpty()) {
                _creatorUsernames.value = current + newEntries
            }
        }
    }

    // Gamification
    fun getUserMappedTreesCount(uid: String): Int {
        return _trees.value.count { it.createdBy == uid }
    }

    fun getUserBadge(count: Int): UserBadge {
        return when {
            count == 0 -> UserBadge(
                "SEMENTINHA",
                "Você ainda não cadastrou nenhuma fruteira. Mapeie sua primeira árvore para começar a germinar!",
                "🌱"
            )
            count in 1..4 -> UserBadge(
                "BROTINHO",
                "Você começou a contribuir com o acervo comunitário de fruteiras do seu bairro.",
                "🌿"
            )
            count in 5..9 -> UserBadge(
                "CULTIVADOR",
                "Membro ativo que ajuda a expandir e manter o mapa urbano de frutas sempre vivo.",
                "🪴"
            )
            count in 10..24 -> UserBadge(
                "PROTETOR DA FLORESTA",
                "Referência na comunidade, mantendo o mapa local rico e bem cuidado.",
                "🌳"
            )
            count in 25..49 -> UserBadge(
                "GUARDIÃO DAS FRUTAS",
                "Grande guardião urbano, promovendo a colheita coletiva e sustentável!",
                "🍊"
            )
            else -> UserBadge(
                "MESTRE FRUTÍFERO",
                "Uma verdadeira lenda do mapeamento e da colheita comunitária!",
                "🍒"
            )
        }
    }

    fun getNextBadgeInfo(count: Int): NextBadgeInfo? {
        return when {
            count == 0 -> NextBadgeInfo("BROTINHO", "🌿", targetCount = 1, currentCount = 0, progress = 0f)
            count in 1..4 -> NextBadgeInfo("CULTIVADOR", "🪴", targetCount = 5, currentCount = count, progress = count / 5f)
            count in 5..9 -> NextBadgeInfo("PROTETOR DA FLORESTA", "🌳", targetCount = 10, currentCount = count, progress = count / 10f)
            count in 10..24 -> NextBadgeInfo("GUARDIÃO DAS FRUTAS", "🍊", targetCount = 25, currentCount = count, progress = count / 25f)
            count in 25..49 -> NextBadgeInfo("MESTRE FRUTÍFERO", "🍒", targetCount = 50, currentCount = count, progress = count / 50f)
            else -> null
        }
    }

    fun getCreatorUsername(uid: String, fallbackName: String): String {
        return _creatorUsernames.value[uid] ?: "@${fallbackName.split(" ").first().lowercase()}"
    }

    override fun onCleared() {
        super.onCleared()
        treesListener?.remove()
        updatesListener?.remove()
    }
}
