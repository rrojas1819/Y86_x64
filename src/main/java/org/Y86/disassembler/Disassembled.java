package org.Y86.disassembler;

import java.util.*;

/***
    Class/Object
    Capable of printing a text file of the code!
    Contains all the mappings!
 */
public class Disassembled {
    private HashMap<String, String> memToInstruction;
    private HashMap<String, String> registers = registerCreation();
    private HashMap<String, String> labels = new HashMap<String,String>();


    /***
     * Could be in constructor, however, separated it anyway.
     * @return
     */
    private HashMap<String, String> registerCreation() {
        HashMap<String, String> registers =  new HashMap<String, String>();
        registers.put("0","%rax");
        registers.put("1","%rcx");
        registers.put("2","%rdx");
        registers.put("3","%rbx");
        registers.put("4","%rsp");
        registers.put("5","%rbp");
        registers.put("6","%rsi");
        registers.put("7","%rdi");
        registers.put("8","%r8");
        registers.put("9","%r9");
        registers.put("10","%r10");
        registers.put("11","%r11");
        registers.put("12","%r12");
        registers.put("13","%r13");
        registers.put("14","%r14");
        registers.put("15","F");

       return registers;
    }

    public Disassembled(String allContents,String addressContents,HashMap<String,String> memToInstruction){
        this.memToInstruction = memToInstruction;
        String[] allInstructions = allContents.split("\n"); //We are assuming it is separated by a new line.
        String[] addresses = addressContents.split("\n");


        //System.out.println(memToInstruction);
        int iterator =0;
        for(String entireInstruction: allInstructions){
            StringBuilder currentInstruct = new StringBuilder();
            int i = 0;
            for(char letter: entireInstruction.toCharArray()){
                switch(i){
                    case 0:
                    case 1:
                        currentInstruct.append(letter);
                }
                i++;
                if(i > 2){
                    break;
                }
            }
            //CurrentInstruction houses the first encode of the instruction to work with
            if(i > 1){
                entireInstruction = entireInstruction.substring(2);
            }

            System.out.println(decodeRest(currentInstruct.toString(),addresses[iterator],entireInstruction));
            iterator++;
        }
    }

    /**
     * Return entire instruction in Assembly Y86-64
     * @param currentInstruct
     * @param restOfDecode
     * @return
     */
    private String decodeRest(String currentInstruct,String currentAddress, String restOfDecode){
        /*
        I'm looking for the same type of instruction and the same type of address.
         */
        currentAddress = currentAddress.strip();
        if(!labels.isEmpty()){
            if(memToInstruction.get(currentAddress) != null && memToInstruction.get(currentAddress).contentEquals(currentInstruct) && labels.containsKey(currentAddress)){
                System.out.println(labels.get(currentAddress) + ":");
                labels.remove(currentAddress);
            }
        }
        switch(currentInstruct){
            //Need all the instructions <---------------
            case "00"://Halt
                return "halt";
            case "10"://Nop
                return "nop";
            case "30"://irmovq
                return "irmovq " + registerDecode(restOfDecode) + "$"+ restOfDecode.substring(2);
            case "40"://rmmovq
                return "rmmovq " + registerDecode(restOfDecode) + addressDecode(restOfDecode);
            case "50"://mrmovq
                return "mrmovq "+ registerDecode(restOfDecode) + addressDecode(restOfDecode);
            case "80"://Call
                Map.Entry<String,String> entry = memToInstruction.entrySet().iterator().next();
                String label = createLabel(addressDecode(restOfDecode),entry.getKey().length());
                //System.out.println(labels);
                return "call " + label;
            case "90"://Return
                return "ret ";
            case "A0"://Push
                return "push " + registerDecode(restOfDecode);
            case "B0"://pop
                return "pop " + registerDecode(restOfDecode);
            default:// Pure laziness in wanting to set up
                switch (currentInstruct.substring(0,1)){
                    case "2"://Conditional Moves FALL THROUGH
                    case "6"://Operations FALL THROUGH
                    case "7"://Jumps
                        return decodeFamilyHelper(currentInstruct,restOfDecode);
                }

        }
        return "<--Invalid Read--> : "+ currentInstruct + restOfDecode;
    }
    private String createLabel(String addressDecode,int length){
        String toStore = addressDecode.substring(0,length);
        Random random = new Random();
        int toLabel = random.nextInt(0,Integer.MAX_VALUE);
        labels.put(toStore,"L" + String.valueOf(toLabel));
        return "L" + toLabel;

    }
    /***
     * Returns the address in hex
     * @param restOfDecode
     * @return
     */
    private String addressDecode(String restOfDecode) {
        boolean numHit = true;
        StringBuilder toReturn = new StringBuilder();

        for(char num: restOfDecode.toCharArray()){
            //Keep checking until zero is no longer in front
            if(num =='0' && numHit){
            }else{
                toReturn.append(num);
                numHit = false;
            }

        }
        return "0x" + toReturn;
    }

    /**
     * Returns the corresponing register <-- switch again -->
     * @param restOfDecode
     * @return
     */
    private String registerDecode(String restOfDecode) {
        String firstRegister = restOfDecode.substring(0,1);
        String secondRegister = restOfDecode.substring(1,2);
        firstRegister = registers.get(firstRegister);
        secondRegister = registers.get(secondRegister);

        return firstRegister + "," + secondRegister;
    }

    /**
     *
     * Assists in decoding the three families of Jump, Conditional Jumps and Operations
     *
     * Can make more efficient in terms of grouping but for now sure.
     *
     * @return
     */
    private String decodeFamilyHelper(String currentInstruct, String restOfDecode){
        switch(currentInstruct){
            case "20"://
                return "rrmovq " + registerDecode(restOfDecode);
            case "21"://
                return "cmovle " + registerDecode(restOfDecode);
            case "22"://
                return "cmovl " + registerDecode(restOfDecode);
            case "23"://
                return "cmove " + registerDecode(restOfDecode);
            case "24"://
                return "cmovne " + registerDecode(restOfDecode);
            case "25"://
                return "cmovge " + registerDecode(restOfDecode);
            case "26"://
                return "cmovg " + registerDecode(restOfDecode);
            case "60"://
                return "addq " + registerDecode(restOfDecode);
            case "61"://
                return "subq " + registerDecode(restOfDecode);
            case "62"://
                return "andq " + registerDecode(restOfDecode);
            case "63"://
                return "xorq " + registerDecode(restOfDecode);
            case "70"://Could Group these up afterwords for better reading.
                return "jmp " + addressDecode(restOfDecode);
            case "71"://
                return "jle " + addressDecode(restOfDecode);
            case "72"://
                return "jl " + addressDecode(restOfDecode);
            case "73"://
                return "je " + addressDecode(restOfDecode);
            case "74"://
                return "jne " + addressDecode(restOfDecode);
            case "75"://
                return "jge " + addressDecode(restOfDecode);
            case "76"://
                return "jg " + addressDecode(restOfDecode);
        }
        return "<--Invalid Family Member Found--> " + currentInstruct + restOfDecode;
    }

}
