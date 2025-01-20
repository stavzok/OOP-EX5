package ex5.main;

import ex5.code.HandleCodeLines;
import ex5.code.TypeOneException;

import java.io.*;
import java.util.ArrayList;


public class Sjavac {

    public static void main(String[] args) {

        ArrayList<String> lines = new ArrayList<>();
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

        System.out.println(0);
    }

    private static ArrayList<String> checkTypeTwoErrors(String[] args) throws IOException {
        ArrayList<String> lines = new ArrayList<>();

        // Check the number of arguments
        if (args.length != 1) {
            throw new IOException("Error: Invalid number of arguments. Expected 1 argument.");
        }

        String inputFile = args[0];
        File file = new File(inputFile);

        // Check if the file has the correct extension
        if (!inputFile.endsWith(".sjava")) {
            throw new IOException("Invalid file format: Expected .sjava file.");
        }

        // Check if the file exists
        if (!file.exists()) {
            throw new IOException("Error: File not found.");
        }

        // Check if the file is readable
        if (!file.canRead()) {
            throw new IOException("Error: Cannot read the file.");
        }

        // Check if the file is empty
        if (file.length() == 0) {
            throw new IOException("Error: The file is empty.");
        }

        // Read file line by line and check for non-text characters
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains only non-printable characters
                if (!line.matches("[\\p{Print}\\s]*")) {
                    throw new IOException("Error: File contains non-text (binary) data.");
                }
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IOException("Error: An IO error occurred while reading the file.");
        }

        return lines;
    }

}

