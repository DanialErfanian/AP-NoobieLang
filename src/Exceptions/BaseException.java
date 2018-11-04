package Exceptions;

abstract public class BaseException extends Exception {
    abstract public String getError(int line);
}
