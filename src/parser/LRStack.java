package parser;

import lexer.Num;
import lexer.Word;

import java.io.FileNotFoundException;
import java.util.*;

public class LRStack {
    public Stack<StackUnit> lrStack = new Stack<>();
    public Semantic semantic = new Semantic("" +
            "./src/parser/clanguage.txt", "./src/parser/codesemantic.txt");
    //semantic structure
    public CodeList codeList = new CodeList();
    public SignList signList = new SignList();
    public List<Integer> tempList = new LinkedList<>();
    String w, t;
    int offset = 0;

    public LRStack() throws FileNotFoundException {
    }

    public void printStack() {
        System.out.println("-------------stack: ");
        for (LRStack.StackUnit stackUnit : lrStack) {
            System.out.print(stackUnit.element.nodeSymbol.element() + "\t: ");
            System.out.println(stackUnit.fieldMap);
        }
        System.out.println();

    }
    public static class StackUnit {
        public int status;
        public Node element;
        public Map<String, String> fieldMap = new HashMap<>();

        public StackUnit(int status, Node element) {
            this.status = status;
            this.element = element;
        }

        public StackUnit(int status, Node element, Map<String, String> fieldMap) {
            this.status = status;
            this.element = element;
            this.fieldMap = fieldMap;
        }
    }

    public int indexOfStack(String s) {
        for(int i = 0; i < lrStack.size(); i++) {
            if((lrStack.get(i).element.nodeSymbol).element().equals(s)){
                return i;
            }
        }
        return -1;
    }

    public void valuePut(List<String> code, Map<String, String> fieldMap, int order) {
        int firstIndex = -1; int secondIndex = -1;
        boolean firstself = code.get(1).equals(semantic.syntaxList.get(order).get(0));
        boolean secondelf = code.get(3).equals(semantic.syntaxList.get(order).get(0));
        if(firstself) {
            secondIndex = indexOfStack(code.get(3));
            fieldMap.put(code.get(2), lrStack.get(secondIndex).fieldMap.get(code.get(4)));
        } else if (secondelf) {
            firstIndex = indexOfStack(code.get(1));
            lrStack.get(firstIndex).fieldMap.put(code.get(2), fieldMap.get(code.get(4)));
        } else {
            firstIndex = indexOfStack(code.get(1));
            secondIndex = indexOfStack(code.get(3));
            lrStack.get(firstIndex).fieldMap.put(
                    code.get(2), lrStack.get(secondIndex).fieldMap.get(code.get(4)));
        }
    }

    public boolean isSelfStack(List<String> code, int order, int index) {
        return code.get(index).equals(semantic.syntaxList.get(order).get(0));
    }

    public void value(List<String> code, Map<String, String> fieldMap, int order) {
        if(code.size() == 5) {
            if(code.get(3).equals("lookup")){

            } else if(code.get(3).equals("num") || code.get(3).equals("digit")) {
                fieldMap.put("addr", ((Num)lrStack.get(indexOfStack(code.get(3))).element.nodeSymbol).value + "");
                System.out.println("---------------------- the things: " + fieldMap.get("addr"));
            } else if (code.get(3).equals("Integer") || code.get(3).equals("String")) {
                int firstIndex = -1;
                boolean firstself = code.get(1).equals(semantic.syntaxList.get(order).get(0));
                if(firstself) {
                    fieldMap.put(code.get(2), code.get(4));
                } else {
                    firstIndex = indexOfStack(code.get(1));
                    lrStack.get(firstIndex).fieldMap.put(code.get(2), code.get(4));
                }
            } else if (code.get(3).equals("temp")) {
                int firstIndex = -1;
                boolean firstself = code.get(1).equals(semantic.syntaxList.get(order).get(0));
                if(firstself) {
                    System.out.println("this!!!!!!!!!!!!!!!");
                    if(code.get(4).equals("t"))
                        fieldMap.put(code.get(2), t);
                    if(code.get(4).equals("w"))
                        fieldMap.put(code.get(2), w);
                } else {
                    System.out.println("that!!!!!!!!!!!!!!!");
                    firstIndex = indexOfStack(code.get(1));
                    if(code.get(4).equals("t"))
                        lrStack.get(firstIndex).fieldMap.put(code.get(2), t);
                    if(code.get(4).equals("w"))
                        lrStack.get(firstIndex).fieldMap.put(code.get(2), w);
                }

            } else if (code.get(1).equals("temp")) {
                int secondIndex = -1;
                boolean secondelf = code.get(3).equals(semantic.syntaxList.get(order).get(0));
                System.out.println("the code: " + Arrays.toString(code.toArray()));
                if (secondelf) {
                    System.out.println("so!!!!!!!!!!!!!!!");
                    if(code.get(2).equals("t"))
                        t = fieldMap.get(code.get(4));
                    if(code.get(2).equals("w"))
                        w = fieldMap.get(code.get(4));
                } else {
                    System.out.println("ugly!!!!!!!!!!!!!!!");
                    secondIndex = indexOfStack(code.get(3));
                    if(code.get(2).equals("t"))
                        t = lrStack.get(secondIndex).fieldMap.get(code.get(4));
                    if(code.get(2).equals("w"))
                        w = lrStack.get(secondIndex).fieldMap.get(code.get(4));
                }
            } else {
                valuePut(code, fieldMap, order);
            }
        }
    }

