package org.Y86;


import org.Y86.disassembler.Disassembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
/*
Was able to GCC the test.c file via Java, not needed right now but in the future YES!
ProcessBuilder builder = new ProcessBuilder("cmd","/c","gcc","test.c");
        builder.directory(new File("./src/main/java/org/C/testCode"));
        try {
            Process process = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
        } catch (java.io.IOException e) {
            // Handle exception
        }
 */
        Disassembler.Break("./src/main/java/org/Y86/disassembler/disassembledCode/","E3.txt");
    }
}