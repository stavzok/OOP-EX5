package ex5.main;

import ex5.code.HandleCodeLines;
import ex5.code.TypeOneException;

import java.io.*;
import java.util.ArrayList;



/**
 * The main class for the Sjavac program. This class handles input validation,
 * error checking, and the overall flow of the program.
 *
 * The program reads s-java file, validates it, and processes its content.
 * If a TypeOneException occurs, it prints '1'. If an IO-related error occurs, it prints '2'.
 * Otherwise, it prints '0' on successful execution.
 *
 * @author inbar.el and stavzok
 */
public class Sjavac {
    /* Error messages for various input and file-related issues. */
    private static final String ARGUMENTS_NUM_ERROR = "Error: Invalid number of arguments. Expected 1 argument.";
    private static final String S_JAVA_SUFFIX = ".sjava";
    private static final String FILE_FORMAT_ERROR = "Invalid file format: Expected .sjava file.";
    private static final String FILE_NOT_FOUND_ERROR = "Error: File not found.";
    private static final String CANT_READ_FILE_ERROR = "Error: Cannot read the file.";
    private static final String EMPTY_FILE_ERROR = "Error: The file is empty.";
    private static final String BINARY_DATA_ERROR = "Error: File contains non-text (binary) data.";
    private static final String IO_ERROR = "Error: An IO error occurred while reading the file.";
    private static final String BINARY_REGEX = "[\\p{Print}\\s]*";

    /**
     * The main method of the program.
     * It validates the input, reads the file, and processes the lines.
     * The program prints:
     * - '0' if the file is valid and processed successfully.
     * - '1' if a TypeOneException occurs.
     * - '2' if an IO error occurs.
     *
     * @param args Command-line arguments. Expected: a single file path.
     */
    public static void main(String[] args) {

        ArrayList<String> lines;
        try {
            lines = checkTypeTwoErrors(args);
        }
        catch(IOException e) {
            System.err.println(e.getMessage());
            System.out.println(2);
            return;
        }

        HandleCodeLines handler = new HandleCodeLines(lines);
        try {
            handler.handleLines();
        }
        catch (TypeOneException e){
            System.err.println(e.getMessage());
            System.out.println(1);
            return;
        }
        if (HandleCodeLines.currScopeLevel > 0){
            System.err.println("A method isn't closed!");
            System.out.println(1);

        }
        else {
            System.out.println(0);
        }
    }

    /*
     * Validates the input arguments and checks for type-two (IO) errors.
     * Ensures the file exists, is readable, has the correct format, and contains only text data.
     *
     * @param args The command-line arguments, expected to contain a single file path.
     * @return A list of strings representing the file lines.
     * @throws IOException If any type-two error occurs (e.g., invalid format, unreadable file).
     */
    private static ArrayList<String> checkTypeTwoErrors(String[] args) throws IOException {
        ArrayList<String> lines = new ArrayList<>();

        // Check the number of arguments
        if (args.length != 1) {
            throw new IOException(ARGUMENTS_NUM_ERROR);
        }

        String inputFile = args[0];
        File file = new File(inputFile);

        // Check if the file has the correct extension
        if (!inputFile.endsWith(S_JAVA_SUFFIX)) {
            throw new IOException(FILE_FORMAT_ERROR);
        }

        // Check if the file exists
        if (!file.exists()) {
            throw new IOException(FILE_NOT_FOUND_ERROR);
        }

        // Check if the file is readable
        if (!file.canRead()) {
            throw new IOException(CANT_READ_FILE_ERROR);
        }

        // Check if the file is empty
        if (file.length() == 0) {
            throw new IOException(EMPTY_FILE_ERROR);
        }

        // Read file line by line and check for non-text characters
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains only non-printable characters
                if (!line.matches(BINARY_REGEX)) {
                    throw new IOException(BINARY_DATA_ERROR);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IOException(IO_ERROR);
        }

        return lines;
    }

}

