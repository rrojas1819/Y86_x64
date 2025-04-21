package org.Y86.disassembler;

import java.util.*;

/***
    Class/Object
    Capable of printing a text file of the code!
    Contains all the mappings!
 */
public class Disassembled {
    private HashMap<String, String> memToInstruction,registers,instructions;
    private HashMap<String, String> labels = new HashMap<String,String>();
    private int currentInstructionOffset;
    private boolean start;
    private ArrayList<String> arrays = new ArrayList<String>();
    private Queue<String> memory = new ArrayDeque<>() ,fullInstructions=new ArrayDeque<>();


    /***
     * Creates the instruction Hashmap!
     * To be used for Future overHaul!
     * Or maybe not, depends ._.
     * @return
     */
    private void instructionCreation() {
        instructions =  new HashMap<String, String>();
        instructions.put("00","halt");
        instructions.put("10","nop");
        instructions.put("20","rrmovq");
        instructions.put("21","cmovle");
        instructions.put("22","cmovl");
        instructions.put("23","cmove");
        instructions.put("24","cmovne");
        instructions.put("25","cmovge");
        instructions.put("26","cmovg");
        instructions.put("30","irmovq");
        instructions.put("40","rrmovq");
        instructions.put("50","mrmovq");
        instructions.put("60","addq");
        instructions.put("61","subq");
        instructions.put("62","andq");
        instructions.put("63","orq");
        instructions.put("70","jmp");
        instructions.put("71","jle");
        instructions.put("72","jl");
        instructions.put("73","je");
        instructions.put("74","jne");
        instructions.put("75","jge");
        instructions.put("76","jg");
        instructions.put("80","call");
        instructions.put("90","ret");
        instructions.put("A0","pushq");
        instructions.put("B0","popq");


    }
    private void registerCreation() {
        registers =  new HashMap<String, String>();
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
    }









    /**
     * Checks if the file has the three instructions to signify the start.
     * @param allContents
     * @param memStartPos
     * @return
     */
    private void checkStart(String[] allContents,int memStartPos){
        if(allContents[0].contentEquals("30") && allContents[1].contentEquals("F4") && allContents[10].contentEquals("80") && allContents[19].contentEquals("00")){
            System.out.println(".pos " + memStartPos);
            start=true;
            /*
            System.out.println("irmovq stack, %rsp"); // 10 ->bytes
            System.out.println("call main"); //11 --> is call , 12-19 is the address of main
            System.out.println("halt");
             */
            currentInstructionOffset+= 20;
            StringBuilder addressOfMain = new StringBuilder();
            for(int j =11;j<19;j++){
                addressOfMain.append(allContents[j]);
            }
            String paddedNumber;
            int i=0;
            int other = i;
            StringBuilder fullArray = new StringBuilder();

            while(true){
                //System.out.println(allContents[currentInstructionOffset + i]);
                //System.out.println("HAWRO>?");
                other =i;
                paddedNumber = String.format("%-" + 16 + "x", currentInstructionOffset + i).replace(' ', '0');
                if(addressOfMain.toString().contentEquals(paddedNumber)){//Need the label/memory of main to confirm we have reached well main.
                    //array.add()  //If want to add array, need to just add 8 bytes each time until you reach the main (Another loop
                    //System.out.println("Main connected!!!");
                    parser(allContents,0,20);
                    //System.out.println("\n");
                    currentInstructionOffset=20;
                    parser(allContents,currentInstructionOffset + i,allContents.length);
                    break;
                }
                i+=8;
                fullArray.setLength(0);
                for(int j = currentInstructionOffset + other; j < currentInstructionOffset + i; j++){
                    fullArray.append(allContents[j]);
                }
                arrays.add(fullArray.toString());


            }

            currentInstructionOffset-= 20;
        }
        else {
            parser(allContents,currentInstructionOffset,allContents.length);
        }


    }


    public Disassembled(String[] allContents, int memStartPos) {
        registerCreation();
        //instructionCreation();
        currentInstructionOffset = 0; //Memory isn't real, so the beginning of the indexing of the array is always zero.
        //currentInstructionOffset += 13; // Offset of start --> only occurs when handling non main start.

        /*
        Beginning Portion
         */
        checkStart(allContents,memStartPos);

        String paddedNumber = String.format("%-" + 16 + "x", currentInstructionOffset).replace(' ', '0');

        while(!fullInstructions.isEmpty()){
            if (!labels.isEmpty() && labels.containsKey(memory.peek())) {
                System.out.println("\n" + labels.get(memory.peek()) + ": ");
            }
            memory.remove();
            System.out.println(fullInstructions.remove());
                    //System.out.println(fullInstructions.remove() +"\t" + memory.remove());
        }
        System.out.println(arrays);


        /*
        Ending Portion (Usually the stack)
         */
        if(start){
            paddedNumber = String.format("%-" + 16 + "x", currentInstructionOffset + 300 + memStartPos); //Default 300  :D
            System.out.println("\n.pos " + "0x" + paddedNumber); //0x300 is just a given general number
            System.out.println("stack:");
        }
    }

