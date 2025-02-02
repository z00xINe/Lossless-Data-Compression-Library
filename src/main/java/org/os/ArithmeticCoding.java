package org.os;

import java.util.*;
import java.io.*;

class ArithmeticNode {
    double from; // The lower bound of the range.
    double to;   // The upper bound of the range.
    double prob; // The probability (range size).


    ArithmeticNode(double from, double to) {
        this.from = from;
        this.to = to;
        this.prob = to - from;
    }
}

public class ArithmeticCoding {

    static void R() throws IOException {
        if (!System.getProperty("ONLINE_JUDGE", "false").equals("true")) {
            System.setIn(new FileInputStream("input.txt"));
            System.setOut(new PrintStream("output.txt"));
            System.setErr(new PrintStream("error.txt"));
        }
    }

    public static void main(String[] args) throws IOException {
        R();

        Scanner sc = new Scanner(System.in);

        String strToCompress = sc.nextLine();

        Map<Character, ArithmeticNode> compression = new HashMap<>(); // A map to store the ranges for each character

        int len = strToCompress.length();
        String tmp = strToCompress; // Temporary string for frequency calculation.

        System.out.println("Original Size = " + len + " * 8 = " + len * 8 + "\n");

        double range = 0;
        while (!tmp.isEmpty()) {
            char ch = tmp.charAt(0);

            double old = tmp.length();
            tmp = tmp.replace(String.valueOf(ch), ""); // Remove occurrences of the character.
            double nw = tmp.length();

            double prob = (old - nw) / len;
            compression.put(ch, new ArithmeticNode(range, range + prob));// Create a range for the character and store it in the map.
            range += prob;
        }

        double lower = 0, higher = compression.get(strToCompress.charAt(0)).to;
        for (int i = 1; i < strToCompress.length(); i++) { // Loop to calculate final bounds.
            char ch = strToCompress.charAt(i); // Current character.
            ArithmeticNode node = compression.get(ch); // Get its range.
            double x = lower;
            lower = lower + (higher - lower) * node.from;
            higher = x + (higher - x) * node.to;
        }

        Random random = new Random(); // Random object for generating a random double.
        double compressedCode;
        if (lower == higher) // If bounds are the same, use the lower bound.
            compressedCode = lower;
        else {
            // Generate a random double within the bounds.
            do {
                compressedCode = random.nextDouble();
            } while (compressedCode < lower || compressedCode > higher);
        }

        toAscii(compressedCode);

        StringBuilder result = new StringBuilder();

        boolean f = true;
        while (result.length() < len) { // Loop until the decompressed string matches the original length.
            for (Map.Entry<Character, ArithmeticNode> entry : compression.entrySet()) {
                char ch = entry.getKey(); // Current character.
                ArithmeticNode node = entry.getValue();
                // Check if the compressed code falls within the range.
                if (compressedCode >= node.from && compressedCode < node.to) {
                    result.append(ch);

                    if (f) { // For the first iteration, update the bounds directly.
                        lower = node.from;
                        higher = node.to;
                        f = false;
                    } else { // For subsequent iterations, update bounds based on the range.
                        double x = lower;
                        lower = lower + (higher - lower) * node.from;
                        higher = x + (higher - x) * node.to;
                    }
                    // Normalize the compressed code to the current range.
                    compressedCode = (compressedCode - node.from) / (node.to - node.from);
                    break;
                }
            }
        }

        System.out.println("Decompressed String: " + result);
    }

    public static String doubleToBinary(double number) {
        long longBits = Double.doubleToLongBits(number);
        return String.format("%64s", Long.toBinaryString(longBits)).replace(' ', '0');
    }

    public static String toAscii(double number) {
        String binary = doubleToBinary(number);

        System.out.println("Binary Coding: " + binary);

        int rem = binary.length() % 7;

        if (rem != 0)
            binary = "0".repeat(7 - rem) + binary;

        StringBuilder convert = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 7) { // Process 7 bits at a time.
            int decimal = Integer.parseInt(binary.substring(i, i + 7), 2); // Convert 7 bits to decimal.
            convert.append((char) decimal);
        }

        System.out.println("Compressed Code in \"double\": " + number);
        System.out.println("Compressed Code in \"Ascii\": " + convert);
        System.out.println("Compressed size = " + convert.length() +
                " * 8 = " + convert.length() * 8 + "\n");

        return convert.toString();
    }
}