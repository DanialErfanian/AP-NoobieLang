package Exceptions;

import Interpreter.Interpreter;

public class SyntaxErrorException extends BaseException {

    public String getError(int line){
        return "syntax-error in-line " + line;
    }

}
