package src.main.dev.m8u.kitpo.builders

interface MyHashableBuilder {
    fun createRandom(): Any

    @Throws(Exception::class)
    fun parse(s: String): Any
}