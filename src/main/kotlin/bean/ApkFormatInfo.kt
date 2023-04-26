package bean

import utils.toKB

sealed class ApkFormatInfo {
    data class Apk(val name: String, val downloadSize: Long) : ApkFormatInfo()
    data class Basic(var size: Long, val child: MutableList<ApkFile>) : ApkFormatInfo()

    fun addFile(file: ApkFile) {
        if (this is Basic) {
            this.child.add(file)
            this.size += file.size
        }
    }

    fun size(): Long {
        return when (this) {
            is Apk -> {
                downloadSize
            }

            is Basic -> {
                size
            }
        }
    }


    fun echo(): String {
        val sb = StringBuilder()
        when (this) {
            is Apk -> {
                sb.append("name:$name,").append("downloadSize:${downloadSize.toKB()},")
            }

            is Basic -> {
                sb.append("size:$size},format:${size.toKB()},sum:${child.size}")
            }
        }
        return sb.toString()
    }
}