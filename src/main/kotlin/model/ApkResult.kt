package model

import java.nio.file.Path

data class ApkResult(
    var baseMap: Map<ApkFileType, IApkFormatInfo>,
    var curMap: Map<ApkFileType, IApkFormatInfo>,
    var diffMap: Map<ApkFileType, Size>,
    var threshold: Map<ApkFileType, Size>,
    var diffOutPath: Path
)