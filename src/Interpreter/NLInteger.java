package Interpreter;

class NLInteger extends Variable {

    private int value;

    public NLInteger(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    String getValue() {
        return null;
    }

}
