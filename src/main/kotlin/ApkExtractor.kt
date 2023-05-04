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
        val result = createApkResult()
        ResultHelper.outPutMd(result).start()
        //..more
        if (result.isBeyondThreshold) error("本次扫描未通过，包大小已超出限定阈值，请检查你的改动代码。")
    }

    private fun createApkResult(): ApkResult {
        val baseMap = analysis(baselineApkPath)
        val curMap = analysis(currentApkPath)
        val diffMap = baseMap.diff(curMap)
        val tMap = diffMap.diffThreshold(threshold)
        val isError = tMap.any {
            it.value == ResultDiffEnum.ExceededThreshold
        }
        return ApkResult(baseMap, curMap, diffMap, tMap, diffOutputPath, isError)
    }

    private fun analysis(path: Path): MutableMap<ApkFileType, IApkFormatInfo> {
        val map = mutableMapOf<ApkFileType, IApkFormatInfo>()
        val file = path.toFile()
        val zipFile = ZipFile(file)
        var apkSize = 0L
        zipFile.stream().forEach { entry ->
            val fileType = entry.fileType()
            apkSize += entry.size
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