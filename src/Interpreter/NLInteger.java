package Interpreter;

class NLInteger extends Value {

    private int value;

    NLInteger(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    String getValue() {
        return Integer.toString(value);
    }

}
