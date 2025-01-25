package ex5.code;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the processing and validation of lines of code.
 * This class is responsible for parsing and checking syntax,
 * ensuring that function declarations, variable assignments,
 * and comments follow the expected format.
 *
 * @author inbar.el and stavzok
 */
public class HandleCodeLines {

    /*
     * List of code lines to be processed.
     */
    private ArrayList<String> codeLines;

    /*
     * Regular expression pattern for detecting comments.
     */
    private final Pattern COMMENT_START = Pattern.compile("^.+//");
    private final Matcher COMMENT_START_MATCHER = COMMENT_START.matcher("");

    /**
     * Regular expressions and patterns for variable names, integers, and doubles.
     */
    public static final String NAME_REGEX = "([a-zA-Z]|_[a-zA-Z0-9])(_?[a-zA-Z0-9])*_?";
    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    public static final Matcher NAME_MATCHER = NAME_PATTERN.matcher("");
    public static final String INT_REGEX = "[+-]?\\d+";
    public static final String DOUBLE_REGEX = "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)";

    /**
     * Tracks the current scope level of the code.
     */
    public static int currScopeLevel = 0;

    /**
     * Symbol tables for global and local variables.
     * Maps variable names to their types, final and initialization states.
     */
    public static HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> globalSymbolsTable = new HashMap<>();
    public static HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> localSymbolsTable = new HashMap<>();

    /**
     * Stores function symbols, mapping function names to their parameters.
     */
    public static HashMap<String, ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>>> functionSymbols = new HashMap<>();

    /*
     * Regular expression pattern for detecting return statements.
     */
    private static final String RETURN_REGEX = "return\\s*;";
    private static final Pattern RETURN_PATTERN = Pattern.compile(RETURN_REGEX);

    /**
     * Matcher for the return statement pattern.
     */
    public static final Matcher RETURN_MATCHER = RETURN_PATTERN.matcher("");

    /**
     * Indicates if a return statement has been encountered in the current scope.
     */
    public static boolean isReturn = false;

    /*
     * Data type constants.
     */
    private static final String VOID = "void";
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String CHAR = "char";
    private static final String BOOLEAN = "boolean";

    /*
     * Regular expressions for string and character literals.
     */
    private static final String STRING_STRUCTURE = "\".*\"";
    private static final String CHAR_STRUCTURE = "'.'";

    /*
     * Comment syntax definitions.
     */
    private final String LEGAL_COMMENT_START = "//";
    private final String ILLEGAL_COMMENT_START = "/*";
    private final String SPACE_REGEX = "\\s+";

    /*
     * End of line characters.
     */
    private final String SEMICOLON = ";";
    private final String OPENING_BRACKET = "{";
    private final String CLOSING_BRACKET = "}";

    /*
     * Error message for invalid comment formats.
     */
    private final String COMMENT_ERROR = "The comment format is illegal!";
    private final String END_OF_LINE_ERROR = "End of line error!";

    /*
     * Handlers for different types of code lines.
     */
    private final HandleVariables variablesHandler;
    private final HandleFunction functionHandler;

    /**
     * Constructs a new HandleCodeLines instance with the provided lines of code and with the needed handlers.
     *
     * @param codeLines The lines of code to be processed.
     */
    public HandleCodeLines(ArrayList<String> codeLines) {
        this.codeLines = codeLines;
        variablesHandler = new HandleVariables();
        functionHandler = new HandleFunction();
    }

    /**
     * Processes all lines of code, handling function declarations,
     * variable assignments, and syntax validation.
     *
     * @throws TypeOneException If an invalid statement is encountered.
     */
    public void handleLines() throws TypeOneException {
        for(String codeLine: codeLines) {
            codeLine = codeLine.replaceAll(SPACE_REGEX, " ");
            if(codeLine.startsWith(VOID)) {
                try {
                    functionHandler.handleFunctionDeclaration(codeLine);
                }
                catch(FunctionDeclarationException e) {
                    throw e;
                }
            }
        }

        for (String line : codeLines) {
            try {
                handleLine(line); // Call the method normally
            } catch (TypeOneException e) {
                throw e;
            }
        }
    }

    /**
     * Processes a single line of code, validating its structure.
     * This method checks for illegal comments, empty lines, function
     * declarations, and variable assignments.
     *
     * @param line The line of code to validate.
     * @throws TypeOneException If an invalid statement is encountered.
     */
    private void handleLine(String line) throws TypeOneException {
        if (COMMENT_START_MATCHER.reset(line).find()) {

            throw new CommentException(COMMENT_ERROR);
        }

        line = line.trim();
        if(line.startsWith(ILLEGAL_COMMENT_START)) {
            throw new CommentException(COMMENT_ERROR);
        }

        if (line.startsWith(LEGAL_COMMENT_START)) {
            return;
        }

        if(line.isEmpty()){
            return;
        }
        if (!(line.endsWith(SEMICOLON) || line.endsWith(OPENING_BRACKET) || line.endsWith(CLOSING_BRACKET))) {
            throw new TypeOneException(END_OF_LINE_ERROR);
        }

        if(currScopeLevel > 0) {
            try {
                RETURN_MATCHER.reset(line);
                if(currScopeLevel == 1 && RETURN_MATCHER.matches()) {
                    isReturn = true;
                    return;
                }
                functionHandler.handleFunction(line);
            }
            catch(TypeOneException e) {
                throw e;
            }
        }

        else if (currScopeLevel == 0) {
            if (line.startsWith(VOID)){
                try {
                    functionHandler.handleFunction(line);
                }
                catch(TypeOneException e) {
                    throw e;
                }
            }
            else {
                try {
                    variablesHandler.defineAssignVariable(line);
                }
                catch(VariablesException e) {
                    throw e;
                }
            }
        }
    }

    /**
     * Determines the data type of a given parameter.
     * This method checks if the parameter matches known data types
     * such as boolean, int, double, char, or String.
     *
     * @param parameter The parameter to analyze.
     * @return The detected type of the parameter, or null if unknown.
     */
    public static String findType(String parameter) {

        // Check for boolean (true/false)
        if (parameter.equals(TRUE) || parameter.equals(FALSE)) {
            return BOOLEAN;
        }

        // Check for integer
        if (parameter.matches(INT_REGEX)) { // Optional '-' for negative numbers
            return INT;
        }

        // Check for double (floating point numbers)
        if (parameter.matches(DOUBLE_REGEX)) { // Includes decimal values
            return DOUBLE;
        }

        // Check for character (single character enclosed in single quotes)
        if (parameter.matches(CHAR_STRUCTURE)) {
            return CHAR;
        }

        // Check for String (text enclosed in double quotes)
        if (parameter.matches(STRING_STRUCTURE)) {
            return STRING;
        }

        // If none of the above match, return "unknown"
        return null;

    }
}
