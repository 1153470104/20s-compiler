package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Semantic {
    public List<List<String>> syntaxList = new LinkedList<>();
    public List<List<List<String>>> semanticList = new LinkedList<>();

    public Semantic(String syntaxFile, String semanticFile) throws FileNotFoundException {
        addContent(syntaxFile, semanticFile);
    }
    public void addContent(String syntaxFile, String semanticFile) throws FileNotFoundException {
        FileReader fr = new FileReader(syntaxFile);
        BufferedReader br = new BufferedReader(fr);
        String[] line = new String[0];
        try{
            String stringLine;
            while (null != (stringLine = br.readLine())) {
                line = stringLine.split(" ");
                List<String> list = new LinkedList<>(Arrays.asList(line));
                syntaxList.add(list);
            }
        } catch(Exception e) {
            System.out.println("IOException!");
        }

        FileReader fr2 = new FileReader(semanticFile);
        BufferedReader br2 = new BufferedReader(fr2);
        String[] line2;
        try{
            String stringLine;
            while(null != (stringLine = br2.readLine())) {
                line2 = stringLine.split(" ");
                List<List<String>> allcodes = new LinkedList<>();
                List<String> code = new LinkedList<>();
                for(int i = 0; i < line2.length; i++) {
                    if(line2[i].equals("|") || i == line2.length - 1) {
                        allcodes.add(code);
                        code = new LinkedList<>();
                    } else {
                        code.add(line2[i]);
                    }
                }
                semanticList.add(allcodes);
            }

        } catch(Exception e) {
            System.out.println("IOException!");
        }
    }

    public void printSemantic() {
        for(int i = 0; i < syntaxList.size(); i++) {
            System.out.print(Arrays.toString(syntaxList.get(i).toArray()) + "\t\t\t\t");
            for(int j = 0; j < semanticList.get(i).size(); j++) {
                System.out.print(Arrays.toString(semanticList.get(i).get(j).toArray()));
            }
            System.out.println();
        }
    }
}
