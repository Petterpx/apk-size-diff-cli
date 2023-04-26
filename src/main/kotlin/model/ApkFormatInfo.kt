package model

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Size(private val size: Long) {
    val kb: String
        get() = "${BigDecimal(size.toDouble() / 1024).setScale(2, RoundingMode.HALF_UP).toDouble()} KB"
    val mb: String
        get() = "${BigDecimal(size.toDouble() / 1024 / 1024).setScale(2, RoundingMode.HALF_UP).toDouble()} MB"

    operator fun plus(other: Size): Size = Size(other.size + this.size)

    operator fun minus(other: Size): Size = Size(this.size - other.size)
}


sealed interface IApkFormatInfo {
    var size: Size

    data class Apk(val name: String, override var size: Size) : IApkFormatInfo
    data class Basic(val child: MutableList<ApkFile>, override var size: Size, var compressSize: Size) : IApkFormatInfo

    fun addFile(file: ApkFile) {
        if (this is Basic) {
            this.child.add(file)
            this.size = this.size + file.size
            this.compressSize = this.size + file.compressSize
        }
    }

}