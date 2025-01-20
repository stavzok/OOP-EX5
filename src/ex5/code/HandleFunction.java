package ex5.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleFunction {
    private static final String PATTERN_TYPE = "(int|double|String|boolean|char)";

    private static final String FUNCTION_NAME_REGEX = "void\\s+[a-zA-Z](_?[a-zA-Z0-9])*_?\\s*\\((\\s*(final\\s+)?" +
            PATTERN_TYPE + "\\s+" + HandleCodeLines.NAME_REGEX +
            "\\s*(,\\s*(final\\s+)?" + PATTERN_TYPE + "\\s+" + HandleCodeLines.NAME_REGEX + ")*)?\\)\\s*";
    private static final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME_REGEX + "\\{");
    private static final Matcher FUNCTION_NAME_MATCHER = FUNCTION_NAME_PATTERN.matcher("");

//    private static final String FUNCTION_CALL_REGEX = "[a-zA-Z](_?[a-zA-Z0-9])*_?\\s*\\((\\s*" +
//            HandleVariables.LITERAL_PATTERN + "|" + HandleCodeLines.NAME_REGEX + "\\s*(,\\s*" +
//            HandleVariables.LITERAL_PATTERN + "|" + HandleCodeLines.NAME_REGEX + "\\s*)*)?\\)\\s*;";

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
    private static final Matcher FUNCTION_CALL_MATCHER = FUNCTION_CALL_PATTERN.matcher("");


    public static void handleFunction(String line) throws TypeOneException {
        HandleCodeLines.RETURN_MATCHER.reset(line);
        if (HandleCodeLines.RETURN_MATCHER.matches()) {
            if (HandleCodeLines.currScopeLevel == 0) {
                throw new ReturnException("Can't return from a global scope!");
            }
        }

        if (line.startsWith("void")) {
            if (HandleCodeLines.currScopeLevel > 0) {
                throw new NestedFunctionException("Can't create nested function!");
            }
            HandleCodeLines.currScopeLevel++;
            HandleCodeLines.localSymbolsTable = new HashMap<>();
            ArrayList<Map.Entry<Map.Entry<String, Boolean>, String>> currentFunction;
            currentFunction =
                    HandleCodeLines.functionSymbols.get(line.split(" ")[1].split("\\(")[0]);
            for (Map.Entry<Map.Entry<String, Boolean>, String> functionSymbol : currentFunction) {
                HandleCodeLines.localSymbolsTable.put(functionSymbol.getValue() + "_1", Map.entry(functionSymbol.getKey().getKey(),
                        Map.entry(functionSymbol.getKey().getValue(), true)));
            }
            return;
        }

        if (line.equals("}")) {
            if (HandleCodeLines.currScopeLevel == 1 && !HandleCodeLines.isReturn) {
                throw new ReturnException("Must return before ending the method!");
            }
            Iterator<String> iterator = HandleCodeLines.localSymbolsTable.keySet().iterator();
            while (iterator.hasNext()) {
                String var = iterator.next();
                String lastPart = var.substring(var.lastIndexOf('_') + 1);
                int value = Integer.parseInt(lastPart);
                if (value == HandleCodeLines.currScopeLevel) {
                    iterator.remove();  // Safe removal using iterator
                }
            }

            HandleCodeLines.currScopeLevel--;
            return;
        }

        if (line.startsWith("if")) {
            try {
                HandleIf.handleIfStatement(line);
                return;
            } catch (IfException e) {
                throw e;
            }
        }

        if (line.startsWith("while")) {
            try {
                HandleWhile.handleWhileStatement(line);
            } catch (WhileException e) {
                throw e;
            }

        } else {
            if (line.contains("(") && line.contains(")")) {
                try {
                    handleFunctionCall(line);
                } catch (FunctionCallException exception) {
                    throw exception;
                }
            } else {
                try {
                    HandleVariables.defineAssignVariable(line);
                } catch (VariablesException e) {
                    throw e;
                }

            }
        }
    }


    public static void handleFunctionDeclaration(String line) throws FunctionDeclarationException {
        FUNCTION_NAME_MATCHER.reset(line);
        if(!FUNCTION_NAME_MATCHER.matches()) {
            throw new FunctionDeclarationException("FUNCTION_DECLARATION_ERROR");
        }

        boolean isFinal = false;
        if(line.contains("final")) {
            isFinal = true;
        }

        // Extract the part of the line before the opening parenthesis
        String name = line.substring(0, line.indexOf('(')).trim();

        // Split by spaces and get the last element before '('
        name = name.split(" ")[1];
        if (HandleCodeLines.functionSymbols.containsKey(name)){
            throw new FunctionDeclarationException("cant declare two functions with the same name!");
        }

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
        HandleCodeLines.functionSymbols.put(name, innerArray);
    }

    public static void handleFunctionCall(String line) throws FunctionCallException {
        FUNCTION_CALL_MATCHER.reset(line);
        if(!FUNCTION_CALL_MATCHER.matches()) {

            throw new FunctionCallException("Wrong call format!");
        }

        String name = line.split("\\(")[0];
        String parameterPart = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        String[] parameters = parameterPart.split(",");
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
                            throw new FunctionCallException("Argument not initialized!");
                        } else {
                            types.add(HandleCodeLines.globalSymbolsTable.get(parameter).getKey());
                            isFound = true;
                        }
                        break;
                    }
                }

                else if(HandleCodeLines.localSymbolsTable.containsKey(parameter + "_" + i)) {
                    parameter = parameter + "_" + i;
                    if (!HandleCodeLines.localSymbolsTable.get(parameter).getValue().getValue()) {
                        throw new FunctionCallException("Argument not initialized!");
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
                    throw new FunctionCallException("Argument not valid!");
                }

            }


        }


        if(types.size() != HandleCodeLines.functionSymbols.get(name).size()) {
            throw new FunctionCallException("Wrong number of arguments!");
        }

        int counter = 0;
        for(String type : types) {
            if(!type.equals(HandleCodeLines.functionSymbols.get(name).get(counter).getKey().getKey())) {
                throw new FunctionCallException("Argument type mismatch!");
            }
            counter++;
        }
    }

}