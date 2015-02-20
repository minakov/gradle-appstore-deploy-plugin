package info.appsense.appstore.gradle.plugins.util

class TextUtils {

    static boolean same(String a, String b) {
        if (a != null && b != null) {
            return a.replaceAll("[\\n\\t ]", "").equals(b.replaceAll("[\\n\\t ]", ""))
        } else if (a == null && b == null) {
            return true
        }
        return false
    }
}
