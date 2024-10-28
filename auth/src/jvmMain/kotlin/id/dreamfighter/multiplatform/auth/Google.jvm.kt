package id.dreamfighter.multiplatform.auth

import id.dreamfighter.multiplatform.auth.model.AccountService
import id.dreamfighter.multiplatform.auth.model.GoogleUser
import id.dreamfighter.multiplatform.auth.utils.FileUtil
import id.dreamfighter.multiplatform.auth.utils.FileUtil.toObject

class GoogleJvm : Google {
    override fun auth(user: (GoogleUser) -> Unit, error: (Exception) -> Unit?) {
        val json = FileUtil.readFile("google-services.json")
        println("json $json")
        json?.let {
            val accountService: AccountService = json.toObject<AccountService>()

            val rt = Runtime.getRuntime()
            val clientId = ""
            val redirectUri = "http://localhost:9002/auth"
            val responseType = "code"
            val state = "auth"
            val scope = "https://www.googleapis.com/auth/chat.spaces"
            val url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&state=${state}&scope=${scope}"
            val browsers = arrayOf(
                "google-chrome", "firefox", "mozilla", "epiphany", "konqueror",
                "netscape", "opera", "links", "lynx"
            )

            val cmd = StringBuffer()
            for (i in browsers.indices) if (i == 0) cmd.append(
                String.format(
                    "%s \"%s\"",
                    browsers[i],
                    url
                )
            )
            else cmd.append(String.format(" || %s \"%s\"", browsers[i], url))


            // If the first didn't work, try the next browser and so on
            rt.exec(arrayOf("sh", "-c", cmd.toString()))
        }

    }
}
actual val google: Google = GoogleJvm()