package result

import model.ApkResult

/**
 * Apk Result Out Helper
 * @author petterp
 */
abstract class ResultHelper {
    protected lateinit var result: ApkResult

    abstract fun start()

    companion object {
        fun outPutMd(result: ApkResult) = ResultOutMd().apply {
            this.result = result
        }
    }
}