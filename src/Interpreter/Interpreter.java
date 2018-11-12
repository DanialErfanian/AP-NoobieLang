package Interpreter;

import Constants.Constants;
import Exceptions.BaseException;
import Exceptions.SyntaxErrorException;
import Exceptions.UndefinedVariableException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    private String[] lines;
    private Scanner scanner;
    private PrintStream outputStream;
    private PrintStream errStream;
    private Stack<Memory> memories;
    private int[] match;
    private String[] type;

    public Interpreter(String[] lines, Scanner scanner, PrintStream outputStream, PrintStream errStream) {
        this.lines = lines;
        this.scanner = scanner;
        this.outputStream = outputStream;
        this.errStream = errStream;
        initialize();
        if (Constants.DEBUG) {
            errStream.println("math array: ");
            for (int i = 0; i < match.length; i++)
                errStream.println(i + ": " + match[i]);
            errStream.println();
        }
    }// DONE

    private void initialize() {
        memories = new Stack<>();
        lines[0] = "if-start 1<2";
        lines[lines.length - 1] = "if-end";
        for (int i = 0; i < lines.length; i++)
            lines[i] = lines[i].trim();
        match = new int[lines.length];
        for (int i = 0; i < match.length; i++)
            match[i] = -1;
        type = new String[lines.length];
        for (int i = 0; i < lines.length; i++)
            type[i] = getCommandType(lines[i]).trim();
        Stack<Integer> stack = new Stack<>();
        // 2*i line i we have if
        // 2*i+1 line i we have loop
        for (int line = 0; line < lines.length; line++)
            if (lines[line].matches(Constants.BLOCK_LEVEL_REGEX)) {
                if (Constants.DEBUG)
                    errStream.println("------       found a block level command in line " + line);
                int x = 2 * line + ((type[line].startsWith("loop")) ? 1 : 0);
                if (type[line].endsWith("start")) {
                    stack.push(x);
                    if (Constants.DEBUG)
                        errStream.println("------       start of block level command " + line);

                } else {
                    if (Constants.DEBUG)
                        errStream.println("------       end of block level command " + line);

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
        if (Constants.DEBUG)
            errStream.println("splitVariables called with string = " + string);
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
            else
                dollar = 0;
        }
        if (Constants.DEBUG)
            errStream.println("in splitVariables variables = " + Arrays.toString(variables.toArray()));

        return variables;
    }

    private void put(int startLine, int endLine) {
        if (Constants.DEBUG)
            errStream.println("method put called with args:(start, end)" + startLine + ", " + endLine);
        try {
            Pattern pattern = Pattern.compile(Constants.PUT_REGEX);
            Matcher matcher = pattern.matcher(lines[startLine]);
            if (matcher.find()) {
                String from = matcher.group(1);
                String save = matcher.group(2);
                if (Constants.DEBUG) {
                    errStream.println("adding to memory with args:(name, line, variable)" +
                            save.substring(1) + " " +
                            endLine + " " +
                            getVariable(from, startLine));
                }
                addToMemory(save.substring(1), endLine, getVariable(from, startLine));
                // FIXME pass by value in put
            } else
                throw new SyntaxErrorException();
        } catch (BaseException e) {
            outputStream.println(e.getError(startLine));
        }
    }

    private void addToMemory(String name, int endLine, Variable variable, boolean define) {
        if (Constants.DEBUG)
            errStream.println("method addToMemory called with args(name, endLine, variable, define)" +
                    name + ", " +
                    endLine + ", " +
                    variable + ", " +
                    define);
        try {
            if (!define)
                getVariable("$" + name, endLine).setValue(variable.getValue());
        } catch (BaseException ignored) {
        }
        memories.lastElement().add(name, endLine, variable);
    }

    private void addToMemory(String name, int endLine, Variable variable) {
        addToMemory(name, endLine, variable, false);
    }

    private void print(int line) {
        if (Constants.DEBUG)
            errStream.println("method print called with line = " + line);
        try {
            String[] strings = lines[line].split("\\s");
            if (Constants.DEBUG)
                errStream.println("string : " + Arrays.toString(strings));
            StringBuilder output = new StringBuilder();
            for (int i = 1; i < strings.length; i++) {
                String string = strings[i];
                if (string.matches(Constants.NAMED_VARIABLE_REGEX))
                    output.append(getVariable(string, line));
                else
                    output.append(parseString(string));
                output.append(" ");
            }
            outputStream.println(output.toString());
        } catch (BaseException e) {
            outputStream.println(e.getError(line));
        }
    }

    private void declare(int startLine, int endLine, boolean readFromInput) {
        if (Constants.DEBUG)
            errStream.println("declare called with args:(startLine, endLine, readFromInput) " + startLine + " " + endLine + " " + readFromInput);
        try {
            ArrayList<String> variables = split(lines[startLine]);
            if (Constants.DEBUG)
                errStream.println("variables: " + Arrays.toString(variables.toArray()));
            for (int i = 0; i < variables.size() - 2; i++) {
                String name = variables.get(i);
                Variable variable;
                if (!name.matches(Constants.NAMED_VARIABLE_REGEX))
                    throw new SyntaxErrorException();
                if (readFromInput)
                    variable = Variable.parseVariable(scanner.nextLine());
                else
                    variable = new Variable(new NLInteger(0));
                if (Constants.DEBUG) {
                    errStream.println("adding to memory with args:(name, line, variable)" +
                            name.substring(1) + " " +
                            endLine + " " +
                            variable);
                }
                addToMemory(name.substring(1), endLine, variable, !readFromInput);
            }

        } catch (BaseException e) {
            outputStream.println(e.getError(startLine));
        }
    } // DONE

    private void declare(int startLine, int endLine) {
        declare(startLine, endLine, false);
    } // DONE

    private void computationalCommand(int startLine, int endLine) {
        if (Constants.DEBUG)
            errStream.println("computationalCommand called with args:(start, end)" + startLine + " " + endLine);
        String command = lines[startLine];
        ArrayList<Variable> variables = new ArrayList<Variable>();
        String destinationName;
        try {
            ArrayList<String> variablesName = split(command);
            for (int i = 0; i + 1 < variablesName.size(); i++)
                variables.add(getVariable(variablesName.get(i), startLine));
            if (Constants.DEBUG)
                errStream.println("variables: " + Arrays.toString(variables.toArray()));
            destinationName = variablesName.get(variablesName.size() - 1);
            if (Constants.DEBUG)
                errStream.println("destinationName = " + destinationName);
            if (destinationName == null)
                throw new SyntaxErrorException();
            destinationName = destinationName.substring(1);
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
                    if (from == null)
                        throw new SyntaxErrorException();
                    destination = Variable.subtract(variables, from);
                    break;
                case "divide":
                    if (from == null)
                        throw new SyntaxErrorException();
                    destination = Variable.divide(variables, from);
                    break;
                default:
                    throw new SyntaxErrorException();
            }
            if (Constants.DEBUG) {
                errStream.println("adding to memory with args:(name, line, variable)" + destinationName + " " + endLine + " " + destination);
            }
            addToMemory(destinationName, endLine, destination);
        } catch (BaseException e) {
            outputStream.println(e.getError(startLine));
        }
    } // DONE

    private Variable getVariable(String s, int startLine) throws BaseException {
        if (s == null)
            return null;
        if (s.matches(Constants.NAMED_VARIABLE_REGEX)) {
            s = s.substring(1);
            for (int i = memories.size() - 1; i >= 0; i--) {
                try {
                    Memory memory = memories.get(i);
                    return memory.get(s, startLine);
                } catch (UndefinedVariableException e) {
                    if (i == 0)
                        throw e;
                }
            }
            assert (false);
            return null;
        } else if (s.matches(Constants.UNNAMED_VARIABLE_REGEX))
            return Variable.parseVariable(parseString(s));
        else
            throw new SyntaxErrorException();
    }// DONE

    private boolean checkExpression(int line) throws BaseException {
        if (Constants.DEBUG)
            errStream.println("checkExpression called with line = " + line + " command = " + lines[line]);
        String command = lines[line];
        Pattern pattern = Pattern.compile(Constants.CONDITIONAL_EXPRESSION_REGEX);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.matches())
            throw new SyntaxErrorException();
        if (Constants.DEBUG)
            errStream.println("in checkExpression matcher.group(1) = " + matcher.group(1) + ", matcher.group(3) = " + matcher.group(3));
        Variable variable1 = getVariable(matcher.group(1), line);
        Variable variable2 = getVariable(matcher.group(3), line);
        if (Constants.DEBUG)
            errStream.println("checkExpression: variable1 = " + variable1 + " variable2 = " + variable2);
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
        if (Constants.DEBUG)
            errStream.println("method run called with args(start, end)" + startLine + ", " + endLine);
        if (startLine >= endLine)
            return;
        String command = lines[startLine];
        String type = this.type[startLine];
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
                    startLine = blockLevelCommand(startLine, endLine);
                else
                    computationalCommand(startLine, endLine);
                break;
        }
        run(startLine + 1, endLine);
    } // DONE

    private void input(int startLine, int endLine) {
        declare(startLine, endLine, true);
    } // DONE

    private int blockLevelCommand(int line, int endLine) {
        if (Constants.DEBUG)
            errStream.println("method blockLevelCommand called for line = " + line + ", command = " + lines[line]);
        if (match[line] == -1) {
            try {
                throw new SyntaxErrorException();
            } catch (SyntaxErrorException e) {
                outputStream.println(e.getError(line));
            }
            return endLine;
        }
        try {
            do {
                if (checkExpression(line)) {
                    memories.add(new Memory());
                    run(line + 1, match[line]);
                    memories.pop();
                } else
                    break;
            } while (lines[line].charAt(0) == 'l');
        } catch (BaseException e) {
            outputStream.println(e.getError(line));
        }
        return match[line];
    }

    public void run() {
        memories.add(new Memory());
        run(0, lines.length);
    }// DONE
}
