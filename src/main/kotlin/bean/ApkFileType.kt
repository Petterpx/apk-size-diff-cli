package bean

enum class ApkFileType(val title: String) {
    APK("Apk"),
    DEX("Dex"),
    ARSC("Arsc"),
    MANIFEST("Manifest"),
    LIB("Lib"),
    RES("Res"),
    ASSETS("Assets"),
    META_INF("META_INF"),
    OTHER("Other")
}