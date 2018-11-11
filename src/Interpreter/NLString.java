package Interpreter;

class NLString extends Value {

    private String value;

    NLString(String value) {
        this.value = value;
    }

    @Override
    String getValue() {
        return value;
    }

    @Override
    public Value add(Value value) {
        return new NLString(this.value + value.getValue());
    }
}
