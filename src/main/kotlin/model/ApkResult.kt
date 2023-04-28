package model

import java.nio.file.Path

data class ApkResult(
    val baseMap: Map<ApkFileType, IApkFormatInfo>,
    val curMap: Map<ApkFileType, IApkFormatInfo>,
    val diffMap: Map<ApkFileType, Size>,
    val tMap: Map<ApkFileType, ResultDiffEnum>,
    val diffOutPath: Path,
    var isBeyondThreshold: Boolean = false
)