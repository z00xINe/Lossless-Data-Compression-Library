package org.os;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LZ77 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String stat = sc.nextLine();
        List<Integer> position = new ArrayList<>();
        List<Integer> len = new ArrayList<>();
        StringBuilder sy = new StringBuilder();
        StringBuilder res = new StringBuilder();

        System.out.println("<0,0,'" + stat.charAt(0) + "'>");
        position.add(0);
        len.add(0);
        sy.append(stat.charAt(0));

        String tmp = "";
        int pos = 1, LastEqual = 0;

        for (int i = 1; i < stat.length(); ++i) {
            tmp += stat.charAt(i);
            boolean found = false;

            for (int j = 0; j + tmp.length() <= pos; ++j) {
                String tst = stat.substring(j, j + tmp.length());
                if (tst.equals(tmp)) {
                    found = true;
                    LastEqual = j;
                }
            }

            if (!found) {
                char AddedChar = tmp.charAt(tmp.length() - 1);
                if (tmp.length() == 1) {
                    System.out.println("<0,0,'" + AddedChar + "'>");
                    position.add(0);
                    len.add(0);
                    sy.append(AddedChar);
                } else {
                    tmp = tmp.substring(0, tmp.length() - 1);
                    System.out.println("<" + (pos - LastEqual) + "," + tmp.length() + ",'" + AddedChar + "'>");
                    position.add(pos - LastEqual);
                    len.add(tmp.length());
                    sy.append(AddedChar);
                }
                tmp = "";
                pos = i + 1;
            }
        }

        if (!tmp.isEmpty()) {
            char AddedChar = tmp.charAt(tmp.length() - 1);
            tmp = tmp.substring(0, tmp.length() - 1);
            for (int i = 0; i + tmp.length() <= stat.length() - tmp.length(); ++i) {
                String tst = stat.substring(i, i + tmp.length());
                if (tst.equals(tmp))
                    LastEqual = i;
            }
            System.out.println("<" + (stat.length() - tmp.length() - LastEqual) + "," + tmp.length() + ",'" + AddedChar + "'>");
            position.add(stat.length() - tmp.length() - LastEqual);
            len.add(tmp.length());
            sy.append(AddedChar);
        }

        for (int i = 0; i < position.size(); ++i) {
            if (position.get(i) == 0) {
                res.append(sy.charAt(i));
            } else {
                int start = res.length() - position.get(i);
                for (int j = 0; j < len.get(i); ++j) {
                    res.append(res.charAt(start + j));
                }
                res.append(sy.charAt(i));
            }
        }

        System.out.println(res.toString());
    }
}
