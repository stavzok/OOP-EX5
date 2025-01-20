package ex5.code;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleCodeLines {
    private ArrayList<String> codeLines;
    private static final Pattern COMMENT_START = Pattern.compile("^.*\\\\");
    private static final Matcher COMMENT_START_MATCHER = COMMENT_START.matcher("");
    private static final String NAME_REGEX = "_?[a-zA-Z](_?[a-zA-Z0-9])*_?";
    private final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private final Matcher NAME_MATCHER = NAME_PATTERN.matcher("");
    private final String PATTERN_TYPE = "(int|double|String|boolean|char)";
    private final String FUNCTION_NAME_REGEX = "[a-zA-Z](_?[a-zA-Z0-9])*_?\\s*\\((\\s*final?\\s*" +
            PATTERN_TYPE + "\\s*" + NAME_REGEX + "\\s*(,\\s*final?\\s*" + PATTERN_TYPE + "\\s*" + NAME_REGEX + ")*)?\\)\\s*";
    private final String INT_REGEX = "[+-]?\\d+";
    private final String DOUBLE_REGEX = "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)";
    private final String INT_PATTERN =
            "(final\\s+)?int\\s+"+ NAME_REGEX + "(\\s*=\\s*([+-]?\\d+|" + NAME_REGEX + "))?(\\s*,\\s*" + NAME_REGEX +
                    "(\\s*=\\s*([+-]?\\d+|" + NAME_REGEX + "))?)*\\s*;";
    private  final String DOUBLE_PATTERN =
            "(final\\s+)?double\\s+" + NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + NAME_REGEX + "))?" +
                    "(\\s*,\\s*" + NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + NAME_REGEX + "))?)*\\s*;";
    private final String STRING_PATTERN =  "(final\\s+)?String\\s+"+ NAME_REGEX + "(\\s*=\\s*(\".*\"|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(\".*\"|" + NAME_REGEX + "))?)*\\s*;";
    private final String BOOLEAN_PATTERN = "(final\\s+)?boolean\\s+"+ NAME_REGEX + "(\\s*=\\s*(true|false|" + INT_REGEX +
            "|" + DOUBLE_REGEX + "|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(true|false|" + INT_REGEX +
            "|" + DOUBLE_REGEX + "|" + NAME_REGEX + "))?)*\\s*;";

    private final String CHAR_PATTERN = "(final\\s+)?char\\s+"+ NAME_REGEX + "(\\s*=\\s*('.'|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*('.'|" + NAME_REGEX + "))?)*\\s*;";
    private final Pattern VARIABLE_PATTERN =
            Pattern.compile(INT_PATTERN + "|" + DOUBLE_PATTERN +"|" + STRING_PATTERN +
                    "|" + BOOLEAN_PATTERN +"|" + CHAR_PATTERN );
    private final Matcher VARIABLE_MATCHER = VARIABLE_PATTERN.matcher("");
    private final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME_REGEX + "\\{");
    private final Matcher FUNCTION_NAME_MATCHER = FUNCTION_NAME_PATTERN.matcher("");
    private final String INT_PATTERN_ASSIGN =
            NAME_REGEX + "(\\s*=\\s*([+-]?\\d+|" + NAME_REGEX + "))?(\\s*,\\s*" + NAME_REGEX +
                    "(\\s*=\\s*([+-]?\\d+|" + NAME_REGEX + "))?)*\\s*;";
    private  final String DOUBLE_PATTERN_ASSIGN =
            NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + NAME_REGEX + "))?" +
                    "(\\s*,\\s*" + NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + NAME_REGEX + "))?)*\\s*;";
    private final String STRING_PATTERN_ASSIGN =  NAME_REGEX + "(\\s*=\\s*(\".*\"|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(\".*\"|" + NAME_REGEX + "))?)*\\s*;";
    private final String BOOLEAN_PATTERN_ASSIGN = NAME_REGEX + "(\\s*=\\s*(true|false|" + INT_REGEX +
            "|" + DOUBLE_REGEX + "|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(true|false|" + INT_REGEX +
            "|" + DOUBLE_REGEX + "|" + NAME_REGEX + "))?)*\\s*;";
    private final String CHAR_PATTERN_ASSIGN = NAME_REGEX + "(\\s*=\\s*(\'.\'|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(\'.\'|" + NAME_REGEX + "))?)*\\s*;";
    private  final Pattern VARIABLE_PATTERN_ASSIGN =
            Pattern.compile(INT_PATTERN_ASSIGN + "|" + DOUBLE_PATTERN_ASSIGN +"|" + STRING_PATTERN_ASSIGN +
                    "|" + BOOLEAN_PATTERN_ASSIGN +"|" + CHAR_PATTERN_ASSIGN );
    private final Matcher VARIABLE_MATCHER_ASSIGN = VARIABLE_PATTERN_ASSIGN.matcher("");
    private int currScopeLevel = 0;
    // name: type: final: initialized
    private HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> globalSymbolStable = new HashMap<>();

    // name: type: final: initialized
    private HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> localSymbolStable = new HashMap<>();
    private HashMap<String, ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>>> functionSymbols = new HashMap<>();
    private final HashSet<String> typesAndFinal = new HashSet<>(Arrays.asList("int", "double", "String", "char",
            "boolean", "final"));
    private final HashMap<Integer, ArrayList<String>> activeScopes = new HashMap<>();
    private final String IF_CONDITION_CONSTANT = "if\\s*\\((true|false|" + INT_REGEX + "|" + DOUBLE_REGEX + "|" +
            NAME_REGEX + ")\\s*(\\|\\||&&)\\s*\\((true|false|" + INT_REGEX + "|" + DOUBLE_REGEX + "|" + NAME_REGEX
            + ")\\)\\s*\\)\\s*\\{";
    private final Pattern IF_CONDITION_PATTERN = Pattern.compile(IF_CONDITION_CONSTANT);
    private final Matcher IF_CONDITION_MATCHER = IF_CONDITION_PATTERN.matcher("");
    private final String WHILE_CONDITION_CONSTANT = "while\\s*\\((true|false|" + INT_REGEX + "|" + DOUBLE_REGEX + "|" +
            NAME_REGEX + ")\\s*(\\|\\||&&)\\s*\\((true|false|" + INT_REGEX + "|" + DOUBLE_REGEX + "|" + NAME_REGEX
            + ")\\)\\s*\\)\\s*\\{";
    private final Pattern WHILE_CONDITION_PATTERN = Pattern.compile(WHILE_CONDITION_CONSTANT);
    private final Matcher WHILE_CONDITION_MATCHER = WHILE_CONDITION_PATTERN.matcher("");

    private final String FUNCTION_CALL_REGEX = "[a-zA-Z](_?[a-zA-Z0-9])*_?\\s*\\((\\s*" + VARIABLE_PATTERN + "|"
            + NAME_REGEX + "\\s*(,\\s*" + VARIABLE_PATTERN + "|" + NAME_REGEX + "\\s*)*)?\\)\\s*";
    private final Pattern FUNCTION_CALL_PATTERN = Pattern.compile(FUNCTION_CALL_REGEX);
    private final Matcher FUNCTION_CALL_MATCHER = FUNCTION_CALL_PATTERN.matcher("");

    private final String RETURN_REGEX = "return\\s*;";
    private final Pattern RETURN_PATTERN = Pattern.compile(RETURN_REGEX);
    private final Matcher RETURN_MATCHER = RETURN_PATTERN.matcher("");

    private boolean isReturn = false;

    // errors
    private final String FUNCTION_DECLARATION_ERROR = "The function declaration is illegal!";


    public HandleCodeLines(ArrayList<String> codeLines) {
        this.codeLines = codeLines;
    }

    public void handleLines() throws TypeOneException {
        for(String codeLine: codeLines) {
            codeLine.trim();
            codeLine = codeLine.replaceAll("\\s+", " ");
            if(codeLine.startsWith("void")) {
                try {
                    handleFunctionDeclaration(codeLine);
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

        line = line.trim();
        if(line.startsWith("/*")) {
            throw new CommentException("Comment format is illegal!");
        }

        if (COMMENT_START_MATCHER.reset(line).find()) {
            throw new CommentException("Comment format is illegal!");
        }

        if (line.startsWith("\\\\")) {
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
                handleFunction(line);
            }
            catch(TypeOneException e) {
                throw e;
            }
        }

        if (currScopeLevel == 0) {
            if (line.startsWith("void")){
                try {
                    handleFunction(line);
                }
                catch(TypeOneException e) {
                    throw e;
                }
            }
            else {
                try {
                    defineAssignVariable(line);
                }
                catch(VariablesException e) {
                    throw e;
                }
            }
        }
    }

    private void handleFunction(String line) throws TypeOneException {

        RETURN_MATCHER.reset(line);
        if(RETURN_MATCHER.matches()) {
            if(currScopeLevel == 0) {
                throw new ReturnException("Can't return from a global scope!");
            }
        }

        if (line.startsWith("void")) {
            if(currScopeLevel > 0) {
                throw new NestedFunctionException("Can't create nested function!");
            }
            currScopeLevel++;
            localSymbolStable = new HashMap<>();
            ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>> currentFunction;
            currentFunction =
                    functionSymbols.get(line.split(" ")[1].split("\\(")[0]);
            for(Map.Entry<Map.Entry<String, Boolean>, String> functionSymbol: currentFunction) {
                localSymbolStable.put(functionSymbol.getValue() + "_1", Map.entry(functionSymbol.getKey().getKey(),
                        Map.entry(functionSymbol.getKey().getValue(), true)));
            }
            return;
        }

        if(line.equals("}")) {
            if(currScopeLevel == 1 && !isReturn) {
                throw new ReturnException("Must return before ending the method!");
            }
            currScopeLevel--;
        }

        if(line.contains("if")) {
            try {
                handleIfStatement(line);
            }
            catch(IfException e) {
              throw e;
            }
        }

        if(line.contains("while")) {
            try {
                handleWhileStatement(line);
            }
            catch(WhileException e) {
                throw e;
            }

        }

        else {
            if(line.contains("\\(") && line.contains("\\)")) {
                try {
                    handleFunctionCall(line);
                }
                catch (FunctionCallException exception) {
                    throw exception;
                }
            }
            else {
                try {
                    defineAssignVariable(line);
                }
                catch(VariablesException e) {
                    throw e;
                }

            }
        }
    }

    private void handleFunctionCall(String line) throws FunctionCallException {
        FUNCTION_CALL_MATCHER.reset(line);
        if(!FUNCTION_CALL_MATCHER.matches()) {
            throw new FunctionCallException("Wrong call format!");
        }

        String name = line.split(" ")[0];
        String parameterPart = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        String[] parameters = parameterPart.split(",");
        ArrayList<String> types = new ArrayList<>();
        for (String parameter : parameters) {
            parameter = parameter.trim(); // Clean up spaces
            if (parameter.isEmpty()){
                continue;

            }
            boolean isFound = false;
            for(int i = currScopeLevel; i > -1; i--) {
                if(i == 0) {
                    if(globalSymbolStable.containsKey(parameter)) {

                        if (!globalSymbolStable.get(parameter).getValue().getValue()) {
                            throw new FunctionCallException("Argument not initialized!");
                        } else {
                            types.add(globalSymbolStable.get(parameter).getKey());
                            isFound = true;
                        }
                        break;
                    }
                }

                else if(localSymbolStable.containsKey(parameter + "_" + i)) {
                    parameter = parameter + "_" + i;
                    if (!localSymbolStable.get(parameter).getValue().getValue()) {
                        throw new FunctionCallException("Argument not initialized!");
                    } else {
                        types.add(localSymbolStable.get(parameter).getKey());
                        isFound = true;
                    }
                    break;
                }

            }

            if(!isFound) {
                String type = findType(parameter);
                if(type != null) {
                    types.add(type);
                }
                else{
                    throw new FunctionCallException("Argument not valid!");
                }

            }


        }

        if(types.size() != functionSymbols.get(name).size()) {
            throw new FunctionCallException("Wrong number of arguments!");
        }

        int counter = 0;
        for(String type : types) {
            if(!type.equals(functionSymbols.get(name).get(counter).getKey().getKey())) {
                throw new FunctionCallException("Argument type mismatch!");
            }
        }
    }

    private String findType(String parameter) {

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

    private void handleWhileStatement(String line) throws WhileException {
        WHILE_CONDITION_MATCHER.reset(line);
        if(WHILE_CONDITION_MATCHER.matches()) {
            NAME_MATCHER.reset(line);
            HashSet<String> validTypes = new HashSet<>(Arrays.asList("int", "double", "boolean"));
            while(NAME_MATCHER.find()) {
                String foundName = NAME_MATCHER.group();
                if(localSymbolStable.containsKey(foundName) &&
                        validTypes.contains(localSymbolStable.get(foundName).getKey()) &&
                        localSymbolStable.get(foundName).getValue().getValue()) {
                    currScopeLevel++;
                }
                else if(globalSymbolStable.containsKey(foundName) &&
                        validTypes.contains(globalSymbolStable.get(foundName).getKey()) &&
                        globalSymbolStable.get(foundName).getValue().getValue()) {
                    currScopeLevel++;
                }
                else {
                    throw new WhileException("While statement is illegal!");
                }
            }
        }
    }

    private void handleIfStatement(String line) throws IfException {
        IF_CONDITION_MATCHER.reset(line);
        if(IF_CONDITION_MATCHER.matches()) {
            NAME_MATCHER.reset(line);
            HashSet<String> validTypes = new HashSet<>(Arrays.asList("int", "double", "boolean"));
            while(NAME_MATCHER.find()) {
                String foundName = NAME_MATCHER.group();
                if(localSymbolStable.containsKey(foundName) &&
                validTypes.contains(localSymbolStable.get(foundName).getKey()) &&
                localSymbolStable.get(foundName).getValue().getValue()) {
                    currScopeLevel++;
                }
                else if(globalSymbolStable.containsKey(foundName) &&
                        validTypes.contains(globalSymbolStable.get(foundName).getKey()) &&
                        globalSymbolStable.get(foundName).getValue().getValue()) {
                    currScopeLevel++;
                }
                else {
                    throw new IfException("If statement is illegal!");
                }
            }
        }
    }

    private void handleFunctionDeclaration(String line) throws FunctionDeclarationException {

        FUNCTION_NAME_MATCHER.reset(line);
        if(!FUNCTION_NAME_MATCHER.matches()) {
            throw new FunctionDeclarationException(FUNCTION_DECLARATION_ERROR);
        }

        boolean isFinal = false;
        if(line.contains("final")) {
            isFinal = true;
        }

        // Extract the part of the line before the opening parenthesis
        String name = line.substring(0, line.indexOf('(')).trim();

        // Split by spaces and get the last element before '('
        name = name.split(" ")[1];

        // Extract parameters (assuming parameters are within parentheses)
        String parameterPart = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        String[] parameters = parameterPart.split(",");
        ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>> innerArray = new ArrayList<>();
        for (String parameter : parameters) {
            parameter = parameter.trim(); // Clean up spaces
            if (!parameter.isEmpty()) {   // Ensure parameter is not empty
                String[] parameterArray = parameter.split("\\s+");
                innerArray.add(Map.entry(Map.entry(parameterArray[0], isFinal), parameterArray[1])); // type and then name
            }
        }
        functionSymbols.put(name, innerArray);
    }

    private void defineAssignVariable(String line) throws VariablesException {
        // check if global / local
        HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> table;
        if(currScopeLevel == 0) {
            table = globalSymbolStable;
        }
        else {
            table = localSymbolStable;
        }

        // check define / assign
        if(typesAndFinal.contains(line.split(" ")[0])) {
            VARIABLE_MATCHER.reset(line);
            if (!VARIABLE_MATCHER.matches()) {
                throw new VariablesException("Not a valid define format!");
            }
        }

        else {
            VARIABLE_MATCHER_ASSIGN.reset(line);
            if (!VARIABLE_MATCHER_ASSIGN.matches()) {
                throw new VariablesException("Not a valid assign format!");
            }
        }

        boolean isInitialized = false;
        boolean isFinal = false;

        String[] myArray = line.split(",");

        for(int i = 0; i < myArray.length; i++){

            if (myArray[i].split("\"")[0].contains("=") || myArray[i].split("'")[0].contains("=")) {
                isInitialized = true;
            }

            if(myArray[i].contains("final")) {
                isFinal = true;
            }

            String type;
            String name;

            if(isInitialized) {
                myArray[i] = myArray[i].replace(";", "").trim();

                // Split by '=' after removing unnecessary spaces around it
                String[] parts = myArray[i].split("\\s*=\\s*");
                name = parts[0].substring(parts[0].lastIndexOf(' ') + 1) + "_" + currScopeLevel;
                type = "";

                if (line.contains("\bint\b")) {
                    if(parts[1].matches(NAME_REGEX)) {
                        if(!(table.containsKey(parts[1]) &&
                                table.get(parts[1]).getValue().getValue().equals(Boolean.TRUE) &&
                                table.get(parts[1]).getKey().equals("int") &&
                                table.get(parts[1]).getValue().getKey().equals(Boolean.FALSE))) {
                            throw new VariablesException("Wrong int variable handling!");
                        }
                    }
                    type = "int";
                }

                else if (line.contains("\bdouble\b")){
                    if(parts[1].matches(NAME_REGEX)) {
                        if(!(table.containsKey(parts[1]) &&
                                table.get(parts[1]).getValue().getValue().equals(Boolean.TRUE) &&
                                table.get(parts[1]).getKey().equals("double") &&
                                table.get(parts[1]).getValue().getKey().equals(Boolean.FALSE))) {
                            throw new VariablesException("Wrong double variable handling!");
                        }
                    }
                    type = "double";
                }

                else if (line.contains("\bchar\b")){
                    if(parts[1].matches(NAME_REGEX)) {
                        if(!(table.containsKey(parts[1]) &&
                                table.get(parts[1]).getValue().getValue().equals(Boolean.TRUE) &&
                                table.get(parts[1]).getKey().equals("char") &&
                                table.get(parts[1]).getValue().getKey().equals(Boolean.FALSE))) {
                            throw new VariablesException("Wrong char variable handling!");
                        }
                    }
                    type = "char";
                }

                else if (line.contains("\bboolean\b")){
                    if(parts[1].matches(NAME_REGEX)) {
                        if(!(table.containsKey(parts[1]) &&
                                table.get(parts[1]).getValue().getValue().equals(Boolean.TRUE) &&
                                table.get(parts[1]).getKey().equals("boolean")&&
                                table.get(parts[1]).getValue().getKey().equals(Boolean.FALSE))) {
                            throw new VariablesException("Wrong boolean variable handling!");
                        }
                    }
                    type = "boolean";
                }

                else if (line.contains("\bString\b")) {
                    if(parts[1].matches(NAME_REGEX)) {
                        if(!(table.containsKey(parts[1]) &&
                                table.get(parts[1]).getValue().getValue().equals(Boolean.TRUE) &&
                                table.get(parts[1]).getKey().equals("String") &&
                                table.get(parts[1]).getValue().getKey().equals(Boolean.FALSE))) {
                            throw new VariablesException("Wrong String variable handling!");
                        }
                    }
                    type = "String";
                }
                if (currScopeLevel >= 2) {
                    if(activeScopes.containsKey(currScopeLevel)) {
                        activeScopes.get(currScopeLevel).add(name);
                    }
                    else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(name); // Add the string to the list
                        activeScopes.put(currScopeLevel, list);
                    }
                }
                table.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }

            else {
                // Replace multiple spaces with a single space
                myArray[i] = myArray[i].replaceAll("\\s{2,}", " ").trim();

                // Split the string into words
                String[] words = myArray[i].split(" ");

                // Get the name (last word) and type (second-to-last word)
                name = words[words.length - 1] + "_" + currScopeLevel; // Last word
                type = words[words.length - 2]; // Second-to-last word

                if (currScopeLevel >= 2) {
                    if(activeScopes.containsKey(currScopeLevel)) {
                        activeScopes.get(currScopeLevel).add(name);
                    }
                    else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(name); // Add the string to the list
                        activeScopes.put(currScopeLevel, list);
                    }
                }

                table.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
        }
    }
}
