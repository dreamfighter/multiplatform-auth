import id.dreamfighter.multiplatform.auth.toCurrency
import kotlin.test.Test
import kotlin.test.assertEquals

class FibiTest {

    @Test
    fun test3rdelement() {
        assertEquals(firstElement + secondElement, fibi.take(3).last())
    }

    @Test
    fun testDoubleToCurreny() {
        //printHelloWorld()
        assertEquals("Rp1.000,0", 1000.0.toCurrency("IDR"))
    }
}