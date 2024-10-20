import id.dreamfighter.multiplatform.auth.Google
import id.dreamfighter.multiplatform.auth.google
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidGoogleTest {

    @Test
    fun `test google auth`() {
        google.auth { _ -> }
        //assertEquals("IDR1,000.00", 1000.0.toCurrency("IDR"))
    }
}