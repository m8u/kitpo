package src.main.dev.m8u.kitpo.builders

import src.main.dev.m8u.kitpo.types.MyDouble
import java.util.concurrent.ThreadLocalRandom

class MyDoubleBuilder : MyHashableBuilder {
    override fun createRandom(): Any {
        return MyDouble(ThreadLocalRandom.current().nextDouble())
    }

    override fun parse(s: String): Any {
        return MyDouble(s.toDouble())
    }
}