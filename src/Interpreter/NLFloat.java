package Interpreter;

import Constants.Constants;

public class NLFloat extends Value {
    private float value;

    NLFloat(float value) {
        this.value = value;
    }

    @Override
    String getValue() {
        double x = Math.pow(10, Constants.FLOAT_DECIMAL_NUMBERS);
        double value = Math.round(this.value * x) / x;
        return Double.toString(value);
    }

    public void setValue(float value) {
        this.value = value;
    }
}