    private void parser(String[] allContents,int startPos, int length){
        currentInstructionOffset=startPos;
        int width = 16;
        int instructionLength = 0; // For the offsets
        //System.out.println("\nmain:");
        for (int i = startPos; i < length; i += instructionLength) {

            String paddedNumber = String.format("%-" + width + "x", currentInstructionOffset).replace(' ', '0');
            //System.out.println(labels);
            //System.out.println(paddedNumber + ": padded number");
            //System.out.println(currentInstructionOffset + ": actual offset currently");

            /*
            This can be an external check when popping the queue :D
             */


            switch (allContents[i]) {
                //Need all the instructions <---------------
                case "00"://Halt
                    instructionLength = 1;
                    memory.add(paddedNumber);
                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("halt ");
                    //System.out.println("halt ");
                    break;
                case "10"://Nop
                    instructionLength = 1;
                    memory.add(paddedNumber);
                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("halt ");

                    break;
                case "30"://irmovq
                    instructionLength = 10;
                    memory.add(paddedNumber);
                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("irmovq " + "$0x" + cycleReadMemory(allContents, i, instructionLength, "30") + "," + registerDecode( allContents[i] + cycleReadMemory(allContents, i, instructionLength, "30"), 3));

                    break;
                case "40"://rmmovq
                    instructionLength = 10;
                    memory.add(paddedNumber);
                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("rmmovq " + registerDecode(cycleReadMemory(allContents, i, instructionLength, "40"), 0));

                    break;
                case "50"://mrmovq
                    instructionLength = 10;
                    memory.add(paddedNumber);

                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("mrmovq " + registerDecode(cycleReadMemory(allContents, i, instructionLength, "50"), 1));

                    break;
                case "80"://Call
                    instructionLength = 9;
                    memory.add(paddedNumber);

                    currentInstructionOffset += instructionLength;
                    fullInstructions.add(cycleReadMemory(allContents, i, instructionLength, "80"));
                    //System.out.println(labels);
                    break;
                case "90"://Return
                    instructionLength = 1;
                    memory.add(paddedNumber);
                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("ret ");
                    break;
                case "A0"://Push
                    instructionLength = 2;
                    memory.add(paddedNumber);

                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("push " + registerDecode(cycleReadMemory(allContents, i, instructionLength, "A0"), 2));

                    break;
                case "B0"://pop
                    instructionLength = 2;
                    memory.add(paddedNumber);

                    currentInstructionOffset += instructionLength;
                    fullInstructions.add("pop " + registerDecode(cycleReadMemory(allContents, i, instructionLength, "B0"), 2));
                    break;
                default:// Pure laziness in wanting to set up
                    switch (allContents[i].substring(0, 1)) {
                        case "2"://Conditional Moves FALL THROUGH
                        case "6"://Operations FALL THROUGH
                            instructionLength = 2;
                            memory.add(paddedNumber);
                            currentInstructionOffset += instructionLength;
                            //System.out.println(currentInstructionOffset);
                            fullInstructions.add(decodeFamilyHelper(allContents[i], cycleReadMemory(allContents, i, instructionLength, "6")));
                            break;
                        case "7"://Jumps
                            //currentInstructionOffset+=instructionLength;
                            instructionLength = 9;
                            memory.add(paddedNumber);
                            currentInstructionOffset += instructionLength;
                            fullInstructions.add(decodeFamilyHelper(allContents[i], cycleReadMemory(allContents, i, instructionLength, "7")));
                            break;
                        default:
                            System.out.println("Program failed to understand instruction?: " + allContents[i]);
                            if(allContents.length > i){
                                i++;
                            }
                            continue;
                    }


            }

        }
    }

