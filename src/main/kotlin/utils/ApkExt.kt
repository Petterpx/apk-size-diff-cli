package utils

import bean.ApkFileType
import bean.ApkFormatInfo
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.zip.ZipEntry


fun ZipEntry.fileType(): ApkFileType {
    return when {
        name.endsWith(".dex") -> ApkFileType.DEX
        name.endsWith(".arsc") -> ApkFileType.ARSC
        name == "AndroidManifest.xml" -> ApkFileType.MANIFEST
        name.startsWith("lib/") -> ApkFileType.LIB
        name.startsWith("res/") -> ApkFileType.RES
        name.startsWith("assets/") -> ApkFileType.ASSETS
        name.startsWith("META-INF/") -> ApkFileType.META_INF
        else -> ApkFileType.OTHER
    }
}

fun Long.toKB(): String {
    var megaBytes = this.toDouble() / 1024
    var unit = " KB"
    if (megaBytes > 1000 || (megaBytes < 0 && megaBytes < -1000)) {
        megaBytes /= 1024
        unit = " M"
    }
    return "${BigDecimal(megaBytes).setScale(2, RoundingMode.HALF_UP).toDouble()} $unit"
}

fun File.createFileIfNoExists() {
    if (exists()) return
    if (!parentFile.exists()) {
        parentFile.mkdirs()
    }
    createNewFile()
}

fun MutableMap<ApkFileType, ApkFormatInfo>.diff(curMap: MutableMap<ApkFileType, ApkFormatInfo>): Map<ApkFileType, Long> {
    val map = mutableMapOf<ApkFileType, Long>()
    keys.forEach {
        val base = this[it]?.size() ?: 0L
        val target = curMap[it]?.size() ?: 0L
        map[it] = target - base
    }
    return map
}