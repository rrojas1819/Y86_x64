package org.Y86.assembler;

import java.util.*;

public class Assembled {
    private Queue<String> queue = new ArrayDeque<String>();
    private Queue<String> values = new ArrayDeque<String>();
    private int currentMemoryPos = 0;
    private HashMap<Integer,String> trackMemory = new LinkedHashMap<Integer, String>();
    private HashMap<Integer,String> labels = new LinkedHashMap<Integer,String>();
    private HashMap<Integer,String> ArrayValues = new LinkedHashMap<Integer, String>();
    private HashMap<Integer,String> registerValues = new LinkedHashMap<Integer, String>();
    private HashMap<Integer,String> immValues = new LinkedHashMap<Integer, String>();
    private HashMap<Integer,String> jumpValues = new LinkedHashMap<Integer, String>();



    private static final HashMap<String,String> instructions =  new HashMap<String, String>();
        static {
            instructions.put("halt", "00");
            instructions.put("nop","10");
            instructions.put("rrmovq","20");
            instructions.put("cmovle","21");
            instructions.put("cmovl","22");
            instructions.put("cmove","23");
            instructions.put("cmovne","24");
            instructions.put("cmovge","25");
            instructions.put("cmovg","26");
            instructions.put("irmovq","30");
            instructions.put("rmmovq","40");
            instructions.put("mrmovq","50");
            instructions.put("addq","60");
            instructions.put("subq","61");
            instructions.put("andq","62");
            instructions.put("orq","63");
            instructions.put("jmp","70");
            instructions.put("jle","71");
            instructions.put("jl","72");
            instructions.put("je","73");
            instructions.put("jne","74");
            instructions.put("jge","75");
            instructions.put("jg","76");
            instructions.put("call","80");
            instructions.put("ret","90");
            instructions.put("pushq","A0");
            instructions.put("popq","B0");
        }
    private static final HashMap<String,String> registers = new HashMap<String,String>();
    static{
        registers.put("%rax","0");
        registers.put("%rcx","1");
        registers.put("%rdx","2");
        registers.put("%rbx","3");
        registers.put("%rsp","4");
        registers.put("%rbp","5");
        registers.put("%rsi","6");
        registers.put("%rdi","7");
        registers.put("%r8","9");
        registers.put("%r9","9");
        registers.put("%r10","10");
        registers.put("%r11","11");
        registers.put("%r12","12");
        registers.put("%r13","13");
        registers.put("%r14","14");
        registers.put("F","15");
    }
    private static final HashMap<String,Integer> instructionSizes =  new HashMap<String, Integer>();
    static {
        instructionSizes.put("halt", 1);
        instructionSizes.put("nop",1);
        instructionSizes.put("rrmovq",2);
        instructionSizes.put("cmovle",2);
        instructionSizes.put("cmovl",2);
        instructionSizes.put("cmove",2);
        instructionSizes.put("cmovne",2);
        instructionSizes.put("cmovge",2);
        instructionSizes.put("cmovg",2);
        instructionSizes.put("irmovq",10);
        instructionSizes.put("rmmovq",10);
        instructionSizes.put("mrmovq",10);
        instructionSizes.put("addq",2);
        instructionSizes.put("subq",2);
        instructionSizes.put("andq",2);
        instructionSizes.put("orq",2);
        instructionSizes.put("jmp",9);
        instructionSizes.put("jle",9);
        instructionSizes.put("jl",9);
        instructionSizes.put("je",9);
        instructionSizes.put("jne",9);
        instructionSizes.put("jge",9);
        instructionSizes.put("jg",9);
        instructionSizes.put("call",9);
        instructionSizes.put("ret",1);
        instructionSizes.put("pushq",2);
        instructionSizes.put("popq",2);
    }

