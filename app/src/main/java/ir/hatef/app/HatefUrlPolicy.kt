package ir.hatef.app

import java.net.URI

/** Only trusted HTTPS origins can render inside our privileged WebView. */
object HatefUrlPolicy {
    const val HOME_URL = "https://hatef.ir/"

    fun isTrustedUrl(value: String): Boolean = try {
        val uri = URI(value)
        uri.scheme.equals("https", ignoreCase = true) &&
            uri.host?.lowercase() in setOf("hatef.ir", "www.hatef.ir") &&
            uri.rawUserInfo == null &&
            (uri.port == -1 || uri.port == 443)
    } catch (_: Exception) {
        false
    }

    /** FCM navigation accepts only local paths, never remote or scheme-relative URLs. */
    fun destinationForPath(path: String?): String? {
        if (path.isNullOrBlank() || !path.startsWith("/") || path.startsWith("//") ||
            path.contains('\\') || path.any { it.code < 32 }) return null
        return try {
            val parsed = URI(path)
            if (parsed.isAbsolute || parsed.rawAuthority != null) null
            else URI(HOME_URL).resolve(parsed).toString().takeIf(::isTrustedUrl)
        } catch (_: Exception) {
            null
        }
    }
}
