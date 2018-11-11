package Exceptions;

public class BadInputException extends BaseException {

    @Override
    public String getError(int line) {
        return "bad-input in-line " + (line + 1);
    }
}
