package Interpreter;

public class NLFloat extends Variable {

    float value;

    public NLFloat(float value) {
        this.value = value;
    }

    @Override
    String getValue() {
        return null;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
