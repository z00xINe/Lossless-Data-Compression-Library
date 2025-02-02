package org.os;

import java.io.*;
import java.util.*;

class HuffmanNode {
    char character;
    double probability;

    HuffmanNode(char character, double probability) {
        this.character = character;
        this.probability = probability;
    }
}

public class HuffmanCoding {

    static void R() throws IOException {
        if (!System.getProperty("ONLINE_JUDGE", "false").equals("true")) {
            System.setIn(new FileInputStream("input.txt"));
            System.setOut(new PrintStream("output.txt"));
            System.setErr(new PrintStream("error.txt"));
        }
    }

    static void convertToAscii(String binary) {
        int remainder = binary.length() % 7;
        if (remainder != 0)
            binary = "0".repeat(7 - remainder) + binary;

        StringBuilder convert = new StringBuilder();
        for (int i = 0; i < binary.length(); i+=7) {
            int decimal = Integer.parseInt
                    (binary.substring(i, i+7), 2);
            convert.append((char) decimal);
        }
        System.out.println("Compressed Code in \"Ascii\": "+convert);
        System.out.println("Compressed size = "+convert.length()+
                " * 8 = "+convert.length()*8+"\n");
    }

    public static void main(String[] args) throws IOException {
        R();

        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();

        Map<Character, String> compression = new HashMap<>();
        Map<String, Character> deCompression = new HashMap<>();
        ArrayList<HuffmanNode> nodes = new ArrayList<>();

        int len = str.length();
        String tmp = str;

        System.out.println("Original Size = " + len + " * 8 = " + len*8 + "\n");

        while (!tmp.isEmpty()) {
            char ch = tmp.charAt(0);

            double old = tmp.length();
            tmp = tmp.replace(String.valueOf(ch), "");
            double nw = tmp.length();

            nodes.add(new HuffmanNode(ch, (old-nw)/len));
        }

        nodes.sort(Comparator.comparingDouble(
                (HuffmanNode node) -> node.probability).reversed());

        String code = "0";
        for (HuffmanNode node : nodes) {
            compression.put(node.character, code);
            deCompression.put(code, node.character);

            System.out.println(node.character+" => "+code);
            code = '1' + code;
        }
        System.out.println();

        StringBuilder bin = new StringBuilder();
        for (char i : str.toCharArray())
            bin.append(compression.get(i));

        System.out.println("Binary Coding: "+bin);

        convertToAscii(bin.toString());

        System.out.print("Decompressed String: ");
        StringBuilder compare = new StringBuilder();
        for (int i = 0; i < bin.length(); i++) {
            compare.append(bin.charAt(i));

            if (deCompression.containsKey(compare.toString())) {
                System.out.print(deCompression.get(compare.toString()));
                compare.setLength(0);
            }
        }
    }
}