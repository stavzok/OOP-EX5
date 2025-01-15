package code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HandleCodeLines {
    private ArrayList<String> codeLines;
    private static final Pattern COMMENT_START = Pattern.compile("^.*\\\\");
    private static final Matcher COMMENT_START_MATCHER = COMMENT_START.matcher("");
    private static final String NAME_REGEX = "_?[a-zA-Z](_?[a-zA-Z0-9])*_?";
    private final String INT_REGEX = "[+-]?\\d+";
    private final String DOUBLE_REGEX = "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)";

    private final String INT_PATTERN =
            "(final\\s+)?int\\s+"+ NAME_REGEX + "(\\s*=\\s*[+-]?\\d+)?(\\s*,\\s*" + NAME_REGEX + "(\\s*=\\s*[+-]?\\d+)?)*\\s*;";
    private  final String DOUBLE_PATTERN =
            "(final\\s+)?double\\s+" + NAME_REGEX + "(\\s*=\\s*[+-]?(\\d+(\\.\\d*)?|\\.\\d+))?" +
                    "(\\s*,\\s*" + NAME_REGEX + "(\\s*=\\s*[+-]?(\\d+(\\.\\d*)?|\\.\\d+))?)*\\s*;";
    private final String STRING_PATTERN =  "(final\\s+)?String\\s+"+ NAME_REGEX + "(\\s*=\\s*\".*\")?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*\".*\")?)*\\s*;";
    private final String BOOLEAN_PATTERN = "(final\\s+)?boolean\\s+"+ NAME_REGEX + "(\\s*=\\s*(true|false|" + INT_REGEX +
            "|" + DOUBLE_REGEX + "))?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*(true|false|" + INT_REGEX +
            "|" + DOUBLE_REGEX + "))?)*\\s*;";

    private final String CHAR_PATTERN = "(final\\s+)?char\\s+"+ NAME_REGEX + "(\\s*=\\s*\'.\')?(\\s*,\\s*"
            + NAME_REGEX + "(\\s*=\\s*\'.\')?)*\\s*;";
    private  final Pattern VARIABLE_PATTERN =
            Pattern.compile(INT_PATTERN + "|" + DOUBLE_PATTERN +"|" + STRING_PATTERN +
                    "|" + BOOLEAN_PATTERN +"|" + CHAR_PATTERN );
    private final Matcher VARIABLE_MATCHER = VARIABLE_PATTERN.matcher("");




    private int scopeLevel = 0;
    private enum ScopeLevel {
        OUTER_SCOPE,      // 0 - Outer Scope
        FUNCTION_SCOPE   // 1 - Function Scope
    }
    private int currScopeLevel = 0;
    private boolean startedFunctions = false;
    private HashMap<String, Map.Entry<String, Boolean>> globalSymbolStable = new HashMap<>();
    private String[] legalTypes = new String[] {"int", "double", "String", "boolean", "char"};

    public HandleCodeLines(ArrayList<String> codeLines) {
        this.codeLines = codeLines;
    }

    public void handleLines() throws IOException {
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

        if (!startedFunctions){
            if (line.startsWith("void")){
                startedFunctions = true;
//                handleFunctionDeclaration(line);
            }
            else{
                handleGlobalLine(line);
            }


        }







    }


    private void handleGlobalLine(String line) throws IOException {
        VARIABLE_MATCHER.reset(line);
        if (!VARIABLE_MATCHER.matches()) {
            throw new IOException();
        }

        if (line.contains("\bint\b")){

        }
        boolean isInitialized = false;

        if (line.contains("=")){
            isInitialized = true;
        }

        else if (line.contains("\bdouble\b")){

        }
        else if (line.contains("\bchar\b")){

        }
        else if (line.contains("\bboolean\b")){

        }
        else if (line.contains("\bString\b")){

        }



    }

}
