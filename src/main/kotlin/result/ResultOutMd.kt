package result

import model.ApkFileType
import model.IApkFormatInfo
import model.Size
import utils.createFileIfNoExists
import utils.diff
import utils.mdHeader
import utils.mdTable
import java.io.File
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

/**
 * Out apk result save to md file.
 * @author petterp
 */
class ResultOutMd : ResultHelper() {

    override fun start() {
        val diffMap = baseMap.diff(curMap)
        var path = diffOutPath.pathString
        if (diffOutPath.isDirectory()) {
            path += "/apk_size_diff.md"
        }
        val file = File(path)
        file.createFileIfNoExists()
        file.outputStream().use {
            val builder = StringBuilder()
            builder.mdHeader(1, "Apk Size Diff")
            builder.mdTable(
                listOf(
                    "Metric",
                    "Base Apk",
                    "Target Apk",
                    "Diff",
                ),
                addMdList(ApkFileType.APK, baseMap, curMap, diffMap),
                addMdList(ApkFileType.DEX, baseMap, curMap, diffMap),
                addMdList(ApkFileType.RES, baseMap, curMap, diffMap),
                addMdList(ApkFileType.LIB, baseMap, curMap, diffMap),
                addMdList(ApkFileType.ARSC, baseMap, curMap, diffMap),
                addMdList(ApkFileType.OTHER, baseMap, curMap, diffMap),
            )
            it.write(builder.toString().toByteArray())
        }
    }

    private fun addMdList(
        fileType: ApkFileType,
        baseMap: Map<ApkFileType, IApkFormatInfo>,
        outMap: Map<ApkFileType, IApkFormatInfo>,
        diffMap: Map<ApkFileType, Size>,
    ) = listOf(
        "${fileType.title} Size",
        "${baseMap[fileType]?.size?.unit}",
        "${outMap[fileType]?.size?.unit}",
        "${diffMap[fileType]?.unit}",
    )
}