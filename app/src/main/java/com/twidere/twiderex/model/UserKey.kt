package com.twidere.twiderex.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class UserKey(
    val id: String,
    val host: String,
) : Parcelable {


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + host.hashCode()
        return result
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val (id1, host1) = o as UserKey
        return if (id != id1) false else !if (host.isNotEmpty()) host != host1 else host1.isNotEmpty()
    }

    override fun toString(): String {
        return if (host.isNotEmpty()) escapeText(id).toString() + "@" + escapeText(host) else id
    }

    private fun escapeText(host: String): String? {
        val sb = java.lang.StringBuilder()
        var i = 0
        val j = host.length
        while (i < j) {
            val ch = host[i]
            if (isSpecialChar(ch)) {
                sb.append('\\')
            }
            sb.append(ch)
            i++
        }
        return sb.toString()
    }


    private fun isSpecialChar(ch: Char): Boolean {
        return ch == '\\' || ch == '@' || ch == ','
    }


    companion object {
        fun valueOf(str: String): UserKey {
            var escaping = false
            var idFinished = false
            val idBuilder = StringBuilder(str.length)
            val hostBuilder = StringBuilder(str.length)
            var i = 0
            val j = str.length
            while (i < j) {
                val ch = str[i]
                var append = false
                if (escaping) {
                    // accept all characters if is escaping
                    append = true
                    escaping = false
                } else if (ch == '\\') {
                    escaping = true
                } else if (ch == '@') {
                    idFinished = true
                } else if (ch == ',') {
                    // end of item, just jump out
                    break
                } else {
                    append = true
                }
                if (append) {
                    if (idFinished) {
                        hostBuilder.append(ch)
                    } else {
                        idBuilder.append(ch)
                    }
                }
                i++
            }
            return if (hostBuilder.isNotEmpty()) {
                UserKey(idBuilder.toString(), hostBuilder.toString())
            } else {
                UserKey(idBuilder.toString(), "")
            }
        }
    }
}