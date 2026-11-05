package ir.hatef.app

import org.junit.Assert.*
import org.junit.Test

class HatefUrlPolicyTest {
    @Test fun acceptsOnlyTrustedHttpsHosts() {
        assertTrue(HatefUrlPolicy.isTrustedUrl("https://hatef.ir/"))
        assertTrue(HatefUrlPolicy.isTrustedUrl("https://www.hatef.ir/profile"))
        assertFalse(HatefUrlPolicy.isTrustedUrl("http://hatef.ir"))
        assertFalse(HatefUrlPolicy.isTrustedUrl("https://hatef.ir.evil.test"))
        assertFalse(HatefUrlPolicy.isTrustedUrl("https://user@hatef.ir/"))
        assertFalse(HatefUrlPolicy.isTrustedUrl("javascript:alert(1)"))
        assertFalse(HatefUrlPolicy.isTrustedUrl("https://hatef.ir:8443/"))
    }

    @Test fun notificationPathsCannotRedirectToOtherOrigins() {
        assertEquals("https://hatef.ir/notifications", HatefUrlPolicy.destinationForPath("/notifications"))
        assertNull(HatefUrlPolicy.destinationForPath("//evil.test/path"))
        assertNull(HatefUrlPolicy.destinationForPath("https://evil.test"))
        assertNull(HatefUrlPolicy.destinationForPath("/\\evil.test"))
        assertNull(HatefUrlPolicy.destinationForPath(null))
        assertNull(HatefUrlPolicy.destinationForPath("/hello\nworld"))
    }
}
