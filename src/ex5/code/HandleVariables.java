package ex5.code;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the declaration and assignment of variables.
 * This class ensures that variables are properly defined, assigned, and checked for scope rules.
 * It supports global and local variable management while enforcing S-Java syntax rules.
 *
 * @author inbar.el and stavzok
 */
public class HandleVariables {
    /*
     * Regular expressions for detecting and validating different variable types and assignments.
     */
    private final String INT_PATTERN_ASSIGN =
            HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?\\d+|" + HandleCodeLines.NAME_REGEX +
                    "))?(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?\\d+|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final String DOUBLE_PATTERN_ASSIGN =
            HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" +
                    HandleCodeLines.NAME_REGEX + "))?" +
                    "(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final String STRING_PATTERN_ASSIGN =  HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(\".*\"|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\".*\"|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final String BOOLEAN_PATTERN_ASSIGN = HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final String CHAR_PATTERN_ASSIGN = HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(\'.\'|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\'.\'|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    /*
     * Pattern for validating variable of different types.
     */
    private final Pattern VARIABLE_PATTERN_ASSIGN =
            Pattern.compile(INT_PATTERN_ASSIGN + "|" + DOUBLE_PATTERN_ASSIGN +"|" + STRING_PATTERN_ASSIGN +
                    "|" + BOOLEAN_PATTERN_ASSIGN +"|" + CHAR_PATTERN_ASSIGN );

    private final Matcher VARIABLE_MATCHER_ASSIGN = VARIABLE_PATTERN_ASSIGN.matcher("");

