package com.felipelaurindo.mamaocomacucar.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.repository.FirestoreRepository
import com.felipelaurindo.mamaocomacucar.ui.map.NextBadgeInfo
import com.felipelaurindo.mamaocomacucar.ui.map.UserBadge
import com.felipelaurindo.mamaocomacucar.ui.theme.*
import com.felipelaurindo.mamaocomacucar.util.normalizeUsername
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AccountSettingsSheet(
    currentUser: LoggedUser,
    userTreeCount: Int,
    badge: UserBadge,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val repository = remember { FirestoreRepository() }

    var tempDisplayName by remember { mutableStateOf(currentUser.displayName) }
    var tempUsername by remember { mutableStateOf(currentUser.username) }
    var isSavingProfile by remember { mutableStateOf(false) }
    var profileError by remember { mutableStateOf("") }
    var isProfileUpdated by remember { mutableStateOf(false) }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deletePassword by remember { mutableStateOf("") }
    var isDeletingProfile by remember { mutableStateOf(false) }

    val isPasswordUser = remember {
        val user = FirebaseAuth.getInstance().currentUser
        user?.providerData?.any { it.providerId == "password" } ?: true
    }

    val nextBadgeInfo = remember(userTreeCount) {
        when {
            userTreeCount == 0 -> NextBadgeInfo("BROTINHO", "🌿", targetCount = 1, currentCount = 0, progress = 0f)
            userTreeCount in 1..4 -> NextBadgeInfo("CULTIVADOR", "🪴", targetCount = 5, currentCount = userTreeCount, progress = userTreeCount / 5f)
            userTreeCount in 5..9 -> NextBadgeInfo("PROTETOR DA FLORESTA", "🌳", targetCount = 10, currentCount = userTreeCount, progress = userTreeCount / 10f)
            userTreeCount in 10..24 -> NextBadgeInfo("GUARDIÃO DAS FRUTAS", "🍊", targetCount = 25, currentCount = userTreeCount, progress = userTreeCount / 25f)
            userTreeCount in 25..49 -> NextBadgeInfo("MESTRE FRUTÍFERO", "🍒", targetCount = 50, currentCount = userTreeCount, progress = userTreeCount / 50f)
            else -> null
        }
    }

    val userInitial = remember(currentUser.displayName, currentUser.username) {
        val name = currentUser.displayName.ifBlank { currentUser.username }
        if (name.isNotBlank()) name.first().uppercase() else "👤"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("👤", fontSize = 20.sp)
                        Text(
                            "Configurações do Perfil",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Stone900
                        )
                    }
                    IconButton(
                        onClick = { onDismiss(); isProfileUpdated = false },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Stone100, CircleShape)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Fechar",
                            tint = Stone600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Profile Card Header (compacto e elegante)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MamaoOrangeLight,
                    border = BorderStroke(1.dp, MamaoOrangeContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MamaoOrange,
                            modifier = Modifier.size(44.dp),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userInitial,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = currentUser.displayName.ifBlank { "Membro da Comunidade" },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Stone900,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "@${currentUser.username}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MamaoOrange,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentUser.email,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = Stone500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                HorizontalDivider(color = Stone200)

                // Form Section
                val isFormChanged = tempDisplayName.trim() != currentUser.displayName || tempUsername.trim() != currentUser.username
                val isFormValid = tempDisplayName.isNotBlank() && tempUsername.isNotBlank()

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "DADOS DO PERFIL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Stone400
                    )

                    // Display Name
                    OutlinedTextField(
                        value = tempDisplayName,
                        onValueChange = {
                            tempDisplayName = it
                            isProfileUpdated = false
                            profileError = ""
                        },
                        label = { Text("Nome de exibição") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Stone900,
                            unfocusedTextColor = Stone900,
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50,
                            cursorColor = MamaoOrange,
                            focusedLabelColor = MamaoOrange,
                            unfocusedLabelColor = Stone400
                        )
                    )

                    // Username
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = {
                            tempUsername = normalizeUsername(it)
                            isProfileUpdated = false
                            profileError = ""
                        },
                        label = { Text("Nome de usuário (username)") },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = {
                            Text(
                                "@",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MamaoOrange
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MamaoOrange,
                            unfocusedTextColor = MamaoOrange,
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50,
                            cursorColor = MamaoOrange,
                            focusedLabelColor = MamaoOrange,
                            unfocusedLabelColor = Stone400
                        )
                    )

                    // Save Button - surge quando houver alteração
                    AnimatedVisibility(
                        visible = isFormChanged,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isSavingProfile = true
                                    profileError = ""
                                    isProfileUpdated = false

                                    try {
                                        val newUsername = tempUsername.trim()
                                        if (newUsername != currentUser.username) {
                                            val isUnique = repository.checkUsernameUnique(newUsername)
                                            if (!isUnique) {
                                                profileError = "Este nome de usuário já está em uso por outro membro."
                                                isSavingProfile = false
                                                return@launch
                                            }
                                        }

                                        repository.updateUsername(
                                            uid = currentUser.uid,
                                            oldUsername = currentUser.username,
                                            newUsername = newUsername,
                                            email = currentUser.email,
                                            displayName = tempDisplayName.trim()
                                        )

                                        isProfileUpdated = true
                                    } catch (e: Exception) {
                                        profileError = "Não foi possível salvar as alterações do perfil."
                                    } finally {
                                        isSavingProfile = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            enabled = !isSavingProfile && isFormValid,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MamaoOrange,
                                contentColor = Color.White
                            )
                        ) {
                            if (isSavingProfile) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Salvar Alterações",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    if (profileError.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Rose50,
                            border = BorderStroke(1.dp, Rose200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "⚠️ $profileError",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Rose700,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (isProfileUpdated) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Emerald50,
                            border = BorderStroke(1.dp, Emerald600.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "✨ Perfil atualizado com sucesso!",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Emerald600,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Stone200)

                // Stats & Gamification Section (Card unificado e harmonioso)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "PROGRESSO E NÍVEL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Stone400
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Stone50,
                        border = BorderStroke(1.dp, Stone200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Linha do Nível Atual
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MamaoOrangeLight,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(badge.icon, fontSize = 22.sp)
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        badge.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Stone900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "$userTreeCount ${if (userTreeCount == 1) "fruteira catalogada" else "fruteiras catalogadas"}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MamaoOrange,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Barra de Progresso para o Próximo Nível (se houver)
                            if (nextBadgeInfo != null) {
                                HorizontalDivider(color = Stone200)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f, fill = false),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                "Próximo:",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Stone500,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                            Text(
                                                "${nextBadgeInfo.nextIcon} ${nextBadgeInfo.nextTitle}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MamaoOrangeDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            "$userTreeCount / ${nextBadgeInfo.targetCount}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = Stone600
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { nextBadgeInfo.progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = MamaoOrange,
                                        trackColor = MamaoOrangeContainer
                                    )

                                    val remaining = nextBadgeInfo.targetCount - userTreeCount
                                    val countText = if (remaining == 1) "Falta apenas 1 fruteira" else "Faltam apenas $remaining fruteiras"
                                    Text(
                                        "$countText para o próximo nível 🚀",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = Stone500
                                    )
                                }
                            } else {
                                HorizontalDivider(color = Stone200)
                                Text(
                                    "🏆 Você alcançou o nível máximo de contribuição no mapa!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MamaoOrangeDark
                                    )
                                )
                            }
                        }
                    }
                }

                // Session / Logout Section
                HorizontalDivider(color = Stone200)
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Stone800,
                        containerColor = Stone50
                    ),
                    border = BorderStroke(1.dp, Stone200)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            tint = MamaoOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Sair da Conta",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Stone800
                        )
                    }
                }

                // Danger Zone
                if (!showDeleteConfirm) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Excluir Minha Conta",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Rose600
                        )
                    }
                } else {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Rose50,
                            border = BorderStroke(1.dp, Rose200)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "⚠️ Atenção: esta ação apagará permanentemente sua conta, seu perfil e o histórico de fruteiras que você catalogou. Esta operação não poderá ser desfeita.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Rose700
                                )

                                if (isPasswordUser) {
                                    OutlinedTextField(
                                        value = deletePassword,
                                        onValueChange = { deletePassword = it },
                                        label = { Text("Digite sua senha atual para confirmar") },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Stone900,
                                            unfocusedTextColor = Stone900,
                                            focusedBorderColor = Rose600,
                                            unfocusedBorderColor = Rose200,
                                            focusedLabelColor = Rose600,
                                            cursorColor = Rose600
                                        )
                                    )
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Stone100,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "Sua conta está conectada através do Google. Clique abaixo para confirmar a exclusão.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Stone600,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = { showDeleteConfirm = false; deletePassword = "" },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = !isDeletingProfile
                                    ) {
                                        Text("Cancelar", style = MaterialTheme.typography.labelMedium)
                                    }
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                isDeletingProfile = true
                                                try {
                                                    val auth = FirebaseAuth.getInstance()
                                                    val firebaseUser = auth.currentUser ?: return@launch

                                                    // Re-authenticate only if using password provider
                                                    if (isPasswordUser) {
                                                        val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, deletePassword)
                                                        firebaseUser.reauthenticate(credential).await()
                                                    }

                                                    // Delete from Firestore
                                                    repository.deleteUserProfile(currentUser.uid, currentUser.username)

                                                    // Delete auth user
                                                    firebaseUser.delete().await()

                                                    onLogout()
                                                } catch (e: Exception) {
                                                    val msg = when {
                                                        e.message?.contains("wrong-password") == true ||
                                                        e.message?.contains("invalid-credential") == true -> "Senha incorreta."
                                                        e.message?.contains("requires-recent-login") == true ->
                                                            "Por segurança, faça login novamente com o Google antes de excluir sua conta."
                                                        else -> "Não foi possível excluir a conta."
                                                    }
                                                    profileError = msg
                                                } finally {
                                                    isDeletingProfile = false
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Rose600,
                                            contentColor = Color.White
                                        ),
                                        enabled = !isDeletingProfile && (!isPasswordUser || deletePassword.isNotBlank())
                                    ) {
                                        if (isDeletingProfile) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                "Sim, excluir conta",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
