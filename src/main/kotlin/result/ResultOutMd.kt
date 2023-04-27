package result

import model.ApkFileType
import model.IApkFormatInfo
import model.Size
import utils.createFileIfNoExists
import utils.diff
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
        if (diffMap[ApkFileType.APK]?.beyondSize(10024) == true) {
            error("已超出边界")
        }
    }

    private fun addMdList(
        fileType: ApkFileType,
        baseMap: Map<ApkFileType, IApkFormatInfo>,
        outMap: Map<ApkFileType, IApkFormatInfo>,
        diffMap: Map<ApkFileType, Size>,
    ) = listOf(
        "${fileType.title} Size",
        "${baseMap[fileType]?.size?.unit ?: 0}",
        "${outMap[fileType]?.size?.unit ?: 0}",
        "${diffMap[fileType]?.unit ?: 0}",
    )
}