package src.main.dev.m8u.kitpo.types;

public class MyDouble {
    double value;

    public MyDouble(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public boolean equals(Object other) {
        Class<?> otherClass = other.getClass();
        if (otherClass.equals(MyDouble.class)) {
            return this.toString().equals(other.toString());
        }
        throw new RuntimeException("MyDouble.equals() is not defined for class "+otherClass.getName());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
