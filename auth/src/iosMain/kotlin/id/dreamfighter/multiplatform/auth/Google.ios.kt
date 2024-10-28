package id.dreamfighter.multiplatform.auth

import cocoapods.GoogleSignIn.GIDSignIn
import id.dreamfighter.multiplatform.auth.model.GoogleUser
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIApplication


class GoogleIos : Google {
    val TAG = "GoogleAndroid"
    @OptIn(ExperimentalForeignApi::class)
    override fun auth(user: (GoogleUser) -> Unit, error: (Exception) -> Unit?) {
        val scope = MainScope()

        scope.launch {
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

            if (rootViewController == null) {
                error(Exception("Root view controller is null"))
            } else {
                GIDSignIn.sharedInstance
                    .signInWithPresentingViewController(rootViewController) { gidSignInResult, nsError ->
                        nsError?.let { println("Error While signing: $nsError") }

                        val gUser = gidSignInResult?.user
                        val idToken = gUser?.idToken?.tokenString
                        val accessToken = gUser?.accessToken?.tokenString
                        val profile = gidSignInResult?.user?.profile
                        if (idToken != null && accessToken != null) {
                            val googleUser = GoogleUser(
                                idToken = idToken,
                                accessToken = accessToken,
                                displayName = profile?.name ?: "",
                                profilePicUrl = profile?.imageURLWithDimension(320u)?.absoluteString
                            )
                            user(googleUser)
                        } else {
                            error(Exception("Error While signing: $nsError"))
                        }
                    }

            }
        }
    }


}
actual val google: Google = GoogleIos()