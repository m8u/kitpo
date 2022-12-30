package src.main.dev.m8u.kitpo.types

class MyDouble(private var value: Double) {
    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        val otherClass: Class<*> = other!!.javaClass
        if (otherClass == MyDouble::class.java) {
            return this.toString() == other.toString()
        }
        throw RuntimeException("MyDouble.equals() is not defined for class " + otherClass.name)
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }
}