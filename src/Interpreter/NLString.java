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
}
