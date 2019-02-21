package com.snail.commons.utils

/**
 * Created by zeng on 2016/8/30.
 * 版本号工具
 */
object VersionUtils {
    /**
     * 抽取版本号。如果是xxx1.2.3 Rev 456之类的，只对1.2.3进行抽取
     */
    fun extractVer(version: String): String {
        var ver: String = version
        if (ver.contains(" ")) {
            ver = ver.substring(0, ver.indexOf(" "))
        }
        ver = ver.replace("^\\D+".toRegex(), "")
        return ver
    }

    /**
     * 将数字抽取出来。如果是xxx1.2.3 Rev 456之类的，只对1.2.3进行抽取
     */
    fun splitVer(version: String): Array<String> {
        var ver = version
        ver = extractVer(ver)
        return ver.split("\\D+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * 递归比较大小
     * @param index 从第几个元素开始比较
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    private fun compare(ver1: Array<String>?, ver2: Array<String>?, index: Int): Int {
        try {
            val a = Integer.valueOf(ver1!![index])
            val b = Integer.valueOf(ver2!![index])
            return if (a == b) {
                if (ver1.size - 1 == index || ver2.size - 1 == index) {
                    0
                } else {
                    compare(ver1, ver2, index + 1)
                }
            } else {
                a - b
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 比较版本号大小
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    fun compareVersion(ver1: String, ver2: String): Int {
        return compare(splitVer(ver1), splitVer(ver2), 0)
    }
}
