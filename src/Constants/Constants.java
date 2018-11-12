package Constants;

public class Constants {
    public static final boolean DEBUG = false;
    public static final String NAMED_VARIABLE_REGEX = "\\$[\\w\\-]+";
    public static final String UNNAMED_VARIABLE_REGEX = "(?:[^\\$,]|\\$\\$|\\$,)+";
    public static final String VARIABLE_REGEX = UNNAMED_VARIABLE_REGEX + "|(?:" + NAMED_VARIABLE_REGEX + ")";

    public static final String BLOCK_LEVEL_REGEX = "(if|loop)-((start (" + VARIABLE_REGEX + ")(<|>|==|!=)(" + VARIABLE_REGEX + "))|(end))";
    public static final String PUT_REGEX = "put (" + VARIABLE_REGEX + ") to (" + NAMED_VARIABLE_REGEX + ")";

    private static final String SPLIT_3ARGS_REGEX = "\\w* (.*) from (.*) save-to (.*)";
    private static final String SPLIT_2ARGS_REGEX = "\\w* (.*)()? save-to (.*)";
    private static final String SPLIT_1ARGS_REGEX = "\\w* (.*)()?()?";
    public static final String[] SPLIT_REGEX = {SPLIT_3ARGS_REGEX, SPLIT_2ARGS_REGEX, SPLIT_1ARGS_REGEX};

    public static final String CONDITIONAL_EXPRESSION_REGEX = "[^ ]* (" + VARIABLE_REGEX + ")(<|>|==|!=)(" + VARIABLE_REGEX + ")";

    public static final int FLOAT_DECIMAL_NUMBERS = 2;
}
