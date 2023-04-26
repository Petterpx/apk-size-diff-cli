package result

import model.ApkFileType
import model.IApkFormatInfo
import java.nio.file.Path

/**
 * Apk Result Out Helper
 * @author petterp
 */
abstract class ResultHelper {
    lateinit var baseMap: MutableMap<ApkFileType, IApkFormatInfo>
    lateinit var curMap: MutableMap<ApkFileType, IApkFormatInfo>
    lateinit var diffOutPath: Path

    abstract fun start()

    companion object {
        fun outPutMd(init: ResultHelper.() -> Unit) = ResultOutMd().apply(init)
    }
}