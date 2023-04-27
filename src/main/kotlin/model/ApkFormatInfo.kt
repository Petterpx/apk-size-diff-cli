package model

import java.math.BigDecimal
import java.math.RoundingMode

const val K = 1024L
const val M = K * K
const val DECIMAL = 2

@JvmInline
value class Size(private val size: Long = 0L) {
    val kb: String
        get() = "${BigDecimal(size.toDouble() / K).setScale(DECIMAL, RoundingMode.HALF_UP).toDouble()} KB"
    val mb: String
        get() = "${BigDecimal(size.toDouble() / M).setScale(DECIMAL, RoundingMode.HALF_UP).toDouble()} MB"
    val unit: String
        get() {
            return if (size > M || size < -M) mb
            else kb
        }

    operator fun plus(other: Size): Size = Size(other.size + this.size)

    operator fun minus(other: Size): Size = Size(this.size - other.size)

    fun beyondSize(base: Size?): ResultDiffEnum {
        if (this.size < 0) return ResultDiffEnum.Decrease
        if (base == null) return ResultDiffEnum.Keep
        return if (this.size > base.size) {
            ResultDiffEnum.Deterioration
        } else {
            ResultDiffEnum.Keep
        }
    }

}


sealed interface IApkFormatInfo {
    var size: Size

    data class Apk(val name: String, override var size: Size, val compressSize: Size) : IApkFormatInfo
    data class Basic(val child: MutableList<ApkFile>, override var size: Size, var compressSize: Size) : IApkFormatInfo

    fun addFile(file: ApkFile) {
        if (this is Basic) {
            this.child.add(file)
            this.size = this.size + file.size
            this.compressSize = this.size + file.compressSize
        }
    }

}