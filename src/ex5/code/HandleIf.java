package ex5.code;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the validation of if-statement conditions in the code.
 * This class ensures that if conditions are properly structured
 * and contain valid components.
 *
 * @author inbar.el and stavzok
 */
public class HandleIf {

    /*
     * Regular expression pattern for validating if conditions.
     */
    private final String IF_CONDITION_CONSTANT =
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
                    HandleCodeLines.NAME_REGEX + "))*" +
                    "\\s*\\)\\s*\\{";// closing parenthesis and curly brace

    /*
     * Compiled pattern and matcher for matching if conditions.
     */
    private final Pattern IF_CONDITION_PATTERN = Pattern.compile(IF_CONDITION_CONSTANT);
    private final Matcher IF_CONDITION_MATCHER = IF_CONDITION_PATTERN.matcher("");

    /*
     * Set of valid types that can be used in if conditions.
     */
    private final HashSet<String> validTypes = new HashSet<>(Arrays.asList("int", "double", "boolean"));

    /*
     * Error message for invalid if condition components.
     */
    private final String IF_CONDITION_COMPONENTS_ERROR = "The if condition components are invalid!";

    /*
     * Underscore constant for variable name handling in different scopes.
     */
    private final String UNDER_SCORE = "_";

    /*
     * Error message for incorrect if statement structure.
     */
    private final String IF_STRUCTURE_ERROR = "The if statement structure is invalid!";

    /**
     * Validates and processes a given if statement.
     * Ensures that the if condition follows the correct structure
     * and that all referenced variables are properly initialized and of valid types.
     *
     * @param line The if statement to validate.
     * @throws IfException If the if condition is improperly structured or contains invalid components.
     */
    public void handleIfStatement(String line) throws IfException {
        IF_CONDITION_MATCHER.reset(line);
        if (IF_CONDITION_MATCHER.matches()) {
            HandleCodeLines.currScopeLevel++;
            HandleCodeLines.NAME_MATCHER.reset(line);
            while (HandleCodeLines.NAME_MATCHER.find()) {
                String foundName = HandleCodeLines.NAME_MATCHER.group();
                // check if a variable inside the if statement is not initialized or is not of a correct type
                for(int i = HandleCodeLines.currScopeLevel; i > -1; i--) {
                    if(i == 0) {
                        // global symbol table
                        if(HandleCodeLines.globalSymbolsTable.containsKey(foundName)) {
                            if (!HandleCodeLines.globalSymbolsTable.get(foundName).getValue().getValue()
                            || !validTypes.contains(HandleCodeLines.globalSymbolsTable.get(foundName).getKey())) {
                                throw new IfException(IF_CONDITION_COMPONENTS_ERROR);
                            }
                            break;
                        }
                    }
                    // local symbol table
                    else if(HandleCodeLines.localSymbolsTable.containsKey(foundName + UNDER_SCORE + i)) {
                        foundName = foundName + UNDER_SCORE + i;
                        if (!HandleCodeLines.localSymbolsTable.get(foundName).getValue().getValue()
                                || !validTypes.contains(HandleCodeLines.localSymbolsTable.get(foundName).getKey()))
                        {
                            throw new IfException(IF_CONDITION_COMPONENTS_ERROR);
                        }
                        break;
                    }
                }
            }
        } else {
            throw new IfException(IF_STRUCTURE_ERROR);
        }
    }
}


