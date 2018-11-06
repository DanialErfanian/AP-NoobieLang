package Interpreter;

abstract class Value {
    abstract String getValue();

    @Override
    public String toString() {
        return getValue();
    }
}
