import model.*
import result.ResultHelper
import utils.*
import java.nio.file.Path
import java.util.zip.ZipFile


class ApkExtractor private constructor() {
    lateinit var baselineApkPath: Path
    lateinit var currentApkPath: Path
    lateinit var diffOutputPath: Path
    lateinit var threshold: Map<ApkFileType, Size>

    fun extract() {
        val baseMap = analysis(baselineApkPath)
        val curMap = analysis(currentApkPath)
        val diffMap = baseMap.diff(curMap)
        val result = ApkResult(baseMap, curMap, diffMap, threshold, diffOutputPath)
        ResultHelper.outPutMd(result).start()
        //...more
    }

    private fun analysis(path: Path): MutableMap<ApkFileType, IApkFormatInfo> {
        val map = mutableMapOf<ApkFileType, IApkFormatInfo>()
        val file = path.toFile()
        val zipFile = ZipFile(file)
        var apkSize = 0L
        zipFile.stream().forEach { entry ->
            val fileType = entry.fileType()
            apkSize += entry.compressedSize
            val apkFile = ApkFile(entry.name, fileType, Size(entry.size), Size(entry.compressedSize))
            val apkFormatInfo = map.getOrDefault(fileType, IApkFormatInfo.Basic(mutableListOf(), Size(), Size()))
            apkFormatInfo.addFile(apkFile)
            map[fileType] = apkFormatInfo
        }
        map[ApkFileType.APK] = IApkFormatInfo.Apk(file.name, Size(file.length()), Size(apkSize))
        return map
    }

    companion object {
        fun init(
            baselineApkPath: Path,
            currentApkPath: Path,
            diffOutputPath: Path,
            threshold: Map<ApkFileType, Size>
        ): ApkExtractor {
            val apkExtractor = ApkExtractor()
            apkExtractor.baselineApkPath = baselineApkPath
            apkExtractor.currentApkPath = currentApkPath
            apkExtractor.diffOutputPath = diffOutputPath
            apkExtractor.threshold = threshold
            return apkExtractor
        }
    }
}