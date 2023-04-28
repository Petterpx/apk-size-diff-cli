package utils

import model.ApkFileType
import model.IApkFormatInfo
import model.ResultDiffEnum
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

fun File.createFileIfNoExists(): File {
    if (exists()) return this
    if (!parentFile.exists()) {
        parentFile.mkdirs()
    }
    createNewFile()
    return this
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

fun Map<ApkFileType, Size>.diffThreshold(tMap: Map<ApkFileType, Size>): Map<ApkFileType, ResultDiffEnum> {
    val map = mutableMapOf<ApkFileType, ResultDiffEnum>().withDefault {
        ResultDiffEnum.UnKnown
    }
    forEach {
        val result = it.value.diffResult(tMap.getValue(it.key))
        map[it.key] = result
    }
    return map
}