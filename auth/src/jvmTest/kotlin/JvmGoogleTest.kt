import id.dreamfighter.multiplatform.auth.utils.FileUtil
import kotlin.test.Test

class JvmGoogleTest {

    @Test
    fun `test 3rd element`() {
        //assertEquals(5, fibi.take(3).last())
        FileUtil.readFile("google-services.json")
    }
}