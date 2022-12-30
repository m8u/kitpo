package src.main.dev.m8u.kitpo.builders

import src.main.dev.m8u.kitpo.types.MyDatetime
import java.util.concurrent.ThreadLocalRandom

class MyDatetimeBuilder : MyHashableBuilder {
    override fun createRandom(): Any {
        var datetime: MyDatetime
        while (true) {
            try {
                datetime = MyDatetime(
                        ThreadLocalRandom.current().nextInt(1900, 2030),
                        ThreadLocalRandom.current().nextInt(1, 12 + 1),
                        ThreadLocalRandom.current().nextInt(1, 31 + 1),
                        ThreadLocalRandom.current().nextInt(0, 23 + 1),
                        ThreadLocalRandom.current().nextInt(0, 59 + 1),
                        ThreadLocalRandom.current().nextInt(0, 59 + 1)
                )
                break
            } catch (_: Exception) {}
        }
        return datetime
    }

    @Throws(Exception::class)
    override fun parse(s: String): Any {
        val minusSplit = s.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val year = minusSplit[0].toInt()
        val month = minusSplit[1].toInt()
        val whitespaceSplit = minusSplit[2].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day = whitespaceSplit[0].toInt()
        val colonSplit = whitespaceSplit[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hour = colonSplit[0].toInt()
        val minute = colonSplit[1].toInt()
        val second = colonSplit[2].toInt()
        return MyDatetime(year, month, day, hour, minute, second)
    }
}