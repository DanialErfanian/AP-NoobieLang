package Interpreter;

class NLInteger extends Value {

    private int value;

    NLInteger(int value) {
        this.value = value;
    }

    @Override
    public Value add(Value value) {
        if (value instanceof NLString)
            return new NLString(this.value + value.getValue());
        else if (value instanceof NLFloat)
            return new NLFloat((double) this.value + Double.parseDouble(value.getValue()));
        else
            return new NLInteger(this.value + Integer.parseInt(value.getValue()));
    }

    @Override
    String getValue() {
        return Integer.toString(value);
    }

}