    /**
     * Performs a loop to iterate through the object code of the given that uses memory instruction and returns the assembly Y86_64 understanding.
     * Cases are call, rmmovq, mrmovq,Jumps, irmovq
     * @return
     */
    private String cycleReadMemory(String[] allContents ,int start ,int length,String instruction){
        StringBuilder fullInstruction = new StringBuilder();
        length +=start;
        for(int i = start;i<length ;i++){
            fullInstruction.append(allContents[i]);
        }
        //System.out.println(fullInstruction);
        //System.out.println(length);
        //System.out.println(Arrays.stream(allContents).toList());
        //System.out.println(start);
        //System.out.println(fullInstruction);

        String restToDecode = fullInstruction.substring(2);
        //System.out.println(restToDecode);
        switch(instruction.substring(0,1)){
            case "8":
                String label = createLabel(restToDecode,16);//Just assume max address constantly, never wrong.
//                System.out.println(labels);
                return "call " + label;
                /*Need to deal with the Jumps ALSO NEED to track memory in order to add it!   */
            case "3":
            case "7":
                //Labels are addresses anyway so it depends?
                //System.out.println(restToDecode);
                return restToDecode;


        }
        return fullInstruction.toString();
    }


    /**
     *Made to work with fakeBreak, of the Disassembler class, more importantly does not parse the correct code, however, can in fact parse the fake to some degree!
     * @param allContents
     * @param addressContents
     * @param memToInstruction
     */
    public Disassembled(String allContents, String addressContents, HashMap<String,String> memToInstruction){
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
                System.out.println("\n" + labels.get(currentAddress) + ":");
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
                return "irmovq " + registerDecode(restOfDecode,-1) + "$"+ restOfDecode.substring(2); // This isn't even right LOL
            case "40"://rmmovq
                return "rmmovq " + registerDecode(restOfDecode,0) + addressDecode(restOfDecode);
            case "50"://mrmovq
                return "mrmovq "+ registerDecode(restOfDecode,1) + addressDecode(restOfDecode);
            case "80"://Call
                Map.Entry<String,String> entry = memToInstruction.entrySet().iterator().next();
                String label = createLabel(addressDecode(restOfDecode),entry.getKey().length());
                //System.out.println(labels);
                return "call " + label;
            case "90"://Return
                return "ret ";
            case "A0"://Push
                return "push " + registerDecode(restOfDecode,2);
            case "B0"://pop
                return "pop " + registerDecode(restOfDecode,2);
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
        if(labels.containsKey(addressDecode.substring(0,length))){
            return "L" + labels.get(addressDecode.substring(0,length));
        }
        long toLabel = random.nextLong(0,Integer.MAX_VALUE);
        labels.put(toStore,"L" + toLabel);
        //System.out.println(labels);
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

        return (!toReturn.isEmpty()) ? "0x" + toReturn : "0x" + restOfDecode;
    }

    /**
     * Returns the corresponing register <-- switch again -->
     * @param restOfDecode
     * @return
     */
    private String registerDecode(String restOfDecode,int check) {
        //System.out.println(restOfDecode);
        String firstRegister = restOfDecode.substring(2,3);
        String secondRegister = restOfDecode.substring(3,4);
        firstRegister = registers.get(firstRegister);
        secondRegister = registers.get(secondRegister);
        //System.out.println(firstRegister);
        //System.out.println(secondRegister);

        return switch (check) {
            case 0 -> // rm
                    firstRegister + "," +"0x" + restOfDecode.substring(4) +  "(" + secondRegister + ")";
            case 1 -> //mr This could look better
                    "0x" + restOfDecode.substring(4) + "(" + firstRegister + ")," + secondRegister;
            case 2 -> //Push or pop
                    firstRegister;
            case 3 -> //irmovq
                    secondRegister;


            default -> firstRegister + "," + secondRegister;
        };
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
            case "20":
                return "rrmovq " + registerDecode(restOfDecode,-1);
            case "21"://
                return "cmovle " + registerDecode(restOfDecode,-1);
            case "22"://
                return "cmovl " + registerDecode(restOfDecode,-1);
            case "23"://
                return "cmove " + registerDecode(restOfDecode,-1);
            case "24"://
                return "cmovne " + registerDecode(restOfDecode,-1);
            case "25"://
                return "cmovge " + registerDecode(restOfDecode,-1);
            case "26"://
                return "cmovg " + registerDecode(restOfDecode,-1);
            case "60"://
                return "addq " + registerDecode(restOfDecode,-1);
            case "61"://
                return "subq " + registerDecode(restOfDecode,-1);
            case "62"://
                return "andq " + registerDecode(restOfDecode,-1);
            case "63"://
                return "xorq " + registerDecode(restOfDecode,-1);
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
