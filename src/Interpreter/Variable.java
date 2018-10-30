package Interpreter;

import Exceptions.BaseException;

import java.util.ArrayList;

abstract class Variable {
    static Variable parseVariable(String value) {
        return null;
    }

    static Variable add(ArrayList<Variable> variables, Variable destination) throws BaseException {
        return null;
    }

    static Variable multiply(ArrayList<Variable> variables, Variable destination) throws BaseException {
        return null;
    }

    static Variable subtract(ArrayList<Variable> variables, Variable from, Variable destination) throws BaseException {
        return null;
    }

    static Variable divide(ArrayList<Variable> variables, Variable from, Variable destination) throws BaseException {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable))
            return false;
        return false;
    }

    public boolean isGreater(Variable variable) throws BaseException {
        return false;
    }

    abstract String getValue();

    @Override
    public String toString() {
        return getValue();
    }

}
