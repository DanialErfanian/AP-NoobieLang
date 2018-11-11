package Interpreter;

import Exceptions.BadInputException;
import Exceptions.BaseException;

import java.util.ArrayList;

public class Variable {

    private Value value;

    Variable(Value value) {
        this.value = value;
    }

    static Variable parseVariable(String value) {
        Value resultValue;
        try {
            resultValue = new NLInteger(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            try {
                resultValue = new NLFloat(Float.parseFloat(value));
            } catch (NumberFormatException g) {
                resultValue = new NLString(value);
            }
        }
        return new Variable(resultValue);
    }

    private Value getValue() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable))
            return false;
        return this.toString().equals(obj.toString());
    }

    private float getFloatValue() throws BaseException {
        if (this.getValue() instanceof NLString)
            throw new BadInputException();
        return Float.parseFloat(this.toString());
    }

    boolean isGreater(Variable variable) throws BaseException {
        return this.getFloatValue() > variable.getFloatValue();
    }

    @Override
    public String toString() {
        return value.getValue();
    }

    static Variable add(ArrayList<Variable> variables) {
        Value resultValue = new NLInteger(0);
        for (Variable variable : variables)
            resultValue = resultValue.add(variable.getValue());
        return new Variable(resultValue);
    }

    static Variable multiply(ArrayList<Variable> variables) throws BaseException {
        float result = 1;
        boolean haveFloat = false;
        for (Variable variable : variables) {
            if (variable.getValue() instanceof NLString)
                throw new BadInputException();
            else
                result *= Float.parseFloat(variable.toString());
            haveFloat |= (variable.getValue() instanceof NLFloat);
        }
        if (haveFloat)
            return new Variable(new NLFloat(result));
        else
            return new Variable(new NLInteger((int) result));
    }

    static Variable subtract(ArrayList<Variable> variables, Variable from) throws BaseException {
        float result = Float.parseFloat(from.toString());
        boolean haveFloat = false;
        for (Variable variable : variables) {
            if (variable.getValue() instanceof NLString)
                throw new BadInputException();
            else
                result -= Float.parseFloat(variable.toString());
            haveFloat |= (variable.getValue() instanceof NLFloat);
        }
        if (haveFloat)
            return new Variable(new NLFloat(result));
        else
            return new Variable(new NLInteger((int) result));
    }


    static Variable divide(ArrayList<Variable> variables, Variable from) throws BaseException {
        float result = Float.parseFloat(from.toString());
        boolean haveFloat = false;
        for (Variable variable : variables) {
            if (variable.getValue() instanceof NLString)
                throw new BadInputException();
            else {
                float v = Float.parseFloat(variable.toString());
                if (v == 0)
                    throw new BadInputException();
                result /= v;
            }
            haveFloat |= (variable.getValue() instanceof NLFloat);
        }
        if (haveFloat)
            return new Variable(new NLFloat(result));
        else
            return new Variable(new NLInteger((int) result));
    }
}
