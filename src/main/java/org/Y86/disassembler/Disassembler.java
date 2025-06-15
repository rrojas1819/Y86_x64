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
     *
     * Reads the name of the file in the given directory
     *
     * @param fileName
     * @return
     */
    public static Disassembled Break(String Dir, String fileName, int memStartPos){
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(Dir + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(" ");

            }
        } catch (IOException _) {
        }
        if(fileContents.isEmpty()){
            return null;
        }
        //System.out.println(fileContents);
        return new Disassembled(fileContents.toString().split("\\s+"),memStartPos);
    }




}
