package Exceptions;

public class UndefinedVariableException extends BaseException {
    private String variableName;

    public UndefinedVariableException(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String getError(int line) {
        return "undefined-variable " + variableName + " in-line " + (line + 1);
    }
}
