package src.main.dev.m8u.kitpo

import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import kotlin.math.abs

class ChainedHashtable(val keyTypeName: String) : Iterable<chain>, expandable {
    private var map: Vector<chain>

    init {
        map = Vector()
        map.setSize(INITIAL_SIZE)
        for (i in 0 until INITIAL_SIZE) {
            map[i] = chain()
        }
    }

    operator fun set(key: Any, value: Any?) {
        val index = abs(key.hashCode()) % map.size
        map[index][key, value] = this
    }

    operator fun get(key: Any): Any? {
        val index = key.hashCode() % map.size
        return map[index][key]
    }

    fun remove(key: Any): Any? {
        val index = key.hashCode() % map.size
        return map[index].remove(key)
    }

    override fun expand() {
        val old = map
        map = Vector()
        map.setSize(old.size * 2)
        for (i in map.indices) {
            map[i] = chain()
        }
        for (chain in old) {
            for (entry in chain) {
                this[entry.key] = entry.value
            }
        }
    }

    val capacity: Int
        get() = map.size

    override fun iterator(): Iterator<chain> {
        return object : MutableIterator<chain> {
            var i = 0
            override fun hasNext(): Boolean {
                return i < map.size
            }

            override fun next(): chain {
                return map[i++]
            }

            override fun remove() {}
        }
    }

    override fun toString(): String {
        return map.toString()
    }

    override fun forEach(action: Consumer<in chain>?) {
        super.forEach(action)
    }

    @Throws(IOException::class)
    fun saveAsJSON(fos: FileOutputStream) {
        var json = "{\"keyType\": \"%s\",\"hashtable\": %s}"
        val jsonHashtable = JSONObject()
        jsonHashtable.put("size", capacity)
        val jsonHashtableData = JSONArray()
        for (chain in this) {
            val jsonChain = JSONObject()
            val jsonChainData = JSONArray()
            var nodeCount = 0
            for (node in chain) {
                val jsonNode = JSONObject()
                jsonNode.put("key", node.key.toString())
                jsonNode.put("value", node.value.toString())
                jsonChainData.put(jsonNode)
                nodeCount++
            }
            jsonChain.put("size", nodeCount)
            jsonChain.put("data", jsonChainData)
            jsonHashtableData.put(jsonChain)
        }
        jsonHashtable.put("data", jsonHashtableData)
        json = String.format(json, keyTypeName, jsonHashtable)
        fos.write(json.toByteArray())
    }

    val averageChainLength: Double
        get() {
            var avgChainLength = 0.0
            var length: Int
            for (c in this) {
                length = 0
                for (ignored in c) length++
                avgChainLength += length.toDouble()
            }
            avgChainLength /= capacity.toDouble()
            return avgChainLength
        }
    val occupancy: Double
        get() {
            var nonEmptyCount = 0.0
            for (c in this) for (ignored in c) {
                nonEmptyCount++
                break
            }
            return nonEmptyCount / capacity
        }

    companion object {
        private const val INITIAL_SIZE = 4
        @Throws(Exception::class)
        fun loadFromJSON(fis: FileInputStream): ChainedHashtable {
            val bytes = fis.readAllBytes()
            val json = String(bytes, StandardCharsets.UTF_8)
            val jsonObject = JSONObject(json)
            val keyTypeName = jsonObject["keyType"].toString()
            val keyBuilder = TypeFactory.getBuilderByName(keyTypeName)
            val hashtable = ChainedHashtable(keyTypeName)
            val jsonHashtable = jsonObject.getJSONObject("hashtable")
            val size = jsonHashtable.getInt("size")
            val jsonHashtableData = jsonHashtable.getJSONArray("data")
            for (i in 0 until size) {
                val jsonChain = jsonHashtableData.getJSONObject(i)
                val nodesCount = jsonChain.getInt("size")
                val jsonChainData = jsonChain.getJSONArray("data")
                for (j in 0 until nodesCount) {
                    val jsonNode = jsonChainData.getJSONObject(j)
                    hashtable[keyBuilder!!.parse(jsonNode["key"] as String)] = jsonNode["value"]
                }
            }
            return hashtable
        }
    }
}

class chain : Iterable<chainNode> {
    var head: chainNode? = null
    operator fun set(key: Any, value: Any?, hashmap: expandable) {
        if (head == null) {
            head = chainNode(key, value)
            return
        }
        if (head!!.key == key) {
            head!!.value = value
            return
        }
        var node = head
        var len = 2
        while (node!!.next != null) {
            if (node.next!!.key == key) {
                node.next!!.value = value
                return
            }
            node = node.next
            len++
        }
        node.next = chainNode(key, value)
        if (len > CHAIN_MAX_LENGTH) {
            hashmap.expand()
        }
    }

    operator fun get(key: Any): Any? {
        var node = head
        while (node != null) {
            if (node.key == key) {
                return node.value
            }
            node = node.next
        }
        return null
    }

    fun remove(key: Any): Any? {
        var node = head
        var prev: chainNode? = null
        while (node != null) {
            if (node.key == key) {
                break
            }
            prev = node
            node = node.next
        }
        if (node == null) {
            return null
        }
        if (prev == null) {
            head = head!!.next
        } else {
            prev.next = node.next
        }
        return node.value
    }

    override fun toString(): String {
        val str = StringBuilder()
        var node = head
        while (node?.key != null) {
            str.append("{" + node.key + ":" + node.value + "}")
            node = node.next
        }
        return str.toString()
    }

    override fun forEach(action: Consumer<in chainNode>?) {
        super.forEach(action)
    }

    override fun iterator(): MutableIterator<chainNode> {
        return object : MutableIterator<chainNode> {
            var current = head
            override fun hasNext(): Boolean {
                return current != null
            }

            override fun next(): chainNode {
                val currentRef = current
                current = current!!.next
                return currentRef!!
            }

            override fun remove() {}
        }
    }

    companion object {
        const val CHAIN_MAX_LENGTH = 5
    }
}

class chainNode(var key: Any, var value: Any?) {
    var next: chainNode? = null
}

interface expandable {
    fun expand()
}
