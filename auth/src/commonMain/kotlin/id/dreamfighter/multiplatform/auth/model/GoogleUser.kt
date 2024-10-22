package id.dreamfighter.multiplatform.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class GoogleUser(val idToken: String, val accessToken: String, val displayName: String, val profilePicUrl: String?)