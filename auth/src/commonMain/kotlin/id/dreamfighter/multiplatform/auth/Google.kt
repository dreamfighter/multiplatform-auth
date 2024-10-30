package id.dreamfighter.multiplatform.auth

import id.dreamfighter.multiplatform.auth.model.GoogleUser


interface Google {
    fun auth(user:(GoogleUser)->Unit,error: (Exception) -> Unit? = {})
    fun authCode(code: (String)->Unit,error: (Exception) -> Unit? = {})
}

expect val google: Google