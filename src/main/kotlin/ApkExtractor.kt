import model.ApkFile
import model.ApkFileType
import model.IApkFormatInfo
import model.Size
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
        baseMap: MutableMap<ApkFileType, IApkFormatInfo>,
        outMap: MutableMap<ApkFileType, IApkFormatInfo>,
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
        baseMap: Map<ApkFileType, IApkFormatInfo>,
        outMap: Map<ApkFileType, IApkFormatInfo>,
        diffMap: Map<ApkFileType, Size>,
    ) = listOf(
        "${fileType.title} Size",
        "${baseMap[fileType]?.size?.kb}",
        "${outMap[fileType]?.size?.kb}",
        "${diffMap[fileType]?.kb}",
    )

    private fun analysis(path: Path): MutableMap<ApkFileType, IApkFormatInfo> {
        val map = mutableMapOf<ApkFileType, IApkFormatInfo>()
        val file = path.toFile()
        val zipFile = ZipFile(file)
        var apkSize = 0L
        zipFile.stream().forEach { entry ->
            val fileType = entry.fileType()
            apkSize += entry.compressedSize
            val apkFile = ApkFile(entry.name, fileType, Size(entry.size), Size(entry.compressedSize))
            val apkFormatInfo = map.getOrDefault(fileType, IApkFormatInfo.Basic(mutableListOf(), Size(0), Size(0)))
            apkFormatInfo.addFile(apkFile)
            map[fileType] = apkFormatInfo
        }
        map[ApkFileType.APK] = IApkFormatInfo.Apk(file.name, Size(apkSize))
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