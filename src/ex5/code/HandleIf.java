package ex5.code;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleIf {
    private static final String IF_CONDITION_CONSTANT =
            "if\\s*\\(\\s*" +                    // if followed by opening parenthesis
                    "(true|false|" +                     // condition
                    HandleCodeLines.INT_REGEX + "|" +
                    HandleCodeLines.DOUBLE_REGEX + "|" +
                    HandleCodeLines.NAME_REGEX + ")" +
                    // Make the compound part optional with ?
                    "(\\s*(\\|\\||&&)\\s*" +            // optional logical operator
                    "(true|false|" +                     // optional second condition
                    HandleCodeLines.INT_REGEX + "|" +
                    HandleCodeLines.DOUBLE_REGEX + "|" +
                    HandleCodeLines.NAME_REGEX + "))?" +
                    "\\s*\\)\\s*\\{";                    // closing parenthesis and curly brace
    private static final Pattern IF_CONDITION_PATTERN = Pattern.compile(IF_CONDITION_CONSTANT);
    private static final Matcher IF_CONDITION_MATCHER = IF_CONDITION_PATTERN.matcher("");

    public static void handleIfStatement(String line) throws IfException {
        IF_CONDITION_MATCHER.reset(line);
        if (IF_CONDITION_MATCHER.matches()) {
            HandleCodeLines.currScopeLevel++;
            HandleCodeLines.NAME_MATCHER.reset(line);
            HashSet<String> validTypes = new HashSet<>(Arrays.asList("int", "double", "boolean"));
            while (HandleCodeLines.NAME_MATCHER.find()) {
                String foundName = HandleCodeLines.NAME_MATCHER.group();
                for(int i = HandleCodeLines.currScopeLevel; i > -1; i--) {
                    if(i == 0) {
                        if(HandleCodeLines.globalSymbolsTable.containsKey(foundName)) {
                            if (!HandleCodeLines.globalSymbolsTable.get(foundName).getValue().getValue()
                            || !validTypes.contains(HandleCodeLines.localSymbolsTable.get(foundName).getKey())) {
                                throw new IfException("not valid argument for if!");
                            }
                            break;
                        }
                    }
                    else if(HandleCodeLines.localSymbolsTable.containsKey(foundName + "_" + i)) {
                        foundName = foundName + "_" + i;
                        if (!HandleCodeLines.localSymbolsTable.get(foundName).getValue().getValue()
                                || !validTypes.contains(HandleCodeLines.localSymbolsTable.get(foundName).getKey()))
                        {
                            throw new IfException("not valid argument for if!");
                        }
                        break;
                    }
                }
            }
        } else {
            throw new IfException("If statement is illegal!");
        }
    }
}


