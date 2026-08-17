package com.felipelaurindo.mamaocomacucar.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.repository.FirestoreRepository
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            "Configurações da Conta",
                            style = MaterialTheme.typography.titleSmall,
                            color = Stone950
                        )
                    }
                    IconButton(onClick = { onDismiss(); isProfileUpdated = false }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Close, null, tint = Stone400, modifier = Modifier.size(16.dp))
                    }
                }

                HorizontalDivider(color = Stone100)

                // Display Name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("NOME DE EXIBIÇÃO", style = MaterialTheme.typography.labelSmall, color = Stone400)
                    OutlinedTextField(
                        value = tempDisplayName,
                        onValueChange = { tempDisplayName = it; isProfileUpdated = false; profileError = "" },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50
                        )
                    )
                }

                // Username
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("USERNAME", style = MaterialTheme.typography.labelSmall, color = MamaoOrange)
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = {
                            tempUsername = normalizeUsername(it)
                            isProfileUpdated = false
                            profileError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = {
                            Text("@", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Stone400)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MamaoOrange,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone50,
                            focusedTextColor = MamaoOrange,
                            unfocusedTextColor = MamaoOrange
                        )
                    )
                }

                // Email (disabled)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("E-MAIL DE CADASTRO", style = MaterialTheme.typography.labelSmall, color = Stone400)
                    OutlinedTextField(
                        value = currentUser.email,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Stone200,
                            disabledContainerColor = Stone100,
                            disabledTextColor = Stone500
                        )
                    )
                }

                // Save button
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
                                        profileError = "Este username já está em uso."
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
                                profileError = "Erro ao salvar alterações."
                            } finally {
                                isSavingProfile = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    enabled = !isSavingProfile &&
                            tempDisplayName.isNotBlank() &&
                            tempUsername.isNotBlank() &&
                            (tempDisplayName.trim() != currentUser.displayName || tempUsername != currentUser.username),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MamaoOrange)
                ) {
                    if (isSavingProfile) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Salvar Alterações", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (profileError.isNotBlank()) {
                    Text("⚠️ $profileError", style = MaterialTheme.typography.labelSmall, color = Rose600)
                }
                if (isProfileUpdated) {
                    Text("✨ Alterações salvas com sucesso!", style = MaterialTheme.typography.labelSmall, color = Emerald600)
                }

                // Stats
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Stone50,
                        border = BorderStroke(1.dp, Stone200)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ÁRVORES MAPEADAS", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("$userTreeCount", style = MaterialTheme.typography.headlineMedium, color = Stone900)
                                Text("pins", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Stone50,
                        border = BorderStroke(1.dp, Stone200)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("NÍVEL", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${badge.icon} ${badge.title}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                color = MamaoOrange
                            )
                        }
                    }
                }

                // Badge card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MamaoOrangeLight,
                    border = BorderStroke(1.dp, MamaoOrangeContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MamaoOrangeContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(badge.icon, fontSize = 20.sp)
                            }
                        }
                        Column {
                            Text(
                                "Emblema: ${badge.title}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                color = Stone900
                            )
                            Text(
                                badge.desc,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Stone500
                            )
                        }
                    }
                }

                // Danger Zone
                HorizontalDivider(color = Stone100)
                Text("ZONA DE PERIGO", style = MaterialTheme.typography.labelSmall, color = Rose600, letterSpacing = 2.sp)

                if (!showDeleteConfirm) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
                        border = BorderStroke(1.dp, Rose200)
                    ) {
                        Text("Deletar Perfil", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Rose50,
                        border = BorderStroke(1.dp, Rose200)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "⚠️ Tem certeza? Esta ação apagará permanentemente sua conta, seus dados de perfil e todas as árvores que você mapeou no sistema. Esta ação não pode ser desfeita.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Rose700
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("SENHA ATUAL DO PERFIL", style = MaterialTheme.typography.labelSmall, color = Rose700)
                                OutlinedTextField(
                                    value = deletePassword,
                                    onValueChange = { deletePassword = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Confirme sua senha", color = Stone400) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Rose600,
                                        unfocusedBorderColor = Rose200
                                    )
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { showDeleteConfirm = false; deletePassword = "" },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
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

                                                // Re-authenticate
                                                val credential = EmailAuthProvider.credential(firebaseUser.email!!, deletePassword)
                                                firebaseUser.reauthenticateWithCredential(credential).await()

                                                // Delete from Firestore
                                                repository.deleteUserProfile(currentUser.uid, currentUser.username)

                                                // Delete auth user
                                                firebaseUser.delete().await()

                                                onLogout()
                                            } catch (e: Exception) {
                                                val msg = when {
                                                    e.message?.contains("wrong-password") == true ||
                                                    e.message?.contains("invalid-credential") == true -> "Senha incorreta."
                                                    else -> "Erro ao deletar o perfil."
                                                }
                                                profileError = msg
                                            } finally {
                                                isDeletingProfile = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                                    enabled = !isDeletingProfile && deletePassword.isNotBlank()
                                ) {
                                    Text(
                                        if (isDeletingProfile) "Deletando..." else "Sim, Deletar",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
