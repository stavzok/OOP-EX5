package main;

import code.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;


public class Sjavac {

    public void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error: Invalid number of arguments.");
            System.out.println(2);
            return;
        }
        String inputFile = args[0];
        ArrayList<String> lines = new ArrayList<>(); // To store lines from the file

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null){
                lines.add(line);
            }

        }
        catch (IOException e) {
            System.err.println("Error: An IO error occurred.");
            System.out.println(2);
            return;
        }

        HandleCodeLines handler = new HandleCodeLines(lines);
        try{
            handler.handleLines();

        }
        catch (IOException e){
            System.err.println("Error: code is illegal.");
            System.out.println(1);
        }

    }



}

