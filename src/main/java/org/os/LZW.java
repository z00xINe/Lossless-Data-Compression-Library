package org.os;

import java.io.*;
import java.util.*;

public class LZW {

    static void R() throws IOException {
        if (!System.getProperty("ONLINE_JUDGE", "false").equals("true")) {
            System.setIn(new FileInputStream("input.txt"));
            System.setOut(new PrintStream("output.txt"));
            System.setErr(new PrintStream("error.txt"));
        }
    }

    public static void main(String[] args) throws IOException {
        R();

        Map<String, Integer> tableCom = new HashMap<>();

        Map<Integer, String> tableDecom = new HashMap<>();

        Map<Character, Boolean> repeated = new HashMap<>();

        List<Integer> res = new ArrayList<>();

        StringBuilder convert = new StringBuilder();

        for (int i = 65; i <= 127; i++) {

            convert.append((char) i);

            tableCom.put(convert.toString(), i);

            tableDecom.put(i, convert.toString());

            convert.setLength(0);
        }

        Scanner sc = new Scanner(System.in);

        String stat = sc.next();

        int counter = 128;

        for (int i = 0; i < stat.length(); i++) {

            convert.append(stat.charAt(i));

            if (!tableCom.containsKey(convert.toString())) {
                tableDecom.put(counter, convert.toString());

                res.add(counter);

                tableCom.put(convert.toString(), counter++);

                String tmp = convert.substring(0, convert.length() - 1);

                System.out.println("<" + tableCom.get(tmp) + ">");

                convert.setLength(0);
                i--;
            }
        }

        for (int i : res) {

            System.out.print(tableDecom.get(i));
        }
    }
}