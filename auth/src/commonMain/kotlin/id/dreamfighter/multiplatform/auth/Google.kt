package id.dreamfighter.multiplatform.auth


interface Google {
    fun auth(code:(String)->Unit,error: (Exception) -> Unit? = {})
}

expect val google: Google