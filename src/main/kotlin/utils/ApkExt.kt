package utils

import model.ApkFileType
import model.IApkFormatInfo
import model.Size
import java.io.File
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

fun File.createFileIfNoExists() {
    if (exists()) return
    if (!parentFile.exists()) {
        parentFile.mkdirs()
    }
    createNewFile()
}

fun MutableMap<ApkFileType, IApkFormatInfo>.diff(curMap: MutableMap<ApkFileType, IApkFormatInfo>): Map<ApkFileType, Size> {
    val map = mutableMapOf<ApkFileType, Size>()
    keys.forEach {
        val base = this[it]?.size ?: Size()
        val target = curMap[it]?.size ?: Size()
        map[it] = target - base
    }
    return map
}