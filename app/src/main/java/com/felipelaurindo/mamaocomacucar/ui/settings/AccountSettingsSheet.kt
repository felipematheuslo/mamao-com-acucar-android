package com.felipelaurindo.mamaocomacucar.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                        Text("👤", fontSize = 22.sp)
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

                // Profile Card Header
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MamaoOrangeLight,
                    border = BorderStroke(1.dp, MamaoOrangeContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MamaoOrange,
                            modifier = Modifier.size(52.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userInitial,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = currentUser.displayName.ifBlank { "Membro da Comunidade" },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Stone900
                            )
                            Text(
                                text = "@${currentUser.username}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MamaoOrange
                            )
                            Text(
                                text = currentUser.email,
                                style = MaterialTheme.typography.labelSmall,
                                color = Stone500
                            )
                        }
                    }
                }

                HorizontalDivider(color = Stone200)

                // Form Section
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "SEUS DADOS DE PERFIL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
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
                        shape = RoundedCornerShape(14.dp),
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
                        shape = RoundedCornerShape(14.dp),
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

                    // Email (disabled)
                    OutlinedTextField(
                        value = currentUser.email,
                        onValueChange = {},
                        label = { Text("E-mail cadastrado") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Stone700,
                            disabledBorderColor = Stone200,
                            disabledContainerColor = Stone100,
                            disabledLabelColor = Stone400
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Save Button
                    val isFormChanged = tempDisplayName.trim() != currentUser.displayName || tempUsername.trim() != currentUser.username
                    val isFormValid = tempDisplayName.isNotBlank() && tempUsername.isNotBlank()

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
                            .height(48.dp),
                        enabled = !isSavingProfile && isFormChanged && isFormValid,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MamaoOrange,
                            contentColor = Color.White,
                            disabledContainerColor = Stone100,
                            disabledContentColor = Stone400
                        )
                    ) {
                        if (isSavingProfile) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
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

                // Stats & Gamification Section
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "SEU PROGRESSO E CONQUISTAS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Stone400
                    )

                    // Stats Row
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = Stone50,
                            border = BorderStroke(1.dp, Stone200)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "ÁRVORES MAPEADAS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = Stone400
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "$userTreeCount",
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                        color = Stone900
                                    )
                                    Text(
                                        if (userTreeCount == 1) "árvore" else "árvores",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Stone500,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = Stone50,
                            border = BorderStroke(1.dp, Stone200)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "NÍVEL DA COMUNIDADE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = Stone400
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        badge.icon,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        badge.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        ),
                                        color = MamaoOrange,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    // Badge details card
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MamaoOrangeLight,
                        border = BorderStroke(1.dp, MamaoOrangeContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MamaoOrangeContainer,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(badge.icon, fontSize = 24.sp)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Nível Atual: ${badge.title}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Stone900
                                    )
                                    Text(
                                        badge.desc,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Stone600
                                    )
                                }
                            }

                            // Progress to next badge
                            if (nextBadgeInfo != null) {
                                HorizontalDivider(color = MamaoOrangeContainer.copy(alpha = 0.7f))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        ) {
                                            Text(
                                                "Próximo Nível",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = Stone500
                                            )
                                            Text(
                                                "${nextBadgeInfo.nextIcon} ${nextBadgeInfo.nextTitle}",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp
                                                ),
                                                color = MamaoOrangeDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.9f),
                                            border = BorderStroke(1.dp, MamaoOrangeContainer)
                                        ) {
                                            Text(
                                                "$userTreeCount / ${nextBadgeInfo.targetCount}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp
                                                ),
                                                color = MamaoOrangeDark,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                maxLines = 1
                                            )
                                        }
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
                                    Text(
                                        "Faltam apenas $remaining ${if (remaining == 1) "fruteira" else "fruteiras"} para você subir de nível! 🚀",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                        color = Stone600
                                    )
                                }
                            } else {
                                Text(
                                    "🏆 Parabéns! Você alcançou o nível máximo de contribuição no mapa!",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MamaoOrangeDark
                                )
                            }
                        }
                    }
                }

                // Danger Zone
                HorizontalDivider(color = Stone200)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "ZONA DE PERIGO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Rose600
                    )

                    if (!showDeleteConfirm) {
                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
                            border = BorderStroke(1.dp, Rose200)
                        ) {
                            Text(
                                "Excluir Minha Conta",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
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
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
