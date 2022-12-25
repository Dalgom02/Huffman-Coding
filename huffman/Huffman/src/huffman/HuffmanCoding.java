package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     * @return 
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);

	/* Your code goes here */
            // create new arraylist that outputs the frequency
        int[] ascii = new int[128];
        double size = 0;
       sortedCharFreqList = new ArrayList<>();

        while (StdIn.hasNextChar()) { // reads through text file char by char
            var v = StdIn.readChar();
            ascii[(int) v]++;
            size++;
        }

        for (int i = 0; i < ascii.length; i++) {
            if (ascii[i] != 0) {
                CharFreq newNode = new CharFreq((char) i, ascii[i] / size);
                sortedCharFreqList.add(newNode);
            }
        }

        if(sortedCharFreqList.size() == 1) {
            char c = sortedCharFreqList.get(0).getCharacter();
            int v = ((int) c);
            v++;
            char newChar = (char)v;
            CharFreq newNode = new CharFreq(newChar, 0);
            sortedCharFreqList.add(newNode); 
        }

        Collections.sort(sortedCharFreqList);


    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {

        Queue<TreeNode> source = new Queue<TreeNode>();
        Queue<TreeNode> target = new Queue<TreeNode>();
        TreeNode f;
        TreeNode s;

        for(int i = 0; i < sortedCharFreqList.size(); i++){
            CharFreq freq = sortedCharFreqList.get(i);
            source.enqueue(new TreeNode(freq, null, null));
        }

        while(!source.isEmpty()){
            f = dq(source, target);
            s = dq(source, target);
            if(f != null && s != null){
                TreeNode intNode = iNode(f,s);
                target.enqueue(intNode);
            } else if(s != null){
                target.enqueue(s);
            } else if (s == null){
                target.enqueue(f);
            }
        }
        
        while(target.size() > 1){
            f = target.dequeue();
            s = target.dequeue();
            TreeNode intNode = iNode(f, s);
            target.enqueue(intNode);
        }
            huffmanRoot = target.dequeue();
    }

    private static TreeNode dq(Queue<TreeNode>a, Queue<TreeNode>b){
        
        TreeNode x = null;
        TreeNode y = null;
        TreeNode temp;

        if(!b.isEmpty()){
            y = b.peek();
        }
        if(!a.isEmpty()){
            x = a.peek();
        }
        if(x != null && y != null){
            if(x.getData().getProbOcc() <= y.getData().getProbOcc()){
                temp = a.dequeue();
            } else{
                temp = b.dequeue();
            }
        } else if(x != null){
            temp = a.dequeue();
        } else if(y != null){
            temp = b.dequeue();
        } else{
            temp = null;
        }

        return temp;
    }

    private static TreeNode iNode (TreeNode f, TreeNode s){
        double freq = f.getData().getProbOcc() + s.getData().getProbOcc();
        CharFreq cNode = new CharFreq(null, freq);
        TreeNode intNode = new TreeNode(cNode, f, s);
        return intNode;
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    
     public void makeEncodings() {

	/* Your code goes here */
        String[] s = new String[128];
        ArrayList<String> aList = new ArrayList<>();
        addTree(huffmanRoot, s, aList);
        encodings = s;
    }
    private void addTree (TreeNode n, String[] s, ArrayList<String>aList){
        if(n.getData().getCharacter() != null){
            s[n.getData().getCharacter()] = String.join("",aList);
            aList.remove(aList.size()-1);
            return;
        }
        if(n.getLeft() != null){
            aList.add("0");
        }
        addTree(n.getLeft(), s, aList);
        if(n.getRight() != null){
            aList.add("1");
        }
        addTree(n.getRight(), s, aList);
        if(!aList.isEmpty()){
            aList.remove(aList.size()-1);
        }
    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
	/* Your code goes here */
        String s = "";

        while(StdIn.hasNextChar()){
         int i = (int)(StdIn.readChar()); //index
         s = s + encodings[i];
        }
        writeBitString(encodedFile, s);

    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);

	/* Your code goes here */

        String read = readBitString(encodedFile);
        String s = "";
        TreeNode p = huffmanRoot;

        for(int i = 0; i<read.length(); i++){
            if(read.charAt(i) == '0'){
                p = p.getLeft();
            } else if (read.charAt(i) == '1'){
                p = p.getRight();
            }  if(p.getLeft() == null && p.getRight() == null) {
                char leaf = p.getData().getCharacter(); 
                s = s + leaf;
               p =  huffmanRoot;
             }
            
        }
            decodedFile = s;
            StdOut.print(s);


    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
