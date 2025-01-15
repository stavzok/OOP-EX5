package code;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HandleCodeLines {
    private ArrayList<String> codeLines;
    private static final Pattern COMMENT_START = Pattern.compile("^.*\\\\");
    private static final Matcher COMMENT_START_MATCHER = COMMENT_START.matcher("");
    private static final String NAME_REGEX = "_?[a-zA-Z](_?[a-zA-Z0-9])*_?";
    private static final String FUNCTION_NAME_REGEX = "[a-zA-Z](_?[a-zA-Z0-9])*_?";
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

    private final String CHAR_PATTERN = "(final\\s+)?char\\s+"+ NAME_REGEX + "(\\s*=\\s*(\'.\'|" + NAME_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(\'.\'|" + NAME_REGEX + "))?)*\\s*;";
    private  final Pattern VARIABLE_PATTERN =
            Pattern.compile(INT_PATTERN + "|" + DOUBLE_PATTERN +"|" + STRING_PATTERN +
                    "|" + BOOLEAN_PATTERN +"|" + CHAR_PATTERN );
    private final Matcher VARIABLE_MATCHER = VARIABLE_PATTERN.matcher("");
    private final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME_REGEX);
    private final Matcher FUNCTION_NAME_MATCHER = FUNCTION_NAME_PATTERN.matcher("");
    private int scopeLevel = 0;
    private enum ScopeLevel {
        OUTER_SCOPE,      // 0 - Outer Scope
        FUNCTION_SCOPE   // 1 - Function Scope
    }
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
    private HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> globalSymbolStable = new HashMap<>();
    private HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> localSymbolStable = new HashMap<>();
    private boolean startedFunctions = false;
    private String[] legalTypes = new String[] {"int", "double", "String", "boolean", "char"};
    private HashMap<String, Map<String, String>> functionSymbols = new HashMap<>();
    private final HashSet<String> typesAndFinal = new HashSet<>(Arrays.asList("int", "double", "String", "char",
            "boolean", "final"));
    private final HashMap<Integer, ArrayList<String>> activeScopes = new HashMap<>();

    private final String IF_CONDITION_CONSTANT = "if\\s*\\((true|false|" + INT_REGEX + "|" + DOUBLE_REGEX +
            ")\\s*(\\|\\||&&)\\s*\\((true|false|" + INT_REGEX + "|" + DOUBLE_REGEX + ")\\)\\s*\\)\\s*\\{";

    private final Pattern IF_CONDITION_PATTERN = Pattern.compile(IF_CONDITION_CONSTANT);
    private final Matcher IF_CONDITION_MATCHER = IF_CONDITION_PATTERN.matcher("");


    public HandleCodeLines(ArrayList<String> codeLines) {
        this.codeLines = codeLines;
    }

    public void handleLines() throws IOException {
        for(String codeLine: codeLines) {
            codeLine.trim();
            codeLine = codeLine.replaceAll("\\s+", " ");
            if(codeLine.startsWith("void")) {
                try {
                    handleFunctionDeclaration(codeLine);
                }
                catch(Exception e) {
                    throw new IOException();
                }
            }
        }

        for (String line : codeLines) {
            try {
                handleLine(line); // Call the method normally
            } catch (IOException e) {
                throw e;
            }
        }
    }

    private void handleLine(String line) throws IOException {
        if (COMMENT_START_MATCHER.reset(line).find()) {
            throw new IOException();
        }
        line = line.trim();
        if (line.startsWith("\\\\")) {
            return;
        }

        if(line.isEmpty()){
            return;
        }

        if(startedFunctions) {
            handleFunction(line);
        }

        if (!startedFunctions){
            if (line.startsWith("void")){
                startedFunctions = true;
                handleFunction(line);
            }
            else {
                defineAssignVariable(line, true);
            }
        }
    }

    private void handleFunction(String line) throws IOException {

        if (line.startsWith("void")) {
            if(currScopeLevel > 0) {
                throw new IOException();
            }
            currScopeLevel++;

            return;
        }

        if(line.equals("}")) {
            currScopeLevel--;

        }

        if(line.contains("if")) {
            handleIfStatement(line);

        }

        if(line.contains("while")) {

        }

        else {
            defineAssignVariable(line, false);
        }
    }

    private void handleIfStatement(String line) {
        IF_CONDITION_MATCHER.reset(line);
        if(IF_CONDITION_MATCHER.matches()) {

        }
    }

    private void handleFunctionDeclaration(String line) throws IOException{
        // Extract the part of the line before the opening parenthesis
        String name = line.substring(0, line.indexOf('(')).trim();

        // Split by spaces and get the last element before '('
        name = name.split(" ")[1];
        FUNCTION_NAME_MATCHER.reset(name);
        if(!FUNCTION_NAME_MATCHER.matches()) {
            throw new IOException();
        }

        if (line.charAt(line.length() - 1) != '{') {
            throw new IOException();
        }

        // Extract parameters (assuming parameters are within parentheses)
        String parameterPart = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        String[] parameters = parameterPart.split(",");
        Map<String, String> innerMap = new HashMap<>();
        for (String parameter : parameters) {
            parameter = parameter.trim(); // Clean up spaces
            if (!parameter.isEmpty()) {   // Ensure parameter is not empty
                String[] parameterArray = parameter.split("\\s+");
                innerMap.put(parameterArray[0], parameterArray[1]);
            }
        }
        functionSymbols.put(name, innerMap);
    }


    private void defineAssignVariable(String line, boolean isGlobal) throws IOException {
        // check if global / local
        HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> table;
        if(isGlobal) {
            table = globalSymbolStable;
        }
        else {
            table = localSymbolStable;
        }

        // check define / assign
        if(typesAndFinal.contains(line.split(" ")[0])) {
            VARIABLE_MATCHER.reset(line);
            if (!VARIABLE_MATCHER.matches()) {
                throw new IOException();
            }
        }

        else {
            VARIABLE_MATCHER_ASSIGN.reset(line);
            if (!VARIABLE_MATCHER_ASSIGN.matches()) {
                throw new IOException();
            }
        }

        boolean isInitialized = false;
        boolean isFinal = false;

        String[] myArray = line.split(",");

        for(int i = 0; i < myArray.length; i++){

            if (myArray[i].contains("=")) {
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
                name = parts[0].substring(parts[0].lastIndexOf(' ') + 1);
                type = "";

                if (line.contains("\bint\b")) {
                    if(parts[1].matches(NAME_REGEX)) {
                        if(!(table.containsKey(parts[1]) &&
                                table.get(parts[1]).getValue().getValue().equals(Boolean.TRUE) &&
                                table.get(parts[1]).getKey().equals("int") &&
                                table.get(parts[1]).getValue().getKey().equals(Boolean.FALSE))) {
                            throw new IOException();
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
                            throw new IOException();
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
                            throw new IOException();
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
                            throw new IOException();
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
                            throw new IOException();
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
                name = words[words.length - 1]; // Last word
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
