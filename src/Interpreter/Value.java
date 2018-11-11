package Interpreter;

abstract class Value {
    abstract String getValue();

    abstract public Value add(Value value);

    @Override
    public String toString() {
        return getValue();
    }
}
