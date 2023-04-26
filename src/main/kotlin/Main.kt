import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import kotlin.io.path.notExists

class Main : CliktCommand(help = "This is Apk Size Diff Utils") {

    init {
        versionOption("1.0")
    }

    private val baselineApkPath: Path by option("--baseline_apk", "-b", help = "Baseline Apk Path")
        .path(mustExist = true, mustBeReadable = true, canBeDir = false)
        .prompt("You need to enter the baseline apk path to continue")

    private val currentApkPath: Path by option("--current_apk", "-c", help = "Current Apk Path")
        .path(mustExist = true, mustBeReadable = true, canBeDir = false)
        .prompt("You need to enter the current apk path to continue")

    private val diffOutputPath: Path by option("--diff_output", "-d", help = "Diff Output Path")
        .path(mustExist = false, canBeDir = true, canBeFile = false)
        .prompt("You need to enter the diff output path to continue")


    override fun run() {
        if (baselineApkPath == currentApkPath) {
            echo("Baseline Apk  and Current Apk  can't be the same")
            return
        }
        if (baselineApkPath.notExists()) baselineApkPath.toFile().mkdirs()
        ApkExtractor.init(baselineApkPath, currentApkPath, diffOutputPath).extract()
    }
}


fun main(args: Array<String>) = Main().main(args)
