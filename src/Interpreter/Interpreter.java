package Interpreter;

import Constants.Constants;
import Exceptions.BaseException;
import Exceptions.SyntaxErrorException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    private String[] lines;
    private Scanner scanner;
    private PrintStream outputStream;
    private PrintStream errStream;
    private Memory memory = new Memory();
    private int[] match;
    private String[] type;

    public Interpreter(String[] lines, Scanner scanner, PrintStream outputStream, PrintStream errStream) {
        this.lines = lines;
        this.scanner = scanner;
        this.outputStream = outputStream;
        this.errStream = errStream;
        initialize();
    }// DONE

    private void initialize() {
        lines[0] = "if-start 1<2";
        lines[lines.length - 1] = "if-end";
        for (int i = 0; i < lines.length; i++)
            lines[i] = lines[i].trim();
        match = new int[lines.length];
        for (int i = 0; i < match.length; i++)
            match[i] = -1;
        type = new String[lines.length];
        for (int i = 0; i < lines.length; i++)
            type[i] = getCommandType(lines[i]);
        Stack<Integer> stack = new Stack<>();
        // 2*i line i we have if
        // 2*i+1 line i we have loop
        for (int line = 0; line < lines.length; line++)
            if (lines[line].matches(Constants.BLOCK_LEVEL_REGEX)) {
                int x = 2 * line + ((type[line].startsWith("loop")) ? 1 : 0);
                if (type[line].endsWith("start")) stack.push(x);
                else {
                    int index = -1;
                    for (int i = stack.size() - 1; i >= 0; i--)
                        if ((stack.get(i) & 1) == (x & 1)) {
                            index = stack.get(i);
                            while (stack.lastElement() != index)
                                stack.pop();
                            stack.pop();
                            index /= 2;
                            break;
                        }
                    match[line] = index;
                    if (index != -1)
                        match[index] = line;
                }
            }
    }// DONE


    private String getCommandType(String command) {
        int last = command.length();
        for (int i = 0; i < command.length(); i++)
            if (command.charAt(i) == ' ') {
                last = i;
                break;
            }
        return command.substring(0, last);
    }// DONE

    private String parseString(String string) {
        string = string.replaceAll("\\G\\$,", "~");
        string = string.replaceAll("\\G\\$\\$", "`");
        string = string.replaceAll("`", "\\$");
        string = string.replaceAll("~", ",");
        return string;
    }

    private ArrayList<String> split(String command) throws BaseException {
        Matcher matcher;
        for (int i = 0; ; i++) {
            if (i == Constants.SPLIT_REGEX.length)
                throw new SyntaxErrorException();
            matcher = Pattern.compile(Constants.SPLIT_REGEX[i]).matcher(command);
            if (matcher.matches())
                break;
        }
        String from = matcher.group(2), save = matcher.group(3);
        ArrayList<String> variables = splitVariables(matcher.group(1));
        if (from.equals("")) from = null;
        if (save.equals("")) save = null;
        variables.add(from);
        variables.add(save);
        return variables;
    }// DONE

    private ArrayList<String> splitVariables(String string) {
        int dollar = 0;
        string = string + ",";
        ArrayList<String> variables = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (ch == ',' && dollar % 2 == 0) {
                variables.add(current.toString());
                current = new StringBuilder();
            } else
                current.append(ch);
            if (ch == '$')
                dollar++;
        }

        return variables;
    }

    private void put(int startLine, int endLine) {
        try {
            Pattern pattern = Pattern.compile(Constants.PUT_REGEX);
            Matcher matcher = pattern.matcher(lines[startLine]);
            if (matcher.find()) {
                String from = matcher.group(1);
                String save = matcher.group(2);
                memory.add(save.substring(1), endLine, getVariable(from, startLine));
                // FIXME pass by value in put
            } else
                throw new SyntaxErrorException();
        } catch (BaseException e) {
            outputStream.println(e.getError(startLine));
        }
    }


    private void print(int line) {
        try {
            String[] strings = lines[line].split("\\s");
            StringBuilder output = new StringBuilder();
            for (String string : strings) {
                if (string.startsWith("$"))
                    output.append(memory.get(string.substring(1), line));
                else
                    output.append(string);
                output.append(" ");
            }
            outputStream.println(output.toString());
        } catch (BaseException e) {
            outputStream.print(e.getError(line));
        }
    }

    private void declare(int startLine, int endLine, boolean readFromInput) {
        try {
            ArrayList<String> variables = split(lines[startLine]);
            for (int i = 0; i < variables.size() - 2; i++) {
                String name = variables.get(i);
                Variable variable;
                if (!name.matches(Constants.NAMED_VARIABLE_REGEX))
                    throw new SyntaxErrorException();
                if (readFromInput)
                    variable = Variable.parseVariable(scanner.next());
                else
                    variable = new Variable(new NLInteger(0));
                memory.add(name.substring(1), endLine, variable);
            }

        } catch (BaseException e) {
            outputStream.println(e.getError(startLine));
        }
    } // DONE

    private void declare(int startLine, int endLine) {
        declare(startLine, endLine, false);
    } // DONE

    private void computationalCommand(int startLine, int endLine) {
        String command = lines[startLine];

        ArrayList<Variable> variables = new ArrayList<Variable>();
        String destinationName;
        try {
            ArrayList<String> variablesName = split(command);
            for (int i = 0; i + 1 < variablesName.size(); i++)
                variables.add(getVariable(variablesName.get(i), startLine));
            destinationName = variablesName.get(variablesName.size() - 1);
            Variable destination;
            Variable from = variables.get(variables.size() - 1);
            variables = new ArrayList<>(variables.subList(0, variables.size() - 1));
            switch (type[startLine]) {
                case "add":
                    if (from != null)
                        throw new SyntaxErrorException();
                    destination = Variable.add(variables);
                    break;
                case "multiply":
                    if (from != null)
                        throw new SyntaxErrorException();
                    destination = Variable.multiply(variables);
                    break;
                case "subtract":
                    destination = Variable.subtract(variables, from);
                    break;
                case "divide":
                    destination = Variable.divide(variables, from);
                    break;
                default:
                    throw new SyntaxErrorException();
            }
            memory.add(destinationName, endLine, destination);
        } catch (BaseException e) {
            outputStream.println(e.getError(startLine));
        }
    } // DONE

    private Variable getVariable(String s, int startLine) throws BaseException {
        if (s.matches(Constants.NAMED_VARIABLE_REGEX))
            return memory.get(s.substring(1), startLine);
        else if (s.matches(Constants.UNNAMED_VARIABLE_REGEX))
            return Variable.parseVariable(parseString(s));
        else
            throw new SyntaxErrorException();
    }// DONE

    private boolean checkExpression(int line) throws BaseException {
        String command = lines[line];
        Pattern pattern = Pattern.compile(Constants.CONDITIONAL_EXPRESSION_REGEX);
        Matcher matcher = pattern.matcher(command);
        Variable variable1 = getVariable(matcher.group(1), line);
        Variable variable2 = getVariable(matcher.group(3), line);
        switch (matcher.group(2)) {
            case "<":
                return variable2.isGreater(variable1);
            case ">":
                return variable1.isGreater(variable2);
            case "==":
                return variable1.equals(variable2);
            case "!=":
                return !variable1.equals(variable2);
        }
        return false;
    }// DONE

    private void run(int startLine, int endLine) {
        if (startLine >= endLine)
            return;
        String command = lines[startLine];
        command = command.trim();
        String type = getCommandType(command);
        switch (type) {
            case "declare":
                declare(startLine, endLine);
                break;
            case "put":
                put(startLine, endLine);
                break;
            case "input":
                input(startLine, endLine);
                break;
            case "print":
                print(startLine);
                break;
            default:
                if (command.matches(Constants.BLOCK_LEVEL_REGEX))
                    startLine = blockLevelCommand(startLine);
                else
                    computationalCommand(startLine, endLine);
                break;
        }
        run(startLine + 1, endLine);
    } // DONE

    private void input(int startLine, int endLine) {
        declare(startLine, endLine, true);
    } // DONE

    private int blockLevelCommand(int line) {
        if (match[line] == -1)
            return line;
        do {
            try {
                if (checkExpression(line)) {
                    // Memory memory1 = memory.clone
                    // TODO clone memory
                    run(line + 1, match[line]);
                } else
                    break;
            } catch (BaseException e) {
                outputStream.println(e.getError(line));
            }
        } while (lines[line].charAt(0) == 'l');
        return match[line];
    }

    public void run() {
        run(0, lines.length);
    }// DONE
}
