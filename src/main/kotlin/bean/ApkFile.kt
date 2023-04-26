package bean

data class ApkFile(
    val name: String,
    val path: ApkFileType,
    val size: Long,
    val compressSize: Long
)