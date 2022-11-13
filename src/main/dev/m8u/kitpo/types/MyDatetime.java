package src.main.dev.m8u.kitpo.types;

public class MyDatetime {
    private static final int GREGORIAN_CALENDAR_SWITCH_YEAR = 1582;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public MyDatetime(int year, int month, int day, int hour, int minute, int second) throws Exception {
        setYear(year);
        setMonth(month);
        setDay(day);
        setHour(hour);
        setMinute(minute);
        setSecond(second);
    }

    public void setDay(int day) throws Exception {
        this.day = day;
        if (!isDayValid()) {
            throw new Exception("Invalid day value");
        }
    }

    public void setMonth(int month) throws Exception {
        if (month < 1 || month > 12) {
            throw new Exception("Invalid month value");
        }
        this.month = month;
    }

    public void setYear(int year) throws Exception {
        if (year <= 0) {
            throw new Exception("Invalid year value");
        }
        this.year = year;
    }

    public void setHour(int hour) throws Exception {
        if (hour < 0 || hour > 23) {
            throw new Exception("Invalid hour value");
        }
        this.hour = hour;
    }

    public void setMinute(int minute) throws Exception {
        if (minute < 0 || minute > 59) {
            throw new Exception("Invalid minute value");
        }
        this.minute = minute;
    }

    public void setSecond(int second) throws Exception {
        if (second < 0 || second > 59) {
            throw new Exception("Invalid second value");
        }
        this.second = second;
    }

    private static boolean isLeapYear(int year) {
        if (year > GREGORIAN_CALENDAR_SWITCH_YEAR) {
            return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
        }
        return year % 4 == 0;
    }

    private boolean isDayValid() {
        if ((this.month == 4 || this.month == 6 || this.month == 9 || this.month == 11) && day == 31) {
            return false;
        } else if (this.month == 2 && isLeapYear(this.year) && this.day > 29) {
            return false;
        } else return this.month != 2 || this.day <= 28;
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                this.year, this.month, this.day, this.hour, this.minute, this.second);
    }

    @Override
    public boolean equals(Object other) {
        Class<?> otherClass = other.getClass();
        if (otherClass.equals(MyDatetime.class)) {
            return this.toString().equals(other.toString());
        }
        throw new RuntimeException("MyDatetime.equals() is not defined for class "+otherClass.getName());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}

