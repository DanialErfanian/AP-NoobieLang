package Interpreter;

public class NLFloat extends Value {
    private float value;

    NLFloat(float value) {
        this.value = value;
    }

    @Override
    String getValue() {
        return Float.toString(value);
    }

    public void setValue(float value) {
        this.value = value;
    }
}