    private final String INT_PATTERN =
            "(final\\s+)?int\\s+"+ HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?\\d+|" +
                    HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?\\d+|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final String DOUBLE_PATTERN =
            "(final\\s+)?double\\s+" + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|"
                    + HandleCodeLines.NAME_REGEX + "))?" +
                    "(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private String STRING_PATTERN =  "(final\\s+)?String\\s+"+ HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\".*\"|"
            + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\".*\"|" +
            HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private String BOOLEAN_PATTERN = "(final\\s+)?boolean\\s+"+ HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final String CHAR_PATTERN = "(final\\s+)?char\\s+"+ HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*('.'|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*('.'|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    public final Pattern VARIABLE_PATTERN =
            Pattern.compile(INT_PATTERN + "|" + DOUBLE_PATTERN +"|" + STRING_PATTERN +
                    "|" + BOOLEAN_PATTERN +"|" + CHAR_PATTERN );
    public final Matcher VARIABLE_MATCHER = VARIABLE_PATTERN.matcher("");

    /*
     * Data types supported in the program and final.
     */
    private final HashSet<String> typesAndFinal = new HashSet<>(Arrays.asList("int", "double", "String", "char",
            "boolean", "final"));

    /*
     * Error messages for variable-related issues.
     */
    private final String TYPE_ERROR = "The type is not recognized!";
    private final String TYPE_ASSIGNMENT_ERROR = "The assigned value is not in the right type!";
    private final String FINALE_INITIALIZATION_ERROR = "Final variable must be initialized!";
    private final String VARIABLE_EXISTS_ERROR = "Variable already exists!";
    private final String NOT_INITIALIZED_ERROR = "Argument not initialized!";
    private final String TYPE_OR_INITIALIZATION_ERROR = "Invalid type or uninitialized argument!";
    private final String VARIABLE_DOESNT_EXIST_ERROR = "Variable doesn't exist!";
    private final String FINAL_VARIABLE_CHANE_ERROR = "Can't assign into final variable";
    private final String INVALID_ASSIGN_ERROR = "Not a valid assignment expression!";
    private final String INVALID_DEFINE_ERROR = "Not a valid define expression!";
    private final String GLOBAL_CALL_FUNCTION_EXCEPTION = "Can't call a function from global scope!";

    /*
     * Type names used in the program.
     */
    private final String DOUBLE = "double";
    private final String INT = "int";
    private final String BOOLEAN = "boolean";
    private final String FINAL = "final";
    private final String TRUE = "true";
    private final String FALSE = "false";

    /*
     * Symbols used for parsing and validation.
     */
    private final String COMMA = ",";
    private final String EQUALS = "=";
    private final String UPPER_COMMA = "'";
    private final String SLASH_REGEX = "\"";
    private final String UNDER_SCORE = "_";
    private final String LINE_END = ";";
    private final String EQUALS_REGEX = "\\s*=\\s*";
    private final String EMPTY_STR = "";
    private final String SPACE = " ";
    private final char EMPTY_CHAR = ' ';


    private void handleLiterals(String type, String rightName) throws VariablesException{
        String typeRight;
        typeRight = HandleCodeLines.findType(rightName);
        if (typeRight == null) {
            throw new VariablesException(TYPE_ERROR);
        }

        if (!(typeRight.equals(type))) {
            if (type.equals(DOUBLE)) {
                if (!typeRight.equals(INT)) {
                    throw new VariablesException(TYPE_ASSIGNMENT_ERROR);
                }
            } else if (type.equals(BOOLEAN)) {
                if (!typeRight.equals(DOUBLE) && !typeRight.equals(INT)) {
                    throw new VariablesException(TYPE_ASSIGNMENT_ERROR);
                }
            }
        }
        
    }

    private boolean checkTypes(String type, String typeRight){
        if (!(typeRight.equals(type))) {
            if (type.equals(DOUBLE)) {
                if (!typeRight.equals(INT)) {
                    return false;
                }
            } else if (type.equals(BOOLEAN)) {
                if (!typeRight.equals(DOUBLE) && !typeRight.equals(INT)) {
                    return false;
                }
            }
            else{
                return false;
            }
        }
        return true;
    }
    
    private void handleDefineLocalVariable(String line, String type) throws VariablesException{
        boolean isInitialized;
        boolean isFinal;
        String[] myArray = line.split(COMMA);
        for(int i = 0; i < myArray.length; i++){
            boolean[] results = checkFinalAndInitialized(myArray[i], myArray[0]);
            isInitialized = results[0];
            isFinal = results[1];
            String name;
            if(isInitialized) {
                // Remove unnecessary spaces and the trailing semicolon.
                myArray[i] = myArray[i].replace(LINE_END, EMPTY_STR).trim();
                // Split the declaration by '=' to separate the variable name and assigned value.
                String[] parts = myArray[i].split(EQUALS_REGEX);
                name = parts[0].substring(parts[0].lastIndexOf(EMPTY_CHAR) + 1) +
                        UNDER_SCORE + HandleCodeLines.currScopeLevel;
                boolean isFound = false;
                // Check if the variable already exists in the current local scope.
                if (HandleCodeLines.localSymbolsTable.containsKey(name)){
                    throw new VariablesException(VARIABLE_EXISTS_ERROR); //check if variable exists in the scope
                }
                String origRightName = parts[1];
                // Check if the right-hand side of the assignment is another variable.
                if(parts[1].matches(HandleCodeLines.NAME_REGEX) && (!parts[1].equals(TRUE))
                        && (!parts[1].equals(FALSE))) { //check right side - assigned variable (not literal)
                    // Look for the variable in the current and higher scope levels.
                    try {
                        if(iterateOverLocalSymbolTable(type, origRightName) != -1){
                            HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                        }
                    }
                    catch (VariablesException e){
                        throw e;
                    }
                    // If the variable wasn't found locally, check the global symbol table.
                    if (!isFound) {
                        if (HandleCodeLines.globalSymbolsTable.containsKey(origRightName)) {
                            String typeRight = HandleCodeLines.globalSymbolsTable.get(origRightName).getKey();
                            if (!HandleCodeLines.globalSymbolsTable.get(origRightName).getValue().getValue() ||
                                    !(checkTypes(type, typeRight))) {
                                throw new VariablesException(TYPE_OR_INITIALIZATION_ERROR);
                            }
                            HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                        }
                    }
                }
                else {
                    // Validate the assigned value to ensure it matches the expected type.
                    try {
                        handleLiterals(type, parts[1]);
                    } catch (VariablesException e) {
                        throw e;
                    }
                    // Store the new variable in the local symbol table.
                    HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                }
            }
            // Variable is declared but not assigned a value.
            else{
                try {
                    handleUnassignedLocalVariable(myArray[i], isFinal, isInitialized, type);
                } catch (VariablesException e) {
                    throw e;
                }
            }
        }
    }

    private void handleUnassignedLocalVariable(String arrayI, boolean isFinal,
                                                      boolean isInitialized, String type) throws VariablesException{
        arrayI = arrayI.replace(LINE_END , EMPTY_STR).trim();
        // Extract the variable name from the declaration.
        String name;
        name = arrayI.substring(arrayI.lastIndexOf(EMPTY_CHAR) + 1) +
                UNDER_SCORE + HandleCodeLines.currScopeLevel;
        // Check if the variable already exists in the local scope.
        if (HandleCodeLines.localSymbolsTable.containsKey(name)){
            throw new VariablesException(VARIABLE_EXISTS_ERROR);
        }
        // Store the variable in the local symbol table without initialization.
        HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
    }

    private boolean[] checkFinalAndInitialized(String arrayI, String array0) throws VariablesException {
        boolean isInitialized = false;
        boolean isFinal = false;
        // Check if the variable is assigned a value at declaration
        if (arrayI.split(SLASH_REGEX)[0].contains(EQUALS) || arrayI.split(UPPER_COMMA)[0].contains(EQUALS)) {
            isInitialized = true;
        }
        // Check if the variable is declared as final
        if(array0.contains(FINAL)) {
            isFinal = true;
            if(!isInitialized){
                throw new VariablesException(FINALE_INITIALIZATION_ERROR);
            }
        }
        return new boolean[]{isInitialized, isFinal};
    }

    private int iterateOverLocalSymbolTable(String type, String rightName) throws VariablesException {
        for (int j = HandleCodeLines.currScopeLevel; j > 0; j--) {
            if (HandleCodeLines.localSymbolsTable.containsKey(rightName + UNDER_SCORE + j)) {
                rightName = rightName + UNDER_SCORE + j;
                String typeRight = HandleCodeLines.localSymbolsTable.get(rightName).getKey();

                if (!HandleCodeLines.localSymbolsTable.get(rightName).getValue().getValue() ||
                        !(checkTypes(type, typeRight))) {
                    throw new VariablesException(NOT_INITIALIZED_ERROR);
                }
                return j;
            }
        }
        return -1;
    }

    private int iterateOverLocalSymbolTableLeft( String rightName) throws VariablesException {
        for (int j = HandleCodeLines.currScopeLevel; j > 0; j--) {
            if (HandleCodeLines.localSymbolsTable.containsKey(rightName + UNDER_SCORE + j)) {
                return j;
            }
        }
        return -1;
    }

    private void checkAssignedVariableValidity(String name, boolean isGlobal) throws VariablesException {
        HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> table;
        if (isGlobal) {
            table = HandleCodeLines.globalSymbolsTable;
        }
        else {
            table = HandleCodeLines.localSymbolsTable;
        }

        if (table.get(name).getValue().getKey().equals(Boolean.TRUE)) {
            throw new VariablesException(FINAL_VARIABLE_CHANE_ERROR);
        }
    }

    private void handleLeftLocal(String name, String rightName, boolean isGlobal) throws VariablesException {
        HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> table;

        if (isGlobal) {
            table = HandleCodeLines.globalSymbolsTable;
        }
        else {
            table = HandleCodeLines.localSymbolsTable;
            name = name + UNDER_SCORE + iterateOverLocalSymbolTableLeft(name);
        }

        try {
            checkAssignedVariableValidity(name, isGlobal);
        }

        catch (VariablesException e) {
            throw e;
        }

        String type;
        type = table.get(name).getKey();
        if (isGlobal){
            name = name + UNDER_SCORE + iterateOverLocalSymbolTableLeft(name);
        }

        if (rightName.matches(HandleCodeLines.NAME_REGEX) && (!rightName.equals(TRUE))
                && (!rightName.equals(FALSE))) {
            try {
                if (iterateOverLocalSymbolTable(type, rightName) != -1) {
                    HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(false, true)));
                    return;
                }
            }
            catch (VariablesException e) {
                throw e;
            }
            if (HandleCodeLines.globalSymbolsTable.containsKey(rightName)) {
                String typeRight = HandleCodeLines.globalSymbolsTable.get(rightName).getKey();

                if (!HandleCodeLines.globalSymbolsTable.get(rightName).getValue().getValue() ||
                        !(checkTypes(type, typeRight))) {
                    throw new VariablesException(TYPE_OR_INITIALIZATION_ERROR);
                }
                HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(false, true)));
            }
        }
        else {
            try {
                handleLiterals(type, rightName);
            } catch (VariablesException e) {
                throw e;
            }
            HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(false, true)));
        }
    }

    private void handleAssignLocalVariable(String line) throws VariablesException {
        String[] myArray = line.split(COMMA);
        for (int i = 0; i < myArray.length; i++) {
            if (!(myArray[i].split(SLASH_REGEX)[0].contains(EQUALS) || myArray[i].split(UPPER_COMMA)[0].contains(EQUALS))) {
                throw new VariablesException(INVALID_ASSIGN_ERROR);
            }
            String name;
            myArray[i] = myArray[i].replace(LINE_END, EMPTY_STR).trim();
            // Extract variable name and assigned value
            String[] parts = myArray[i].split(EQUALS_REGEX);
            name = parts[0].substring(parts[0].lastIndexOf(EMPTY_CHAR) + 1);
            try {
                if (HandleCodeLines.globalSymbolsTable.containsKey(name)) {
                    handleLeftLocal(name, parts[1], true);
                    return;
                }
                else if (HandleCodeLines.localSymbolsTable.containsKey(name + UNDER_SCORE +
                        iterateOverLocalSymbolTableLeft(name))) {
                    handleLeftLocal(name, parts[1], false);
                    return;
                } else {
                    throw new VariablesException(VARIABLE_DOESNT_EXIST_ERROR);
                }
            } catch (VariablesException e) {
                throw e;
            }
        }
    }

    private void handleDefineGlobalVariable(String line,String type) throws VariablesException {
        boolean isInitialized;
        boolean isFinal;
        String[] myArray = line.split(COMMA);
        for(int i = 0; i < myArray.length; i++){
            boolean[] results = checkFinalAndInitialized(myArray[i], myArray[0]);
            isInitialized = results[0];
            isFinal = results[1];
            String name;
            if(isInitialized) {
                // Remove unnecessary spaces and semicolons
                myArray[i] = myArray[i].replace(LINE_END, EMPTY_STR).trim();
                // Extract variable name and assigned value
                String[] parts = myArray[i].split(EQUALS_REGEX);
                name = parts[0].substring(parts[0].lastIndexOf(EMPTY_CHAR) + 1);
                // Ensure the variable does not already exist
                if (HandleCodeLines.globalSymbolsTable.containsKey(name)){
                    throw new VariablesException(VARIABLE_EXISTS_ERROR);
                }
                // Check if the assigned value is another variable
                if(parts[1].matches(HandleCodeLines.NAME_REGEX) && (!parts[1].equals(TRUE))
                        && (!parts[1].equals(FALSE))) {
                    // Ensure the assigned variable is initialized and of the correct type
                    if (HandleCodeLines.globalSymbolsTable.containsKey(parts[1])) {
                        String typeRight = HandleCodeLines.globalSymbolsTable.get(parts[1]).getKey();

                        if (HandleCodeLines.globalSymbolsTable.get(parts[1]).getValue().getValue().equals(Boolean.FALSE)
                                || !(checkTypes(type, typeRight))) {
                            throw new VariablesException(TYPE_OR_INITIALIZATION_ERROR);
                        }
                    }
                    else{
                        throw new VariablesException(VARIABLE_DOESNT_EXIST_ERROR);

                    }
                    // Store variable in the global symbol table
                    HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                }
                else {
                    // Validate the assigned value type
                    try {
                        handleLiterals(type, parts[1]);
                    }
                    catch (VariablesException e){
                        throw e;
                    }
                    // Store variable in the global symbol table
                    HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                }
            }
            else{
                // Variable is declared but not assigned a value
                myArray[i] = myArray[i].replace(LINE_END, EMPTY_STR).trim();
                // Extract variable name
                name = myArray[i].substring(myArray[i].lastIndexOf(' ') + 1);
                // Ensure the variable does not already exist
                if (HandleCodeLines.globalSymbolsTable.containsKey(name)){
                    throw new VariablesException(VARIABLE_EXISTS_ERROR); //check if variable exists in the scope
                }
                // Store uninitialized variable in the global symbol table
                HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
        }
    }

    private void handleAssignGlobalVariable(String line) throws VariablesException {
        boolean isInitialized;
        boolean isFinal = false;
        String[] myArray = line.split(COMMA);
        for(int i = 0; i < myArray.length; i++) {
            isInitialized = false;
            // Ensure assignment syntax is valid
            if (!(myArray[i].split(SLASH_REGEX)[0].contains(EQUALS) ||
                    myArray[i].split(UPPER_COMMA)[0].contains(EQUALS))) {
                throw new VariablesException(INVALID_ASSIGN_ERROR);
            }
            String name;
            String type;
            myArray[i] = myArray[i].replace(LINE_END, EMPTY_STR).trim();
            // Extract variable name and assigned value
            String[] parts = myArray[i].split(EQUALS_REGEX);
            name = parts[0].substring(parts[0].lastIndexOf(EMPTY_CHAR) + 1);
            // Ensure the variable exists in the global scope
            if (HandleCodeLines.currScopeLevel == 0){
                isInitialized = true;
            }
            if (!HandleCodeLines.globalSymbolsTable.containsKey(name)) {
                throw new VariablesException(VARIABLE_DOESNT_EXIST_ERROR);
            }
            // Prevent modification of final variables
            if (HandleCodeLines.globalSymbolsTable.get(name).getValue().getKey().equals(Boolean.TRUE) &&
                    (HandleCodeLines.globalSymbolsTable.get(name).getValue().getValue().equals(Boolean.TRUE))) {
                throw new VariablesException(FINAL_VARIABLE_CHANE_ERROR);
            }
            type = HandleCodeLines.globalSymbolsTable.get(name).getKey();
            // Check if assigned value is another variable
            if (parts[1].matches(HandleCodeLines.NAME_REGEX) && (!parts[1].equals(TRUE))
                    && (!parts[1].equals(FALSE)))  {
                // Ensure the assigned variable exists and is initialized
                if (HandleCodeLines.globalSymbolsTable.containsKey(parts[1])) {
                    String typeRight = HandleCodeLines.localSymbolsTable.get(parts[1]).getKey();

                    if (!HandleCodeLines.globalSymbolsTable.get(parts[1]).getValue().getValue() ||
                            !(checkTypes(type, typeRight))) {
                        throw new VariablesException(INVALID_ASSIGN_ERROR);
                    }
                }
                else{
                    throw new VariablesException(VARIABLE_DOESNT_EXIST_ERROR);
                }
                // Store updated value in the global symbol table
                HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
            else {
                // Ensure the assigned value matches the variable type
                try {
                    handleLiterals(type, parts[1]);
                }
                catch (VariablesException e){
                    throw e;
                }
                // Store updated value in the global symbol table
                HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
        }
    }

    public void defineAssignVariable(String line) throws TypeOneException, FunctionCallException {
        // check if global / local
        String type;
        boolean isGlobal = false;
        if(HandleCodeLines.currScopeLevel == 0) {
            isGlobal = true;
            HandleFunction.FUNCTION_CALL_MATCHER.reset(line);
            if(HandleFunction.FUNCTION_CALL_MATCHER.matches()) {
                throw new FunctionCallException(GLOBAL_CALL_FUNCTION_EXCEPTION);
            }
        }

        // check define / assign
        if(typesAndFinal.contains(line.split(SPACE)[0])) {
            type = line.split(SPACE)[0];
            if (type.equals(FINAL)){
                type = line.split(SPACE)[1];
            }
            VARIABLE_MATCHER.reset(line);
            if (!VARIABLE_MATCHER.matches()) {
                throw new VariablesException(INVALID_DEFINE_ERROR);
            }
            if (isGlobal){
                try {
                    handleDefineGlobalVariable(line, type);
                }
                catch (VariablesException e) {
                    throw e;
                }
            }
            else{
                try {
                    handleDefineLocalVariable(line, type);
                }
                catch(VariablesException e) {
                    throw e;
                }
            }
        }
        else {
            VARIABLE_MATCHER_ASSIGN.reset(line);
            if (!VARIABLE_MATCHER_ASSIGN.matches()) {
                throw new VariablesException(INVALID_ASSIGN_ERROR);
            }
            if (isGlobal){
                try {
                    handleAssignGlobalVariable(line);
                }
                catch (VariablesException e) {
                    throw e;
                }
            }
            else{
                try {
                    handleAssignLocalVariable(line);
                }
                catch(VariablesException e) {
                    throw e;
                }
            }
        }
    }
    
}
