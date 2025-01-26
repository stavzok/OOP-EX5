package ex5.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the parsing, validation, and execution of function declarations and calls.
 * This class ensures that function syntax follows the expected format, prevents nested
 * function declarations, and validates function calls.
 *
 * @author inbar.el and stavzok
 */
public class HandleFunction {

    /*
     * Regular expression pattern for detecting function declarations.
     */
    private final String PATTERN_TYPE = "(int|double|String|boolean|char)";
    private final String FUNCTION_NAME_REGEX =
            "void\\s+[a-zA-Z](_?[a-zA-Z0-9])*_?\\s*\\(\\s*(\\s*(final\\s+)?" +
            PATTERN_TYPE + "\\s+" + HandleCodeLines.NAME_REGEX +
            "\\s*(,\\s*(final\\s+)?" + PATTERN_TYPE + "\\s+" + HandleCodeLines.NAME_REGEX + ")*)?\\)\\s*";
    private final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME_REGEX + "\\{");
    private final Matcher FUNCTION_NAME_MATCHER = FUNCTION_NAME_PATTERN.matcher("");

    /*
     * Regular expression pattern for detecting function calls.
     */
    private static final String FUNCTION_CALL_REGEX =
            "[a-zA-Z](_?[a-zA-Z0-9])*_?\\s*" +  // function name
                    "\\(" +                              // opening parenthesis
                    "\\s*" +                             // optional whitespace
                    "(" +                                // start optional group for arguments
                    "(" +                            // group for first argument
                    "([+-]?\\d+|" +              // INT
                    "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + // DOUBLE
                    "\".*\"|" +                  // STRING
                    "(true|false)|" +            // BOOLEAN
                    "'.'|" +                     // CHAR
                    "_?[a-zA-Z](_?[a-zA-Z0-9])*_?)" + // NAME
                    ")" +
                    "\\s*" +                         // whitespace after argument
                    "(,\\s*" +                       // optional additional arguments
                    "([+-]?\\d+|" +              // same pattern for additional args
                    "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" +
                    "\".*\"|" +
                    "(true|false)|" +
                    "'.'|" +
                    "_?[a-zA-Z](_?[a-zA-Z0-9])*_?)" +
                    "\\s*" +
                    ")*" +
                    ")?" +                               // end optional group for arguments
                    "\\)\\s*;";                          // closing parenthesis and semicolon
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile(FUNCTION_CALL_REGEX);

    /**
     * The matcher for the function call pattern.
     */
    public static final Matcher FUNCTION_CALL_MATCHER = FUNCTION_CALL_PATTERN.matcher("");

    /*
     * Error messages for function-related issues.
     */
    private final String RETURN_FROM_GLOBAL_SCOPE_ERROR = "Can't return from a global scope!";
    private final String NESTED_FUNCTION_ERROR = "Can't create nested function!";
    private final String FUNCTION_DECLARATION_ERROR = "Not a valid function declaration!";
    private final String FUNCTION_CALL_ERROR = "Not a valid function call!";
    private final String FUNCTIONS_WITH_SAME_NAME_ERROR = "Can't declare two functions with the same name!";
    private final String ARGUMENT_NOT_INITIALIZED_ERROR =
            "Can't call a function with uninitialized argument!";
    private final String INVALID_ARGUMENT_ERROR = "Invalid argument was passed to the function!";
    private final String WRONG_NUM_OF_ARGUMENTS_ERROR = "Wrong number of arguments foe the function!";
    private final String ARGUMENT_TYPE_ERROR = "The type of the argument is incorrect!";
    private final String NOT_RETURNING_ERROR = "Must return before ending the method!";
    private final String FUNCTION_DOESNT_EXIST_ERROR = "The function doesn't exist!";

    /*
     * Constants used for function handling.
     */
    private final String OPENING_PARENTHESES_REGEX = "\\(";
    private final String UNDER_SCORE = "_";
    private final char UNDER_SCORE_CHAR = '_';
    private final String COMMA = ",";
    private final String SPACE = " ";
    private final char OPENING_PARENTHESES = '(';
    private final char CLOSING_PARENTHESES = ')';
    private final String OPENING_PARENTHESES_STR = "(";
    private final String CLOSING_PARENTHESES_STR = ")";
    private final String SPACE_REGEX = "\\s+";
    private final String CLOSING_BRACKETS = "}";
    private final String FIRST_SCOPE = "_1";

    /*
     * Types, keywords and final, used for function handling.
     */
    private final String INT = "int";
    private final String DOUBLE = "double";
    private final String BOOLEAN = "boolean";
    private final String VOID = "void";
    private final String FINAL = "final";
    private final String IF = "if";
    private final String WHILE = "while";

    /*
     * Handlers of different cases and line types that may appear inside a function.
     */
    private final HandleIf ifHandler;
    private final HandleWhile whileHandler;
    private final HandleVariables variablesHandler;

    /**
     * Constructs a new HandleFunction object,
     * defines the handlers that will be used in this class.
     */
    public HandleFunction() {
        ifHandler = new HandleIf();
        whileHandler = new HandleWhile();
        variablesHandler = new HandleVariables();
    }

    /**
     * Processes a given line of code and determines if it is a function declaration,
     * function call, or another statement. Validates structure and ensures correct syntax.
     *
     * @param line The line of code to process.
     * @throws TypeOneException If a function-related syntax error is encountered.
     */
    public void handleFunction(String line) throws TypeOneException {
        HandleCodeLines.RETURN_MATCHER.reset(line);
        if (HandleCodeLines.RETURN_MATCHER.matches()) {
            if (HandleCodeLines.currScopeLevel == 0) {
                throw new ReturnException(RETURN_FROM_GLOBAL_SCOPE_ERROR);
            }
            return;
        }
        if (line.startsWith(VOID)) {
            if (HandleCodeLines.currScopeLevel > 0) {
                throw new NestedFunctionException(NESTED_FUNCTION_ERROR);
            }
            HandleCodeLines.currScopeLevel++;
            HandleCodeLines.localSymbolsTable = new HashMap<>();
            ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>> currentFunction;
            currentFunction =
                    HandleCodeLines.functionSymbols.
                            get(line.split(SPACE)[1].split(OPENING_PARENTHESES_REGEX)[0]);
            if (!(currentFunction == null)) {
                for (Map.Entry<Map.Entry<String, Boolean>, String> functionSymbol : currentFunction) {
                    HandleCodeLines.localSymbolsTable.put(functionSymbol.getValue() +
                            FIRST_SCOPE, Map.entry(functionSymbol.getKey().getKey(),
                            Map.entry(functionSymbol.getKey().getValue(), true)));
                }
            }
            return;
        }

        if (line.equals(CLOSING_BRACKETS)) {
            if (HandleCodeLines.currScopeLevel == 1 && !HandleCodeLines.isReturn) {
                throw new ReturnException(NOT_RETURNING_ERROR);
            }
            Iterator<String> iterator = HandleCodeLines.localSymbolsTable.keySet().iterator();
            while (iterator.hasNext()) {
                String var = iterator.next();
                String lastPart = var.substring(var.lastIndexOf(UNDER_SCORE_CHAR) + 1);
                int value = Integer.parseInt(lastPart);
                if (value == HandleCodeLines.currScopeLevel) {
                    iterator.remove();  // Safe removal using iterator
                }
            }
            HandleCodeLines.currScopeLevel--;
            return;
        }
        if (line.startsWith(IF)) {
            try {
                ifHandler.handleIfStatement(line);
                return;
            } catch (IfException e) {
                throw e;
            }
        }
        if (line.startsWith(WHILE)) {
            try {
                whileHandler.handleWhileStatement(line);
            } catch (WhileException e) {
                throw e;
            }
        } else {
            if (line.contains(OPENING_PARENTHESES_STR) && line.contains(CLOSING_PARENTHESES_STR)) {
                try {
                    handleFunctionCall(line);
                } catch (FunctionCallException exception) {
                    throw exception;
                }
            } else {
                try {
                    variablesHandler.defineAssignVariable(line);
                } catch (VariablesException e) {
                    throw e;
                }

            }
        }
    }

    /**
     * Validates and processes a function declaration.
     * Ensures that the function has a valid name, parameters, and is not redeclared.
     *
     * @param line The function declaration statement.
     * @throws FunctionDeclarationException If the function declaration is invalid.
     */
    public void handleFunctionDeclaration(String line) throws FunctionDeclarationException {
        FUNCTION_NAME_MATCHER.reset(line);
        if(!FUNCTION_NAME_MATCHER.matches()) {
            throw new FunctionDeclarationException(FUNCTION_DECLARATION_ERROR);
        }
        boolean isFinal = line.contains(FINAL);
        // Extract the part of the line before the opening parenthesis
        String name = line.substring(0, line.indexOf(OPENING_PARENTHESES));
        // Split by spaces and get the last element before '('
        name = name.split(SPACE)[1].trim();
        if (HandleCodeLines.functionSymbols.containsKey(name)){
            throw new FunctionDeclarationException(FUNCTIONS_WITH_SAME_NAME_ERROR);
        }
        // Extract parameters (assuming parameters are within parentheses)
        String parameterPart =
                line.substring(line.indexOf(OPENING_PARENTHESES) + 1, line.indexOf(CLOSING_PARENTHESES));
        String[] parameters = parameterPart.split(COMMA);
        ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>> innerArray = new ArrayList<>();
        for (String parameter : parameters) {
            parameter = parameter.trim(); // Clean up spaces
            if (!parameter.isEmpty()) {   // Ensure parameter is not empty
                String[] parameterArray = parameter.split(SPACE_REGEX);
                if (parameterArray[0].equals(FINAL)) {
                    parameterArray[0] = parameterArray[1];
                    parameterArray[1] = parameterArray[2];

                }
                innerArray.add(Map.entry(Map.entry(parameterArray[0], isFinal), parameterArray[1]));
            }
        }
        HandleCodeLines.functionSymbols.put(name, innerArray);
    }

    /*
     * Checks if the type of a function parameter matches the expected type.
     * This method allows implicit type conversions where applicable
     * (e.g., int to double, int/double to boolean).
     *
     * @param funcType The expected function parameter type.
     * @param callType The actual type being passed in the function call.
     * @return true if the types are compatible, false otherwise.
     */
    private boolean checkTypes(String funcType, String callType){
        if (!(callType.equals(funcType))) {
            if (funcType.equals(DOUBLE)) {
                if (!callType.equals(INT)) {
                    return false;
                }
            } else if (funcType.equals(BOOLEAN)) {
                if (!callType.equals(DOUBLE) && !callType.equals(INT)) {
                    return false;
                }
            }
            else{
                return false;
            }
        }
        return true;
    }

    /**
     * Validates and processes a function call.
     * Ensures that the function exists, has the correct number of arguments, and the correct argument types.
     *
     * @param line The function call statement.
     * @throws FunctionCallException If the function call is invalid.
     */
    public void handleFunctionCall(String line) throws FunctionCallException {
        FUNCTION_CALL_MATCHER.reset(line);
        if(!FUNCTION_CALL_MATCHER.matches()) {
            throw new FunctionCallException(FUNCTION_CALL_ERROR);
        }
        String name = line.split(OPENING_PARENTHESES_REGEX)[0].trim();
        String parameterPart = line.substring(line.indexOf(OPENING_PARENTHESES) + 1,
                line.indexOf(CLOSING_PARENTHESES));
        String[] parameters = parameterPart.split(COMMA);
        ArrayList<String> types = new ArrayList<>();
        for (String parameter : parameters) {
            parameter = parameter.trim(); // Clean up spaces
            if (parameter.isEmpty()){
                continue;
            }
            boolean isFound = false;
            for(int i = HandleCodeLines.currScopeLevel; i > -1; i--) {
                if(i == 0) {
                    if(HandleCodeLines.globalSymbolsTable.containsKey(parameter)) {

                        if (!HandleCodeLines.globalSymbolsTable.get(parameter).getValue().getValue()) {
                            throw new FunctionCallException(ARGUMENT_NOT_INITIALIZED_ERROR);
                        } else {
                            types.add(HandleCodeLines.globalSymbolsTable.get(parameter).getKey());
                            isFound = true;
                        }
                        break;
                    }
                }
                else if(HandleCodeLines.localSymbolsTable.containsKey(parameter + UNDER_SCORE + i)) {
                    parameter = parameter + UNDER_SCORE + i;
                    if (!HandleCodeLines.localSymbolsTable.get(parameter).getValue().getValue()) {
                        throw new FunctionCallException(ARGUMENT_NOT_INITIALIZED_ERROR);
                    } else {
                        types.add(HandleCodeLines.localSymbolsTable.get(parameter).getKey());
                        isFound = true;
                    }
                    break;
                }
            }
            if(!isFound) {
                String type = HandleCodeLines.findType(parameter);
                if(type != null) {
                    types.add(type);
                }
                else{
                    throw new FunctionCallException(INVALID_ARGUMENT_ERROR);
                }
            }
        }
        if (!HandleCodeLines.functionSymbols.containsKey(name)) {
            throw new FunctionCallException(FUNCTION_DOESNT_EXIST_ERROR);
        }
        if(types.size() != HandleCodeLines.functionSymbols.get(name).size()) {
            throw new FunctionCallException(WRONG_NUM_OF_ARGUMENTS_ERROR);
        }
        int counter = 0;
        for(String type : types) {
            if(!checkTypes( HandleCodeLines.functionSymbols.get(name).get(counter).getKey().getKey(),type)) {
                throw new FunctionCallException(ARGUMENT_TYPE_ERROR);
            }
            counter++;
        }
    }

}