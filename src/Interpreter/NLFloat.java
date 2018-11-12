package Interpreter;

import Constants.Constants;

public class NLFloat extends Value {
    private double value;

    NLFloat(double value) {
        this.value = value;
    }

    @Override
    public Value add(Value value) {
        if (value instanceof NLString)
            return new NLString(this.value + value.toString());
        else
            return new NLFloat(this.value + Double.parseDouble(value.getValue()));
    }

    @Override
    String getValue() {
        double x = Math.pow(10, Constants.FLOAT_DECIMAL_NUMBERS);
        double value = Math.round(this.value * x) / x;
        return String.format("%.2f", value);
    }

}
