package id.dreamfighter.multiplatform.auth

import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import id.dreamfighter.multiplatform.auth.model.AccountService
import id.dreamfighter.multiplatform.auth.model.GoogleUser
import id.dreamfighter.multiplatform.auth.utils.FileUtil
import id.dreamfighter.multiplatform.auth.utils.FileUtil.toObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.UUID

class GoogleAndroid : Google {
    val TAG = "GoogleAndroid"
    override fun auth(user: (GoogleUser) -> Unit, error: (Exception) -> Unit?) {
        val json = FileUtil.readFile("google-services.json")
        val accountService: AccountService = json.toObject<AccountService>()
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(accountService.clientId)
            .setAutoSelectEnabled(false)
            .setNonce(UUID.randomUUID().toString())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val scope = MainScope()

        scope.launch {
            val context = applicationContext
            val credentialManager = CredentialManager.create(context)

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result,user)
            } catch (e: GetCredentialException) {
                e.printStackTrace()
                error(e)
                //handleFailure(e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse,user: (GoogleUser) -> Unit) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val googleUser = GoogleUser(
                            idToken = googleIdTokenCredential.idToken,
                            accessToken = googleIdTokenCredential.idToken,
                            displayName = googleIdTokenCredential.displayName ?: "",
                            profilePicUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        )
                        user(googleUser)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    override fun authCode(code: (String) -> Unit, error: (Exception) -> Unit?) {
        TODO("Not yet implemented")
    }
}

actual val google: Google = GoogleAndroid()