    public Assembled(String[] entireContent) {

        /*Find instructions*/

        /*Find registers*/

        /*Find Quads*/

        /*Create memory calls! */
        String lastUsed="";
        for(String statement: entireContent){
            if(statement.contains("L") && !labels.containsKey(statement)){
                labels.put(currentMemoryPos,statement);
                lastUsed=statement;

                continue;
            }
            if(instructions.containsKey(statement) || statement.contains(".quad")){
                if(statement.contains(".quad")){
                    currentMemoryPos+=8;
                    lastUsed=statement;

                    continue;
                }
                trackMemory.put(currentMemoryPos,statement);
                currentMemoryPos += instructionSizes.get(statement);
                lastUsed=statement;

                continue;
            }
            //Check for Immediates/Quads
            if(statement.contains("0x") || lastUsed.contains(".quad")) {
                if(statement.contains("$")){
                    String imm = statement.split(",")[0].substring(3);
                    String register = statement.split(",")[1];
                    immValues.put(currentMemoryPos,splitString2(imm));
                    registerValues.put(currentMemoryPos,"F" + registers.get(register));
                    continue;
                }
                //Triggers the read for jumps.
                if(statement.contains("0x") && lastUsed.matches("j.*")){
                    jumpValues.put(currentMemoryPos,splitString2(statement.substring(2)));
                    continue;
                }
                ArrayValues.put(currentMemoryPos,splitString2(statement.substring(2)));
                continue;
            }
            if(statement.contains(",")) {
                String register1 = statement.split(",")[0];
                String register2 = statement.split(",")[1];
                if(register1.contains("0x")){
                    String[] collection = cleanRegister(register1);
                    register1=collection[1];
                    immValues.put(currentMemoryPos,splitString2(collection[0]));

                }else if(register2.contains("0x")){
                    String[] collection = cleanRegister(register2);
                    immValues.put(currentMemoryPos,splitString2(collection[0]));
                    register2=collection[1];
                }
                registerValues.put(currentMemoryPos,registers.get(register1) + registers.get(register2));
                continue;
            }else if(statement.contains(".pos") || statement.contains("array:") || statement.contains("stack")){
                continue;
            }
            System.out.println("Error with file" + statement);

        }
        //System.out.println(Arrays.toString(entireContent));

        for (Map.Entry<Integer, String> entry : trackMemory.entrySet()) {
            Integer memoryValue = entry.getKey();
            String instruction = entry.getValue();

            switch(instruction) {

                case "halt", "nop","ret":
                    System.out.println(instructions.get(instruction));
                    break;
                case "rrmovq","pushq","popq","addq", "subq", "andq", "xorq", "cmovle", "cmovl", "cmove", "cmovne", "cmovge", "cmovg":
                    System.out.println(instructions.get(instruction) +" " +registerValues.get(memoryValue + instructionSizes.get(instruction)));
                    break;
                case "irmovq", "rmmovq","mrmovq":
                    System.out.println(instructions.get(instruction)+ " " + registerValues.get(memoryValue + instructionSizes.get(instruction)) + " " + immValues.get(memoryValue+ instructionSizes.get(instruction)));
                    break;
                case "call":
                    //Use the value collected to get the Label, once you have the label, find the other associated label and use that corresponding memory.
                    //{L789876014=19, L789876014:=36, L1056683174=45, L1056683174:=46, L1415088566:=51, L1415088566=60}
                    //Call will get 19, 19 is L789876014, L789876014 is associated with L789876014: which has memory 36 :D
                    Integer pos = 0;
                    for (Map.Entry<Integer, String> e : labels.entrySet()) {
                        String Label = e.getValue();
                        Integer position = e.getKey();
                        //System.out.println(labels.get(memoryValue + instructionSizes.get(instruction)));
                        if(Label.contains(labels.get(memoryValue + instructionSizes.get(instruction)) + ":")){
                            pos = position;
                        }
                    }
                    String number = splitString2(String.format("%-" + 16 + "x",pos).replace(' ', '0'));
                    System.out.println(instructions.get(instruction) +" "+ number);
                    break;
                case "jmp","jle","jl","je","jne","jge","jg":
                    System.out.println(instructions.get(instruction) + " " + jumpValues.get(memoryValue + instructionSizes.get(instruction)));
                    break;


            }



            if(instruction.contains("halt") && !ArrayValues.isEmpty()){
                for (Map.Entry<Integer, String> en : ArrayValues.entrySet()) {
                    String array = en.getValue();
                    System.out.println(array);
                }
            }
        }


        //System.out.println(trackMemory);
        //System.out.println(registerValues);

        //System.out.println(currentMemoryPos);
        //System.out.println(ArrayValues);
        //System.out.println(immValues);

        //System.out.println(labels);


    }

    /***
     * Splits the string by 2 character sections and returns a combined version.
     * @param str
     * @return
     */
    private static String splitString2(String str) {
        int len = str.length();
        int arraySize = (len + 1) / 2; // Calculate array size, handling odd lengths
        StringBuilder result = new StringBuilder();
        for (int i = 0, j = 0; i < len; i += 2, j++) {
            int endIndex = Math.min(i + 2, len); // Ensure not to go beyond string length
            result.append(str, i, endIndex);
            result.append(" ");
        }
        return result.toString();
    }

    private String[] cleanRegister(String rM) {
        String[] splitArray = rM.split("\\(");//2 parts [0x1000000000000000,%rsi) ]
        String imm = splitArray[0].substring(2);
        String register = splitArray[1].substring(0,splitArray[1].length()-1);

        return new String[] {imm,register};
    }

}
