package com.felipelaurindo.mamaocomacucar.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipelaurindo.mamaocomacucar.data.model.LoggedUser
import com.felipelaurindo.mamaocomacucar.data.repository.FirestoreRepository
import com.felipelaurindo.mamaocomacucar.util.normalizeUsername
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: LoggedUser) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repository = FirestoreRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _isSubmittingGoogle = MutableStateFlow(false)
    val isSubmittingGoogle: StateFlow<Boolean> = _isSubmittingGoogle.asStateFlow()

    private val _showVerificationSent = MutableStateFlow(false)
    val showVerificationSent: StateFlow<Boolean> = _showVerificationSent.asStateFlow()

    private val _registrationComplete = MutableStateFlow(false)
    val registrationComplete: StateFlow<Boolean> = _registrationComplete.asStateFlow()

    private val _resendSuccess = MutableStateFlow(false)
    val resendSuccess: StateFlow<Boolean> = _resendSuccess.asStateFlow()

    private val _isSendingPasswordReset = MutableStateFlow(false)
    val isSendingPasswordReset: StateFlow<Boolean> = _isSendingPasswordReset.asStateFlow()

    private val _passwordResetError = MutableStateFlow<String?>(null)
    val passwordResetError: StateFlow<String?> = _passwordResetError.asStateFlow()

    private val _passwordResetSuccess = MutableStateFlow(false)
    val passwordResetSuccess: StateFlow<Boolean> = _passwordResetSuccess.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null && firebaseUser.isEmailVerified) {
                viewModelScope.launch {
                    val username = try {
                        val profile = repository.getUserProfile(firebaseUser.uid)
                        if (profile != null && profile.username.isNotBlank()) {
                            profile.username
                        } else {
                            // First time logging in (e.g. Google Sign-In): auto-provision profile in Firestore
                            val baseUsername = normalizeUsername(
                                firebaseUser.email?.substringBefore("@") ?: "usuario"
                            ).ifEmpty { "usuario" }

                            var uniqueUsername = baseUsername
                            var counter = 1
                            while (!repository.checkUsernameUnique(uniqueUsername)) {
                                uniqueUsername = "${baseUsername}${counter}"
                                counter++
                            }

                            val resolvedDisplayName = firebaseUser.displayName?.trim()?.ifEmpty { null }
                                ?: firebaseUser.email?.substringBefore("@")
                                ?: "Usuário"

                            repository.registerUsername(
                                uid = firebaseUser.uid,
                                username = uniqueUsername,
                                email = firebaseUser.email ?: "",
                                displayName = resolvedDisplayName
                            )
                            uniqueUsername
                        }
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Error resolving user profile on auth state change", e)
                        normalizeUsername(
                            firebaseUser.email?.substringBefore("@") ?: "usuario"
                        )
                    }

                    _authState.value = AuthState.Authenticated(
                        LoggedUser(
                            uid = firebaseUser.uid,
                            displayName = firebaseUser.displayName
                                ?: firebaseUser.email?.substringBefore("@")
                                ?: "Usuário",
                            username = username,
                            email = firebaseUser.email ?: ""
                        )
                    )
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun loginWithGoogle(context: Context) {
        _loginError.value = null
        _registerError.value = null
        _showVerificationSent.value = false
        _resendSuccess.value = false
        _isSubmittingGoogle.value = true

        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                val serverClientId = try {
                    context.getString(com.felipelaurindo.mamaocomacucar.R.string.default_web_client_id)
                } catch (_: Exception) {
                    "808257197765-hlpfahvq68cq9ohshfdberbh4dh1e417.apps.googleusercontent.com"
                }

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(authCredential).await()
                    // Auth state listener automatically triggers and authenticates
                } else {
                    _loginError.value = "Tipo de credencial Google inesperado."
                }
            } catch (e: GetCredentialCancellationException) {
                // User cancelled or closed the account selection prompt; no error message needed
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google Sign-In failed", e)
                val message = when {
                    e.message?.contains("16") == true || e.message?.contains("Cannot find a matching credential") == true ->
                        "Nenhuma conta Google selecionada."
                    e.message?.contains("10") == true || e.message?.contains("DEVELOPER_ERROR") == true ->
                        "Configuração pendente do Google Sign-In (verifique a chave SHA-1 no Firebase)."
                    else -> e.message ?: "Falha ao autenticar com o Google. Tente novamente."
                }
                _loginError.value = message
                _registerError.value = message
            } finally {
                _isSubmittingGoogle.value = false
            }
        }
    }

    fun loginWithEmail(emailOrUsername: String, password: String) {
        _loginError.value = null
        _resendSuccess.value = false
        _showVerificationSent.value = false
        _isSubmitting.value = true

        viewModelScope.launch {
            try {
                val input = emailOrUsername.trim()
                if (input.isBlank() || password.isBlank()) {
                    _loginError.value = "Por favor, preencha todos os campos."
                    _isSubmitting.value = false
                    return@launch
                }

                val targetEmail = if (input.contains("@")) {
                    input
                } else {
                    val resolvedEmail = repository.getEmailByUsername(input)
                    if (resolvedEmail == null) {
                        _loginError.value = "Nome de usuário não encontrado. Verifique a digitação ou entre com seu e-mail."
                        _isSubmitting.value = false
                        return@launch
                    }
                    resolvedEmail
                }

                val result = auth.signInWithEmailAndPassword(targetEmail, password).await()
                val user = result.user

                if (user != null && !user.isEmailVerified) {
                    try {
                        user.sendEmailVerification().await()
                    } catch (_: Exception) { }
                    auth.signOut()
                    _loginError.value = "Confirmação de e-mail pendente.\nEnviamos um link de ativação para você.\n\nPor favor, confira sua caixa de entrada e a pasta de spam."
                    _showVerificationSent.value = true
                    _isSubmitting.value = false
                    return@launch
                }

                // Auth state listener will handle the rest
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("no user record") == true ||
                    e.message?.contains("password is invalid") == true ||
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                        "E-mail/usuário ou senha incorretos."
                    else -> e.message ?: "Ocorreu um erro na autenticação. Tente novamente."
                }
                _loginError.value = message
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun resendVerificationEmail(emailOrUsername: String, password: String) {
        _loginError.value = null
        _resendSuccess.value = false
        _isSubmitting.value = true

        viewModelScope.launch {
            try {
                val input = emailOrUsername.trim()
                val targetEmail = if (input.contains("@")) {
                    input
                } else {
                    repository.getEmailByUsername(input) ?: input
                }
                val result = auth.signInWithEmailAndPassword(targetEmail, password).await()
                result.user?.sendEmailVerification()?.await()
                auth.signOut()
                _resendSuccess.value = true
            } catch (e: Exception) {
                _loginError.value = "Não foi possível reenviar o e-mail de verificação. Verifique se a senha está correta."
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun registerWithEmail(
        displayName: String,
        username: String,
        email: String,
        password: String
    ) {
        _registerError.value = null
        _isSubmitting.value = true
        _registrationComplete.value = false

        val normalizedUsername = normalizeUsername(username)
        if (normalizedUsername.isEmpty()) {
            _registerError.value = "Escolha um nome de usuário válido."
            _isSubmitting.value = false
            return
        }

        viewModelScope.launch {
            try {
                // Check username uniqueness
                val isUnique = repository.checkUsernameUnique(normalizedUsername)
                if (!isUnique) {
                    _registerError.value = "Este nome de usuário já está em uso por outra pessoa."
                    _isSubmitting.value = false
                    return@launch
                }

                // Create Firebase Auth user
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    val resolvedName = displayName.trim().ifEmpty {
                        user.email?.substringBefore("@") ?: "Usuário"
                    }

                    // Update display name
                    try {
                        user.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(resolvedName)
                                .build()
                        ).await()
                    } catch (_: Exception) { }

                    // Register username and profile in Firestore
                    repository.registerUsername(user.uid, normalizedUsername, email, resolvedName)

                    // Send email verification
                    user.sendEmailVerification().await()

                    // Sign out immediately
                    auth.signOut()

                    _registrationComplete.value = true
                }
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("email address is already in use") == true ->
                        "Este endereço de e-mail já está cadastrado."
                    e.message?.contains("badly formatted") == true ->
                        "Por favor, informe um e-mail válido."
                    e.message?.contains("at least 6 characters") == true ||
                    e.message?.contains("WEAK_PASSWORD") == true ->
                        "A senha deve ter pelo menos 6 caracteres."
                    else -> e.message ?: "Ocorreu um erro ao criar sua conta. Tente novamente."
                }
                _registerError.value = message
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun resetRegistrationState() {
        _registrationComplete.value = false
        _registerError.value = null
    }

    fun clearLoginError() {
        _loginError.value = null
        _showVerificationSent.value = false
        _resendSuccess.value = false
    }

    fun sendPasswordResetEmail(emailOrUsername: String) {
        _passwordResetError.value = null
        _passwordResetSuccess.value = false
        _isSendingPasswordReset.value = true

        viewModelScope.launch {
            try {
                val input = emailOrUsername.trim()
                if (input.isBlank()) {
                    _passwordResetError.value = "Informe seu e-mail ou nome de usuário."
                    _isSendingPasswordReset.value = false
                    return@launch
                }

                val targetEmail = if (input.contains("@")) {
                    input
                } else {
                    val resolved = repository.getEmailByUsername(input)
                    if (resolved == null) {
                        _passwordResetError.value = "Nome de usuário não encontrado."
                        _isSendingPasswordReset.value = false
                        return@launch
                    }
                    resolved
                }

                auth.sendPasswordResetEmail(targetEmail).await()
                _passwordResetSuccess.value = true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Password reset failed", e)
                val msg = when {
                    e.message?.contains("no user record") == true ||
                    e.message?.contains("user-not-found") == true ->
                        "Nenhuma conta encontrada com este e-mail."
                    e.message?.contains("badly formatted") == true ||
                    e.message?.contains("invalid-email") == true ->
                        "Endereço de e-mail inválido."
                    else -> e.message ?: "Não foi possível enviar o e-mail de recuperação. Tente novamente."
                }
                _passwordResetError.value = msg
            } finally {
                _isSendingPasswordReset.value = false
            }
        }
    }

    fun clearPasswordResetState() {
        _isSendingPasswordReset.value = false
        _passwordResetError.value = null
        _passwordResetSuccess.value = false
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
