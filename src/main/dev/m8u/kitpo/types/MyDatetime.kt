package src.main.dev.m8u.kitpo.types

class MyDatetime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) {
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0
    private var second = 0

    init {
        setYear(year)
        setMonth(month)
        setDay(day)
        setHour(hour)
        setMinute(minute)
        setSecond(second)
    }

    @Throws(Exception::class)
    fun setDay(day: Int) {
        this.day = day
        if (!isDayValid) {
            throw Exception("Invalid day value")
        }
    }

    @Throws(Exception::class)
    fun setMonth(month: Int) {
        if (month < 1 || month > 12) {
            throw Exception("Invalid month value")
        }
        this.month = month
    }

    @Throws(Exception::class)
    fun setYear(year: Int) {
        if (year <= 0) {
            throw Exception("Invalid year value")
        }
        this.year = year
    }

    @Throws(Exception::class)
    fun setHour(hour: Int) {
        if (hour < 0 || hour > 23) {
            throw Exception("Invalid hour value")
        }
        this.hour = hour
    }

    @Throws(Exception::class)
    fun setMinute(minute: Int) {
        if (minute < 0 || minute > 59) {
            throw Exception("Invalid minute value")
        }
        this.minute = minute
    }

    @Throws(Exception::class)
    fun setSecond(second: Int) {
        if (second < 0 || second > 59) {
            throw Exception("Invalid second value")
        }
        this.second = second
    }

    private val isDayValid: Boolean
        get() = if (month == 4 || month == 6 || month == 9 || month == 11 && day == 31) {
            false
        } else if (month == 2 && isLeapYear(year) && day > 29) {
            false
        } else month != 2 || day <= 28

    override fun toString(): String {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                year, month, day, hour, minute, second)
    }

    override fun equals(other: Any?): Boolean {
        val otherClass: Class<*> = other!!.javaClass
        if (otherClass == MyDatetime::class.java) {
            return this.toString() == other.toString()
        }
        throw RuntimeException("MyDatetime.equals() is not defined for class " + otherClass.name)
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }

    companion object {
        private const val GREGORIAN_CALENDAR_SWITCH_YEAR = 1582
        private fun isLeapYear(year: Int): Boolean {
            return if (year > GREGORIAN_CALENDAR_SWITCH_YEAR) {
                year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
            } else year % 4 == 0
        }
    }
}