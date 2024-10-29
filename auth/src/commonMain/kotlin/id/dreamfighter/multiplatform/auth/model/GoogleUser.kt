package id.dreamfighter.multiplatform.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class GoogleUser(
    @SerialName("id_token")
    val idToken: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("profile_pic_url")
    val profilePicUrl: String?)