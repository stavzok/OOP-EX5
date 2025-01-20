package ex5.code;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleVariables {

    private static final String INT_PATTERN_ASSIGN =
            HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?\\d+|" + HandleCodeLines.NAME_REGEX +
                    "))?(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?\\d+|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private static  final String DOUBLE_PATTERN_ASSIGN =
            HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" +
                    HandleCodeLines.NAME_REGEX + "))?" +
                    "(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private static final String STRING_PATTERN_ASSIGN =  HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(\".*\"|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\".*\"|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private static final String BOOLEAN_PATTERN_ASSIGN = HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private static final String CHAR_PATTERN_ASSIGN = HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(\'.\'|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\'.\'|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";


    private static  final Pattern VARIABLE_PATTERN_ASSIGN =
            Pattern.compile(INT_PATTERN_ASSIGN + "|" + DOUBLE_PATTERN_ASSIGN +"|" + STRING_PATTERN_ASSIGN +
                    "|" + BOOLEAN_PATTERN_ASSIGN +"|" + CHAR_PATTERN_ASSIGN );

    private static final Matcher VARIABLE_MATCHER_ASSIGN = VARIABLE_PATTERN_ASSIGN.matcher("");

    private static final String INT_PATTERN =
            "(final\\s+)?int\\s+"+ HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?\\d+|" +
                    HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?\\d+|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private static final String DOUBLE_PATTERN =
            "(final\\s+)?double\\s+" + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|"
                    + HandleCodeLines.NAME_REGEX + "))?" +
                    "(\\s*,\\s*" + HandleCodeLines.NAME_REGEX +
                    "(\\s*=\\s*([+-]?(\\d+(\\.\\d*)?|\\.\\d+)|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final static String STRING_PATTERN =  "(final\\s+)?String\\s+"+ HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\".*\"|"
            + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(\".*\"|" +
            HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private final static String BOOLEAN_PATTERN = "(final\\s+)?boolean\\s+"+ HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*(true|false|" + HandleCodeLines.INT_REGEX +
            "|" + HandleCodeLines.DOUBLE_REGEX + "|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";

    private static final String CHAR_PATTERN = "(final\\s+)?char\\s+"+ HandleCodeLines.NAME_REGEX +
            "(\\s*=\\s*('.'|" + HandleCodeLines.NAME_REGEX + "))?(\\s*,\\s*"
            + HandleCodeLines.NAME_REGEX + "(\\s*=\\s*('.'|" + HandleCodeLines.NAME_REGEX + "))?)*\\s*;";


    public static final String LITERAL_PATTERN =
            HandleCodeLines.INT_REGEX + "|" + HandleCodeLines.DOUBLE_REGEX +"|" + "\".*\"" +
                    "|" + "(true|false)" +"|" + "'.'";


    public static final Pattern VARIABLE_PATTERN =
            Pattern.compile(INT_PATTERN + "|" + DOUBLE_PATTERN +"|" + STRING_PATTERN +
                    "|" + BOOLEAN_PATTERN +"|" + CHAR_PATTERN );
    public static final Matcher VARIABLE_MATCHER = VARIABLE_PATTERN.matcher("");

    private static final HashSet<String> typesAndFinal = new HashSet<>(Arrays.asList("int", "double", "String", "char",
            "boolean", "final"));



    private static void handleLiterals(String type, String rightName) throws VariablesException{
        String typeRight;
        typeRight = HandleCodeLines.findType(rightName);
        if (typeRight == null) {
            throw new VariablesException("assigned type not valid!");
        }

        if (!(typeRight.equals(type))) {
            if (type.equals("double")) {
                if (!typeRight.equals("int")) {
                    throw new VariablesException("assigned type not correct!");
                }
            } else if (type.equals("boolean")) {
                if (!typeRight.equals("double") && !typeRight.equals("int")) {
                    throw new VariablesException("assigned type not correct!");
                }
            }
        }
        
    }
    
    
    private static boolean handleDefineLocalVariable(String line, String type) throws VariablesException{
        boolean isInitialized = false;
        boolean isFinal = false;

        String[] myArray = line.split(",");

        for(int i = 0; i < myArray.length; i++){

            if (myArray[i].split("\"")[0].contains("=") || myArray[i].split("'")[0].contains("=")) {
                isInitialized = true;
            }

            if(myArray[0].contains("final")) {
                isFinal = true;
                if(!isInitialized){
                    throw new VariablesException("final variable must be initialized!");
                }
            }
            String name;
            if(isInitialized) {
                myArray[i] = myArray[i].replace(";", "").trim();
                // Split by '=' after removing unnecessary spaces around it
                String[] parts = myArray[i].split("\\s*=\\s*");
                name = parts[0].substring(parts[0].lastIndexOf(' ') + 1) + "_" + HandleCodeLines.currScopeLevel;
                boolean isFound = false;

                if (HandleCodeLines.localSymbolsTable.containsKey(name)){
                    throw new VariablesException("variable already exists!"); //check if variable exists in the scope
                }
                String origRightName = parts[1];
                if(parts[1].matches(HandleCodeLines.NAME_REGEX)) { //check right side - assigned variable (not literal)
                    for (int j = HandleCodeLines.currScopeLevel; j > 0; j--) {
                        if (HandleCodeLines.localSymbolsTable.containsKey(parts[1] + "_" + j)) {
                            parts[1] = parts[1] + "_" + j;
                            if (!HandleCodeLines.localSymbolsTable.get(parts[1]).getValue().getValue() ||
                                    !(HandleCodeLines.localSymbolsTable.get(parts[1]).getKey().equals(type))) {
                                throw new VariablesException("Argument not initialized!");
                            }
                            HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                            isFound = true;
                            break;

                        }

                    }
                    if (!isFound) {
                        if (HandleCodeLines.globalSymbolsTable.containsKey(origRightName)) {
                            if (!HandleCodeLines.globalSymbolsTable.get(origRightName).getValue().getValue() ||
                                    !(HandleCodeLines.globalSymbolsTable.get(origRightName).getKey().equals(type))) {
                                throw new VariablesException("Assigmnet invalid!");
                            }
                            HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                        }
                    }
                }

                try {
                    handleLiterals(type, parts[1]);
                }
                catch (VariablesException e){
                    throw e;
                }
                
                HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
            else{   //  varialbe is not asigned at defining
                myArray[i] = myArray[i].replace(";", "").trim();

                // Split by '=' after removing unnecessary spaces around it
                name = myArray[i].substring(myArray[i].lastIndexOf(' ') + 1) + "_" + HandleCodeLines.currScopeLevel;
                if (HandleCodeLines.localSymbolsTable.containsKey(name)){
                    throw new VariablesException("variable already exists!"); //check if variable exists in the scope
                }
                HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
        }
        return true;
    }


    private static boolean handleAssignLocalVariable(String line) throws VariablesException {
        boolean isInitialized = false;
        boolean isFinal = false;

        String[] myArray = line.split(",");
        for(int i = 0; i < myArray.length; i++) {
            if (!(myArray[i].split("\"")[0].contains("=") || myArray[i].split("'")[0].contains("="))) {
                throw new VariablesException("not a valid statement");
            }
            String name;
            String type;
            boolean isFound = false;
            myArray[i] = myArray[i].replace(";", "").trim();
            // Split by '=' after removing unnecessary spaces around it
            String[] parts = myArray[i].split("\\s*=\\s*");
            name = parts[0].substring(parts[0].lastIndexOf(' ') + 1) + "_" + HandleCodeLines.currScopeLevel;
            if (!HandleCodeLines.localSymbolsTable.containsKey(name)) {
                throw new VariablesException("variable doesnt exists!"); //check if variable exists in the scope
            }
            if (HandleCodeLines.localSymbolsTable.get(name).getValue().getKey().equals(Boolean.TRUE) &&
                    (HandleCodeLines.localSymbolsTable.get(name).getValue().getValue().equals(Boolean.TRUE))) {
                throw new VariablesException("cant assign final variable");
            }
            String origRightName = parts[1];

            type = HandleCodeLines.localSymbolsTable.get(name).getKey();
            if (parts[1].matches(HandleCodeLines.NAME_REGEX)) { //check right side - assigned variable (not literal)
                for (int j = HandleCodeLines.currScopeLevel; j > 0; j--) {
                    if (HandleCodeLines.localSymbolsTable.containsKey(parts[1] + "_" + j)) {
                        parts[1] = parts[1] + "_" + j;
                        if (!HandleCodeLines.localSymbolsTable.get(parts[1]).getValue().getValue() ||
                                !(HandleCodeLines.localSymbolsTable.get(parts[1]).getKey().equals(type))) {
                            throw new VariablesException("Argument not initialized!");
                        }
                        HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    if (HandleCodeLines.globalSymbolsTable.containsKey(origRightName)) {
                        if (!HandleCodeLines.globalSymbolsTable.get(origRightName).getValue().getValue() ||
                                !(HandleCodeLines.globalSymbolsTable.get(origRightName).getKey().equals(type))) {
                            throw new VariablesException("Assigmnet invalid!");
                        }
                        HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                    }
                }
            }
            try {
                handleLiterals(type, parts[1]);
            }
            catch (VariablesException e){
                throw e;
            }
            
            HandleCodeLines.localSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
        }
        return true;

    }

    private static boolean handleDefineGlobalVariable(String line,String type) throws VariablesException {
        boolean isInitialized = false;
        boolean isFinal = false;

        String[] myArray = line.split(",");

        for(int i = 0; i < myArray.length; i++){
            isInitialized = false;


            if (myArray[i].split("\"")[0].contains("=") || myArray[i].split("'")[0].contains("=")) {
                isInitialized = true;
            }

            if(myArray[0].contains("final")) {
                isFinal = true;
                if(!isInitialized){
                    throw new VariablesException("final variable must be initialized!");
                }
            }
            String name;
            if(isInitialized) {
                myArray[i] = myArray[i].replace(";", "").trim();
                // Split by '=' after removing unnecessary spaces around it
                String[] parts = myArray[i].split("\\s*=\\s*");
                name = parts[0].substring(parts[0].lastIndexOf(' ') + 1);

                if (HandleCodeLines.globalSymbolsTable.containsKey(name)){
                    throw new VariablesException("variable already exists!"); //check if variable exists in the scope
                }
                if(parts[1].matches(HandleCodeLines.NAME_REGEX)) { //check right side - assigned variable (not literal)
                    if (HandleCodeLines.globalSymbolsTable.containsKey(parts[1])) {
                        if (HandleCodeLines.globalSymbolsTable.get(parts[1]).getValue().getValue().equals(Boolean.FALSE)
                                || !(HandleCodeLines.globalSymbolsTable.get(parts[1]).getKey().equals(type))) {
                            throw new VariablesException("invalid assignment!"); //check if variable exists in the scope
                        }
                    }
                    else{
                        throw new VariablesException("the right assigned variable doesnt exist!"); //check if variable exists in the scope


                    }

                    HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                }
                else {
                    try {
                        handleLiterals(type, parts[1]);
                    }
                    catch (VariablesException e){
                        throw e;
                    }
                    HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
                }
            }
            else{   //  varialbe is not assigned at defining
                myArray[i] = myArray[i].replace(";", "").trim();

                // Split by '=' after removing unnecessary spaces around it
                name = myArray[i].substring(myArray[i].lastIndexOf(' ') + 1);
                if (HandleCodeLines.globalSymbolsTable.containsKey(name)){
                    throw new VariablesException("variable already exists!"); //check if variable exists in the scope
                }
                HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
        }
        return true;
    }

    private static boolean handleAssignGlobalVariable(String line) throws VariablesException {

        boolean isInitialized = false;
        boolean isFinal = false;
        String[] myArray = line.split(",");
        for(int i = 0; i < myArray.length; i++) {
            if (!(myArray[i].split("\"")[0].contains("=") || myArray[i].split("'")[0].contains("="))) {
                throw new VariablesException("not a valid statement");
            }
            String name;
            String type;
            myArray[i] = myArray[i].replace(";", "").trim();
            // Split by '=' after removing unnecessary spaces around it
            String[] parts = myArray[i].split("\\s*=\\s*");
            name = parts[0].substring(parts[0].lastIndexOf(' ') + 1);
            if (!HandleCodeLines.globalSymbolsTable.containsKey(name)) {
                throw new VariablesException("variable doesnt exists!"); //check if variable exists in the scope
            }
            if (HandleCodeLines.globalSymbolsTable.get(name).getValue().getKey().equals(Boolean.TRUE) &&
                    (HandleCodeLines.globalSymbolsTable.get(name).getValue().getValue().equals(Boolean.TRUE))) {
                throw new VariablesException("cant assign final variable");
            }
            type = HandleCodeLines.globalSymbolsTable.get(name).getKey();
            if (parts[1].matches(HandleCodeLines.NAME_REGEX)) { //check right side - assigned variable (not literal)
                if (HandleCodeLines.globalSymbolsTable.containsKey(parts[1])) {
                    if (!HandleCodeLines.globalSymbolsTable.get(parts[1]).getValue().getValue() ||
                            !(HandleCodeLines.globalSymbolsTable.get(parts[1]).getKey().equals(type))) {
                        throw new VariablesException("Assigmnet invalid!");
                    }
                }
                else{
                    throw new VariablesException("the right assigned variable doesnt exist!");
                }
                HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
            else {
                try {
                    handleLiterals(type, parts[1]);
                }
                catch (VariablesException e){
                    throw e;
                }
                HandleCodeLines.globalSymbolsTable.put(name, Map.entry(type, Map.entry(isFinal, isInitialized)));
            }
        }
        return true;
    }

    public static void defineAssignVariable(String line) throws VariablesException {
        // check if global / local
        HashMap<String, Map.Entry<String, Map.Entry<Boolean, Boolean>>> table;
        String type;
        boolean isGlobal = false;
        if(HandleCodeLines.currScopeLevel == 0) {
            isGlobal = true;
        }

        // check define / assign
        if(typesAndFinal.contains(line.split(" ")[0])) {
            type = line.split(" ")[0];
            VARIABLE_MATCHER.reset(line);
            if (!VARIABLE_MATCHER.matches()) {
                throw new VariablesException("Not a valid define format!");
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
                throw new VariablesException("Not a valid assign format!");
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
