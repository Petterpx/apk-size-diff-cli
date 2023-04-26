package model

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

data class ApkFile(
    val name: String,
    val path: ApkFileType,
    val size: Size,
    val compressSize: Size
)