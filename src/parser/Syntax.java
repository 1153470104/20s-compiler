package parser;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Syntax {
    public Set<List<String>> syntax = new HashSet<>();

    public Syntax() {
    }

    public void addSyntax(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String[] line = new String[];
        try{
            String stringLine;
            while (null != (stringLine = br.readLine())) {
                line = stringLine.split(" ");
                List<String> list = new LinkedList<>();
                for(int i = 0; i < line.length; i++) {
                    list.add(line[i]);
                }
                syntax.add(list);
            }

        } catch(Exception e) {
            System.out.println("IOException!");
        }
    }
}
