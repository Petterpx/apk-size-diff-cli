import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.parameters.types.path
import model.ApkFileType
import model.IApkFormatInfo
import model.Size
import model.defaultThresholdSize
import java.nio.file.Path
import kotlin.io.path.notExists

class Main() : CliktCommand(help = "This is Apk Size Diff Utils") {

    private val pattern = Regex("^\\w+:\\d+$")

    init {
        versionOption("1.0")
    }

    private val baselineApkPath: Path by option(
        "--baseline_apk",
        "-b",
        help = "Baseline Apk Path"
    ).path(mustExist = true, mustBeReadable = true, canBeDir = false)
        .prompt("You need to enter the baseline apk path to continue")

    private val currentApkPath: Path by option("--current_apk", "-c", help = "Current Apk Path").path(
        mustExist = true,
        mustBeReadable = true,
        canBeDir = false
    ).prompt("You need to enter the current apk path to continue")

    private val diffOutputPath: Path by option("--diff_output", "-d", help = "Diff Output Path").path(
        mustExist = false,
        canBeDir = true,
        canBeFile = false
    ).prompt("You need to enter the diff output path to continue")

    private val threshold by option(
        "--threshold", "-t", help = "Apk threshold. Input example: apk:102400"
    ).multiple().check("Input Error. Check your input format is xx:Number, for example: apk:10000.") { it ->
        it.all {
            pattern.matches(it)
        }
    }

    private val thresholds by option(
        "--thresholds", "-ts", help = "Apk threshold. Input example: apk:102400,res:102400"
    ).check("Input Error. Check your input format is xx:Number, for example: apk:10000,res:102400") { it ->
        pattern.matches(it) || it.split(",").any {
            pattern.matches(it)
        }
    }

    private val basicThresholds by option(
        "--thresholdsBase", "-tss", help = "The basic threshold will be applied to all sub-levels."
    ).long().default(-1L)

    override fun run() {
        if (baselineApkPath == currentApkPath) {
            echo("Baseline Apk  and Current Apk  can't be the same")
            return
        }
        val thresholdMap = createThresholdConfig()
        if (diffOutputPath.notExists()) diffOutputPath.toFile().mkdirs()
        ApkExtractor.init(currentApkPath, baselineApkPath, diffOutputPath, thresholdMap).extract()
        echo("apk_size_diff -> success")
    }

    private fun createThresholdConfig(): Map<ApkFileType, Size> {
        val basicTs = if (basicThresholds != -1L) Size(basicThresholds) else defaultThresholdSize
        val thresholdList = mutableListOf<String>()
        val thresholdMap = mutableMapOf<ApkFileType, Size>().withDefault { basicTs }
        thresholds?.apply {
            if (pattern.matches(this)) thresholdList.add(this)
            else thresholdList.addAll(this.split(","))
        }
        thresholdList.addAll(threshold)
        thresholdList.forEach {
            val item = it.split(":")
            val key = item[0]
            val value = item[1].toLong()
            val fileType = when {
                ApkFileType.APK.name.contains(key, true) -> ApkFileType.APK
                ApkFileType.DEX.name.contains(key, true) -> ApkFileType.DEX
                ApkFileType.ARSC.name.contains(key, true) -> ApkFileType.ARSC
                ApkFileType.MANIFEST.name.contains(key, true) -> ApkFileType.MANIFEST
                ApkFileType.LIB.name.contains(key, true) -> ApkFileType.LIB
                ApkFileType.RES.name.contains(key, true) -> ApkFileType.RES
                ApkFileType.ASSETS.name.contains(key, true) -> ApkFileType.ASSETS
                ApkFileType.META_INF.name.contains(key, true) -> ApkFileType.META_INF
                else -> ApkFileType.OTHER
            }
            thresholdMap[fileType] = Size(value)
        }
        return thresholdMap
    }
}


fun main(args: Array<String>) = Main().main(args)
