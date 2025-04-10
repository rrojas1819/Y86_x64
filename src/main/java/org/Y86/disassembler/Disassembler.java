package org.Y86.disassembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
Return Disassembled object that contains all the instructions :D
 */
public class Disassembler{

    /***
     * Params Example()
     * Reads the name of the file in the given directory
     *
     * @param fileName
     * @return
     */
    public static Disassembled Break(String Dir,String fileName){
        StringBuilder fileContents = new StringBuilder();
        StringBuilder addressContents = new StringBuilder();
        HashMap<String, String> memToInstruction = new HashMap<String,String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Dir + fileName))) {
            String line;
            String address;
            while ((line = reader.readLine()) != null) {

                if(line.contains("...")){
                    continue;
                }
                memToInstruction.put(line.split(":")[0],line.split(":")[1].substring(0,2));
                address = line.split(":")[0];
                line = line.split(":")[1];
                fileContents.append(line).append(System.lineSeparator());
                addressContents.append(address).append(System.lineSeparator());

            }
        } catch (IOException _) {
        }
         return new Disassembled(fileContents.toString(),addressContents.toString(),memToInstruction);
    }



}
