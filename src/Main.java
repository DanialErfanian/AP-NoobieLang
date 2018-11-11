import Interpreter.Interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> lines = readLines(scanner);
        String[] code_lines = lines.toArray(new String[0]);
        Interpreter interpreter = new Interpreter(code_lines, scanner, System.out, System.err);
        interpreter.run();
    }

    private static List<String> readLines(Scanner scanner) {
        List<String> lines = new ArrayList<>();
        while (true) {
            String cur = scanner.nextLine();
            lines.add(cur);
            if (cur.equals("code-end"))
                break;
        }
        return lines;
    }
}