    public String getFromStack(String type, int stackIndex) {
        String content = lrStack.get(stackIndex).fieldMap.get(type);
        if(null != content) {
            return content;
        } else {
            return null;
        }
    }

    public void genEqual(List<String> code, Map<String, String> fieldMap, int order) {
        if(code.size() == 6) {
            if(isSelfStack(code, order, 2)) {
                String first = fieldMap.get(code.get(3));
                String second = getFromStack(code.get(5), indexOfStack(code.get(4)));
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            }else if (isSelfStack(code, order, 4)) {
                String second = fieldMap.get(code.get(5));
                String first = getFromStack(code.get(3), indexOfStack(code.get(2)));
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            } else if (code.get(2).equals("id")) {
                String first = ((Word)lrStack.get(indexOfStack("id")).element.nodeSymbol).lexeme;
                String second = getFromStack(code.get(5), indexOfStack(code.get(4)));
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            } else {
                String first = getFromStack(code.get(3), indexOfStack(code.get(2)));
                String second = getFromStack(code.get(5), indexOfStack(code.get(4)));
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            }
        }
    }

    public void genGoto(List<String> code, Map<String, String> fieldMap, int order) {
    }
    public void genIfGoto(List<String> code, Map<String, String> fieldMap, int order) {

    }


    public void generate(List<String> code, Map<String, String> fieldMap, int order) {
        switch (code.get(1)) {
            case "=":
                genEqual(code, fieldMap, order);
                break;
            case "goto":
                genGoto(code, fieldMap, order);
                break;
            case "ifgoto":
                genIfGoto(code, fieldMap, order);
                break;
        }

    }

    public Map<String, String> doSemantic(int order) {
        List<List<String>> semanticCode = semantic.semanticList.get(order);
        Map<String, String> fieldMap = new HashMap<>();

//        System.out.println("before!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(semanticCode.size() == 1 && semanticCode.get(0).size() == 0) {
            System.out.println(Arrays.toString(semanticCode.get(0).toArray()));
            return fieldMap;
        }
//        System.out.println("after!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for (List<String> code : semanticCode) {
            System.out.println("the code: " + Arrays.toString(code.toArray()));
            switch (code.get(0)) {
                case "enter":
//                    System.out.println("here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    signList.enter(code.get(1), code.get(2), offset);
                    break;
                case "offset":
                    int firstIndex = indexOfStack(code.get(1));
                    int width = Integer.parseInt(lrStack.get(firstIndex).fieldMap.get(code.get(2)));
                    offset = offset + width;
                    break;
                case "value":
                    value(code, fieldMap, order);
                    break;
                case "merge":
                    break;
                case "back":
                    break;
                case "gen":
                    generate(code, fieldMap, order);
                    break;
            }
        }
        return fieldMap;
    }
}
