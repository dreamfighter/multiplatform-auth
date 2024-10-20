import id.dreamfighter.multiplatform.auth.toCurrency
import kotlin.test.Test
import kotlin.test.assertEquals

class IosCurrencyTest {

    @Test
    fun `test double to curreny`() {
        assertEquals("IDR 1.000,0", 1000.0.toCurrency("IDR"))
    }
}