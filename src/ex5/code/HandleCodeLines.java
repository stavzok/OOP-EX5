package ex5.code;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleCodeLines {
    private ArrayList<String> codeLines;
    private static final Pattern COMMENT_START = Pattern.compile("^.*\\\\");
    private static final Matcher COMMENT_START_MATCHER = COMMENT_START.matcher("");

    public static final String NAME_REGEX = "_?[a-zA-Z](_?[a-zA-Z0-9])*_?";
    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    public static final Matcher NAME_MATCHER = NAME_PATTERN.matcher("");
    public static final String INT_REGEX = "[+-]?\\d+";
    public static final String DOUBLE_REGEX = "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)";
    public static int currScopeLevel = 0;
    // name: type: final: initialized
    public static HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> globalSymbolsTable = new HashMap<>();

    // name: type: final: initialized
    public static HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> localSymbolsTable = new HashMap<>();
    public static HashMap<String, ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>>> functionSymbols = new HashMap<>();
    private static final String RETURN_REGEX = "return\\s*;";
    private static final Pattern RETURN_PATTERN = Pattern.compile(RETURN_REGEX);
    public static final Matcher RETURN_MATCHER = RETURN_PATTERN.matcher("");
    public static boolean isReturn = false;
    // errors
    private final String FUNCTION_DECLARATION_ERROR = "The function declaration is illegal!";
    public HandleCodeLines(ArrayList<String> codeLines) {
        this.codeLines = codeLines;
    }

    public void handleLines() throws TypeOneException {
        for(String codeLine: codeLines) {
            codeLine = codeLine.replaceAll("\\s+", " ");
            if(codeLine.startsWith("void")) {
                codeLine.trim();
                try {
                    HandleFunction.handleFunctionDeclaration(codeLine);
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

    private void handleLine(String line) throws TypeOneException {
        if (COMMENT_START_MATCHER.reset(line).find()) {
            throw new CommentException("Comment format is illegal!");
        }

        line = line.trim();
        if(line.startsWith("/*")) {
            throw new CommentException("Comment format is illegal!");
        }


        if (line.startsWith("//")) {
            return;
        }

        if(line.isEmpty()){
            return;
        }

        if(currScopeLevel > 0) {
            try {
                RETURN_MATCHER.reset(line);
                if(currScopeLevel == 1 && RETURN_MATCHER.matches()) {
                    isReturn = true;
                    return;
                }
                HandleFunction.handleFunction(line);
            }
            catch(TypeOneException e) {
                throw e;
            }
        }

        else if (currScopeLevel == 0) {
            if (line.startsWith("void")){
                try {
                    HandleFunction.handleFunction(line);
                }
                catch(TypeOneException e) {
                    throw e;
                }
            }
            else {
                try {
                    HandleVariables.defineAssignVariable(line);
                }
                catch(VariablesException e) {
                    throw e;
                }
            }
        }
    }




    public static String findType(String parameter) {

        // Check for boolean (true/false)
        if (parameter.equals("true") || parameter.equals("false")) {
            return "boolean";
        }

        // Check for integer
        if (parameter.matches(INT_REGEX)) { // Optional '-' for negative numbers
            return "int";
        }

        // Check for double (floating point numbers)
        if (parameter.matches(DOUBLE_REGEX)) { // Includes decimal values
            return "double";
        }

        // Check for character (single character enclosed in single quotes)
        if (parameter.matches("'.'")) {
            return "char";
        }

        // Check for String (text enclosed in double quotes)
        if (parameter.matches("\".*\"")) {
            return "String";
        }

        // If none of the above match, return "unknown"
        return null;

    }
}
