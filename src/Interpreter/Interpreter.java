package Interpreter;

import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Interpreter {
    private static final String splitRegex = "[\\s,]+";
    private String[] lines;
    private Scanner input;
    private PrintStream output;
    private PrintStream err;

    public Interpreter(String[] lines, Scanner input, PrintStream output, PrintStream err) {
        this.lines = lines;
        this.input = input;
        this.output = output;
        this.err = err;
    }

    private Map<String, Variable> memory = new TreeMap<>();
    private Map<String, Integer> lifeLine = new TreeMap<>();

    public void run(boolean debug, int startLine, int endLine) {

    }

    public void run() {
        run(false, 0, lines.length);
    }

}
