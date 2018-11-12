package Interpreter;

import Exceptions.BaseException;
import Exceptions.UndefinedVariableException;

import java.util.Map;
import java.util.TreeMap;

class Memory {

    private Map<String, Variable> memory = new TreeMap<>();
    private Map<String, Integer> lifeLine = new TreeMap<>();

    Variable get(String name, int line) throws BaseException {
        Variable variable = memory.get(name);
        Integer lifeLine = this.lifeLine.get(name);
        if (variable == null || lifeLine == null || lifeLine < line)
            throw new UndefinedVariableException(name);
        return variable;
    }

    void add(String name, int lifeLine, Variable variable) {
        memory.put(name, variable);
        this.lifeLine.put(name, lifeLine);
    }

    private void addToMemory(String name, Variable variable) {
        memory.put(name, variable);
    }

    private void addToLifeLine(String name, int lifeLine) {
        this.lifeLine.put(name, lifeLine);
    }

    Memory Clone() {
        Memory result = new Memory();
        for (Map.Entry<String, Variable> entry : memory.entrySet()) {
            String key = entry.getKey();
            Variable value = entry.getValue();
            result.addToMemory(key, value);
        }
        for (Map.Entry<String, Integer> entry : lifeLine.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            result.addToLifeLine(key, value);
        }
        return result;
    }

}
