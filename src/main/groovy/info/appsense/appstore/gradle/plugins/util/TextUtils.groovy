package info.appsense.appstore.gradle.plugins.util

class TextUtils {

    static boolean same(String one, String two) {
        return one.replaceAll("[\\n\\t ]", "").equals(two.replaceAll("[\\n\\t ]", ""))
    }
}
