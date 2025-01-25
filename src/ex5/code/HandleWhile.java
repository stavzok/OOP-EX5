package ex5.code;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the validation of while-loop statements in the code.
 * This class ensures that while conditions are properly structured
 * and contain valid components.
 *
 * @author inbar.el and stavzok
 */
public class HandleWhile {

    /*
     * Regular expression pattern for validating while conditions.
     */
    private final String WHILE_CONDITION_CONSTANT =
            "while\\s*\\(\\s*" +                 // while followed by opening parenthesis
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
                    "\\s*\\)\\s*\\{";                    // closing parenthesis and curly brace

    /*
     * Compiled pattern and matcher for matching while conditions.
     */
    private final Pattern WHILE_CONDITION_PATTERN = Pattern.compile(WHILE_CONDITION_CONSTANT);
    private final Matcher WHILE_CONDITION_MATCHER = WHILE_CONDITION_PATTERN.matcher("");

    /*
     * Set of valid types that can be used in while conditions.
     */
    private final HashSet<String> validTypes = new HashSet<>(Arrays.asList("int", "double", "boolean"));

    /*
     * Error message for invalid while condition components.
     */
    private final String WHILE_CONDITION_COMPONENTS_ERROR = "The while statement components are invalid!";

    /*
     * Error message for incorrect while statement structure.
     */
    private final String WHILE_STRUCTURE_ERROR = "The while structure is illegal!";

    /*
     * Underscore constant for variable name handling in different scopes.
     */
    private final String UNDER_SCORE = "_";

    /**
     * Validates and processes a given while statement.
     * Ensures that the while condition follows the correct structure
     * and that all referenced variables are properly initialized and of valid types.
     *
     * @param line The while statement to validate.
     * @throws WhileException If the while condition is improperly structured or contains invalid components.
     */
    public void handleWhileStatement(String line) throws WhileException {
        WHILE_CONDITION_MATCHER.reset(line);
        if(WHILE_CONDITION_MATCHER.matches()) {
            HandleCodeLines.currScopeLevel++;
            HandleCodeLines.NAME_MATCHER.reset(line);
            while(HandleCodeLines.NAME_MATCHER.find()) {
                String foundName = HandleCodeLines.NAME_MATCHER.group();
                // check if a variable inside the while statement is not initialized or is not of a correct type
                for(int i = HandleCodeLines.currScopeLevel; i > -1; i--) {
                    if(i == 0) {
                        if(HandleCodeLines.globalSymbolsTable.containsKey(foundName)) {
                            // global symbol table
                            if (!HandleCodeLines.globalSymbolsTable.get(foundName).getValue().getValue()
                                    || !validTypes.contains(HandleCodeLines.globalSymbolsTable.get(foundName).getKey())) {
                                throw new WhileException(WHILE_CONDITION_COMPONENTS_ERROR);
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
                            throw new WhileException(WHILE_CONDITION_COMPONENTS_ERROR);
                        }
                        break;
                    }
                }
            }
        }
        else {
            throw new WhileException(WHILE_STRUCTURE_ERROR);
        }
    }
}
