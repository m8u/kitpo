package src.main.dev.m8u.kitpo

import src.main.dev.m8u.kitpo.builders.MyDatetimeBuilder
import src.main.dev.m8u.kitpo.builders.MyDoubleBuilder
import src.main.dev.m8u.kitpo.builders.MyHashableBuilder

internal enum class MyHashmapStorable {
    MyDouble, MyDatetime
}

object TypeFactory {
    val typeNames: ArrayList<String>
        get() {
            val list = ArrayList<String>()
            for (type in MyHashmapStorable.values()) {
                list.add(type.toString())
            }
            return list
        }

    fun getBuilderByName(name: String): MyHashableBuilder? {
        if ("MyDouble" == name) {
            return MyDoubleBuilder()
        } else if ("MyDatetime" == name) {
            return MyDatetimeBuilder()
        }
        return null
    }
}