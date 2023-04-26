import bean.ApkFile
import bean.ApkFileType
import bean.ApkFormatInfo
import utils.*
import java.io.File
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

class ApkExtractor private constructor() {
    lateinit var baselineApkPath: Path
    lateinit var currentApkPath: Path
    lateinit var diffOutputPath: Path

    fun extract() {
        val baseMap = analysis(baselineApkPath)
        val curMap = analysis(currentApkPath)
        outPutMd(baseMap, curMap)
    }

    private fun outPutMd(
        baseMap: MutableMap<ApkFileType, ApkFormatInfo>,
        outMap: MutableMap<ApkFileType, ApkFormatInfo>,
    ) {
        val diffMap = baseMap.diff(outMap)
        var path = diffOutputPath.pathString
        if (diffOutputPath.isDirectory()) {
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
                addMdList(ApkFileType.APK, baseMap, outMap, diffMap),
                addMdList(ApkFileType.DEX, baseMap, outMap, diffMap),
                addMdList(ApkFileType.ARSC, baseMap, outMap, diffMap),
                addMdList(ApkFileType.LIB, baseMap, outMap, diffMap),
                addMdList(ApkFileType.OTHER, baseMap, outMap, diffMap),
            )
            it.write(builder.toString().toByteArray())
        }
    }

    private fun addMdList(
        fileType: ApkFileType,
        baseMap: Map<ApkFileType, ApkFormatInfo>,
        outMap: Map<ApkFileType, ApkFormatInfo>,
        diffMap: Map<ApkFileType, Long>,
    ) = listOf(
        "${fileType.title} Size",
        "${baseMap[fileType]?.size()?.toKB()}",
        "${outMap[fileType]?.size()?.toKB()}",
        "${diffMap[fileType]?.toKB()}",
    )

    private fun analysis(path: Path): MutableMap<ApkFileType, ApkFormatInfo> {
        val map = mutableMapOf<ApkFileType, ApkFormatInfo>()
        val file = path.toFile()
        val zipFile = ZipFile(file)
        var apkSize = 0L
        zipFile.stream().forEach { entry ->
            val fileType = entry.fileType()
            apkSize += entry.compressedSize
            val apkFile = ApkFile(entry.name, fileType, entry.size, entry.compressedSize)
            val apkFormatInfo = map.getOrDefault(fileType, ApkFormatInfo.Basic(0, mutableListOf()))
            apkFormatInfo.addFile(apkFile)
            map[fileType] = apkFormatInfo
        }
        map[ApkFileType.APK] = ApkFormatInfo.Apk(file.name, apkSize)
        return map
    }

    companion object {
        fun init(baselineApkPath: Path, currentApkPath: Path, diffOutputPath: Path): ApkExtractor {
            val apkExtractor = ApkExtractor()
            apkExtractor.baselineApkPath = baselineApkPath
            apkExtractor.currentApkPath = currentApkPath
            apkExtractor.diffOutputPath = diffOutputPath
            return apkExtractor
        }
    }
}