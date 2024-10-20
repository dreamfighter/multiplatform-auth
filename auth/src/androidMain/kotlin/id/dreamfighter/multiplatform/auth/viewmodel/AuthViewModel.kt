package id.dreamfighter.multiplatform.auth.viewmodel

import android.app.Application
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val application: Application) : AndroidViewModel(application) {
    private val credentialManager = CredentialManager.create(application)

    fun signIn(request: GetCredentialRequest) {
        viewModelScope.launch {
            // Use the application context here
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = application,
                )
                //handleSignIn(result)
            } catch (e: GetCredentialException) {
                //handleFailure(e)
            }
        }
    }
}