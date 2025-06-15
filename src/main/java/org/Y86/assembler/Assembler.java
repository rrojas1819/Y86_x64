package org.Y86.assembler;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Assembler {
/***
 * Assembler object :D
 * */
public static Assembled Read(String Dir, String fileName){
    StringBuilder fileContents = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(Dir + fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if(!line.contains(".pos 0")) { //This gets rid of both pos, fix this later.
                fileContents.append(line).append(" ");
            }
            else{
                fileContents.append(".pos0\n");
            }
        }
    } catch (IOException _) {
    }
    if(fileContents.isEmpty()){
        return null;
    }
    //System.out.println(fileContents);
    if(fileContents.toString().contains(".pos 0")){
    }
    return new Assembled(fileContents.toString().split("\\s+"));
}

}
