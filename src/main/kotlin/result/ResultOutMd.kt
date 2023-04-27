package result

import model.ApkFileType
import model.IApkFormatInfo
import model.ResultDiffEnum
import model.Size
import utils.*
import java.awt.Color
import java.io.File
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

/**
 * Out apk result save to md file.
 * @author petterp
 */
class ResultOutMd : ResultHelper() {

    var errorSum = 0
    override fun start() {
        val diffOutPath = result.diffOutPath
        var path = diffOutPath.pathString
        if (diffOutPath.isDirectory()) {
            path += "/apk_size_diff.md"
        }
        val isError =
            result.diffMap[ApkFileType.APK]?.beyondSize(result.threshold[ApkFileType.APK]) == ResultDiffEnum.Deterioration
        File(path).createFileIfNoExists().outputStream().use {
            val builder = StringBuilder()
            builder.mdHeader(4, "Apk Size Diff Analysis 🧩")
            builder.mdTable(
                listOf(
                    "Metric",
                    "Base Apk",
                    "Target Apk",
                    "Diff",
                ),
                addMdList(ApkFileType.APK),
                addMdList(ApkFileType.DEX),
                addMdList(ApkFileType.RES),
                addMdList(ApkFileType.LIB),
                addMdList(ApkFileType.ARSC),
                addMdList(ApkFileType.OTHER),
            )
            if (isError) builder.mdReference("本次扫描未通过，包大小超出限定阈值，请检查你的改动代码。")
            it.write(builder.toString().toByteArray())
        }
        if (isError) error("本次扫描未通过，本次包大小超出限定阈值，请检查你的改动代码。")
    }

    private fun addMdList(fileType: ApkFileType): List<String> {
        return listOf(
            fileType.title,
            "${result.baseMap[fileType]?.size?.unit ?: 0}",
            "${result.curMap[fileType]?.size?.unit ?: 0}",
            "${result.diffMap[fileType]?.unit ?: 0}".addText(null, true),
        )
    }
}