package src.main.dev.m8u.kitpo

import java.io.FileOutputStream
import java.io.IOException

object Main {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        test("MyDouble", 8000)
        test("MyDouble", 10000)
        test("MyDatetime", 8000)
        test("MyDatetime", 10000)
        val gui = GUI()
        gui.isVisible = true
    }

    @Throws(IOException::class)
    private fun test(typeName: String, items: Int) {
        val builder = TypeFactory.getBuilderByName(typeName)
        val hashtable = ChainedHashtable(typeName)
        val timeFos = FileOutputStream("%s_%d_avg_set_time.txt".format(typeName, items))
        val timeMA = ArrayList<Long>()
        val timeMAWindow = 100
        var chainLengthSum = 0.0
        var occupancySum = 0.0
        for (i in 1..items) {
            val key = builder!!.createRandom()
            val start = System.currentTimeMillis()
            hashtable[key] = null
            val stop = System.currentTimeMillis()
            timeMA.add(stop - start)
            if (timeMA.size > timeMAWindow) timeMA.removeAt(0)
            timeFos.write("""${timeMA.stream().reduce { a: Long, b: Long -> java.lang.Long.sum(a, b) }.get().toDouble() / timeMAWindow}
""".toByteArray())
            chainLengthSum += hashtable.averageChainLength
            occupancySum += hashtable.occupancy
        }
        System.out.printf(
"""
============ %s (%d) test complete ============
Final capacity: %d
Final avg. chain length: %.2f
Final occupancy: %.2f%%
All-time avg. chain length: %.2f
All-time avg. occupancy: %.2f%%
""",
                typeName, items,
                hashtable.capacity,
                hashtable.averageChainLength,
                hashtable.occupancy * 100,
                chainLengthSum / items,
                occupancySum / items * 100
        )
    }
}