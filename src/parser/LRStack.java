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
    public Errors semanticErrors = new Errors();
    public List<String> tempList = new LinkedList<>();
    String w, t;
    int offset = 0;
    public List<List<Integer>> jumplist = new LinkedList<>();

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
        for(int i = lrStack.size()-1; i >= 0; i--) {
            if((lrStack.get(i).element.nodeSymbol).element().equals(s)){
                return i;
            }
        }
        return -1;
    }
    public int indexOfStack(String s, int count) {
        int a = 0;
        for(int i = lrStack.size()-1 ; i > 0; i--) {
            if((lrStack.get(i).element.nodeSymbol).element().equals(s)){
                a++;
                if(a == count) {
                    return i;
                }
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
            if(code.get(4).equals("type")) {
                if(lrStack.get(secondIndex).fieldMap.containsKey("array")) {
                    fieldMap.put("array", getFromStack("array", secondIndex));
                    int i = 1;
                    while(lrStack.get(secondIndex).fieldMap.containsKey("array" + i)) {
                        fieldMap.put("array" + i, getFromStack("array"+i, secondIndex));
                        i++;
                    }
                }
            }
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
        if(code.size() == 4) {
            if(code.get(1).equals("temp")) {
                tempList.add("doesn't matter");
                fieldMap.put("temp", (tempList.size() - 1) + "");
            } else if(code.get(3).equals("newtemp")) {
                fieldMap.put(code.get(2), "temp");
                tempList.add("doesn't matter");
                fieldMap.put(code.get(2) + "temp", (tempList.size() - 1) + "");
            } else if(code.get(3).equals("nextquad")) {
                fieldMap.put("quad", codeList.codelist.size()+"");
            }
        } else if(code.size() == 5) {
            if (code.get(3).equals("lookup")) {
                //这里可能会有先后顺序的问题！！到底是哪个id
                String id = ((Word) lrStack.get(indexOfStack("id")).element.nodeSymbol).lexeme;
                int indexId = signList.lookup(id);
                if (indexId == -1) {
                    System.out.println("this is an error!!!!!!!!!!!!!!!!!");
                    semanticErrors.addS(
                            new Errors.ErrorInfo(lrStack.peek().element.nodeSymbol.line
                                    , "Variable \"" + id + "\" usage with out been declared"));
                    fieldMap.put("lookup", "fail");
                    return;
                }
                StackUnit stack = signList.symbolEntryList.get(indexId).type;
                if (stack.fieldMap.containsKey("array")) {
                    fieldMap.put("array", stack.fieldMap.get("array"));
                    int i = 1;
                    while (stack.fieldMap.containsKey("array" + i)) {
                        fieldMap.put("array" + i, stack.fieldMap.get("array" + i));
                        i++;
                    }
                }
                fieldMap.put("type", stack.fieldMap.get("type"));
                fieldMap.put("addr", id);
            } else if (code.get(3).equals("makelist")) {
                int i = 0;
                if (code.get(4).equals("nextquadplus")) {
                    i = 1;
                }
                fieldMap.put(code.get(2), jumplist.size() + "");
                List<Integer> list = new LinkedList<>();
                list.add(codeList.lineNumber() + i);
                jumplist.add(list);
            } else if (code.get(3).equals("num") || code.get(3).equals("digit")) {
                fieldMap.put("addr", ((Num) lrStack.get(
                        indexOfStack(code.get(3))).element.nodeSymbol).value + "");
                System.out.println("---------------------- the things: " + fieldMap.get("addr"));
            } else if (code.get(3).equals("Integer") || code.get(3).equals("String")) {
                int firstIndex = -1;
                boolean firstself = code.get(1).equals(semantic.syntaxList.get(order).get(0));
                if (firstself) {
                    fieldMap.put(code.get(2), code.get(4));
                } else {
                    firstIndex = indexOfStack(code.get(1));
                    lrStack.get(firstIndex).fieldMap.put(code.get(2), code.get(4));
                }
            } else if (code.get(3).equals("temp")) {
                int firstIndex = -1;
                boolean firstself = code.get(1).equals(semantic.syntaxList.get(order).get(0));
                if (firstself) {
                    if (code.get(4).equals("t"))
                        fieldMap.put(code.get(2), t);
                    if (code.get(4).equals("w"))
                        fieldMap.put(code.get(2), w);
                } else {
                    firstIndex = indexOfStack(code.get(1));
                    if (code.get(4).equals("t"))
                        lrStack.get(firstIndex).fieldMap.put(code.get(2), t);
                    if (code.get(4).equals("w"))
                        lrStack.get(firstIndex).fieldMap.put(code.get(2), w);
                }

            } else if (code.get(1).equals("temp")) {
                int secondIndex = -1;
                boolean secondelf = code.get(3).equals(semantic.syntaxList.get(order).get(0));
                System.out.println("the code: " + Arrays.toString(code.toArray()));
                if (secondelf) {
                    if (code.get(2).equals("t"))
                        t = fieldMap.get(code.get(4));
                    if (code.get(2).equals("w"))
                        w = fieldMap.get(code.get(4));
                } else {
                    secondIndex = indexOfStack(code.get(3));
                    if (code.get(2).equals("t"))
                        t = lrStack.get(secondIndex).fieldMap.get(code.get(4));
                    if (code.get(2).equals("w"))
                        w = lrStack.get(secondIndex).fieldMap.get(code.get(4));
                }
            } else if (code.get(4).equals("subtype")) {
                if (code.get(1).equals("M13")) {
                    int i = 1;
                    while (fieldMap.containsKey("array" + i)) {
                        i++;
                    }
                    i = i - 1;
                    String array = fieldMap.get("array" + i);
                    fieldMap.remove("array" + i);
                    if (i != 0)
                        fieldMap.put("array", array);
                } else if (code.get(1).equals("M14")) {
                    int indexL = indexOfStack("[") - 1;
                    Map<String, String> map = lrStack.get(indexL).fieldMap;
                    fieldMap.put("type", map.get("type"));
                    int i = 1;
                    while (map.containsKey("array" + i)) {
                        fieldMap.put("array" + i, map.get("array" + i));
                        i++;
                    }
                    i = i - 1;
                    String array = fieldMap.get("array" + i);
                    fieldMap.remove("array" + i);
                    if (array != null)
                        fieldMap.put("array", array);
                }
            } else {
                valuePut(code, fieldMap, order);
            }
        } else if(code.size() == 6) {
            int index = indexOfStack(code.get(3));
            fieldMap.put(code.get(2), getFromStack(code.get(5), index));

        } else if(code.size() == 8) {
            //still with only situation
            int c1 = indexOfStack("C");
            int num = indexOfStack("num");
            int numValue = ((Num)lrStack.get(num).element.nodeSymbol).value;
            fieldMap.put("array", numValue + "");
            fieldMap.put("type", getFromStack("type", c1));
            if(lrStack.get(c1).fieldMap.containsKey("array")) {
                int i = 1;
                while(lrStack.get(c1).fieldMap.containsKey("array" + i)) {
                    fieldMap.put("array"+i, lrStack.get(c1).fieldMap.get("array" + i));
                    i++;
                }
                fieldMap.put("array"+i, getFromStack("array", c1));
            }

        } else if(code.size() == 9) {
            //this is the only situation when array is declared
            int c1 = indexOfStack("C");
            int num = indexOfStack("num");
            int c1value = Integer.parseInt(getFromStack("width", c1));
            int numValue = ((Num)lrStack.get(num).element.nodeSymbol).value;
            int putValue = c1value * numValue;
            fieldMap.put("width", putValue + "");
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

    public String typewidth(int index) {
        Map<String, String> map = lrStack.get(index).fieldMap;
        String type = map.get("type");
        int width = 1;
        if(type.equals("int"))
            width =  4;
        else if(type.equals("float"))
            width = 8;

        if(map.containsKey("array")) {
            width = width * Integer.parseInt(map.get("array"));
            int i = 1;
            while(map.containsKey("array" + i)) {
                width = width * Integer.parseInt(map.get("array" + i));
                i++;
            }
        }
        return width + "";
    }

    public void genEqual(List<String> code, Map<String, String> fieldMap, int order) {
        if(code.size() == 6) {
            if(code.get(5).equals("offset")) {
                if (!fieldMap.containsKey("array")) {
                    return;
                } else {
                    fieldMap.put("offset", "temp");
                    tempList.add("emmmmm");
                    fieldMap.put("offset" + "temp", tempList.size() - 1 + "");
                    int indexL = indexOfStack(code.get(4));
                    codeList.add("t" + (tempList.size() - 1) + " = "
                            + "t" + lrStack.get(indexL).fieldMap.get("offsettemp"));
                    codeList.add("=", "t" + lrStack.get(indexL).fieldMap.get("offsettemp")
                            , null, "t" + (tempList.size() - 1));
                }
            } else if(isSelfStack(code, order, 2)) {
                String first = fieldMap.get(code.get(3));
                String second = getFromStack(code.get(5), indexOfStack(code.get(4)));
                if(code.get(3).equals("array")){
                    first = fieldMap.get("addr");
                }
                if(first.equals("temp")) {
                    first = "t" + fieldMap.get(code.get(3)+"temp");
                }
                if(second.equals("temp")) {
                    second = "t" + getFromStack(code.get(5) + "temp", indexOfStack(code.get(4)));
                }
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            }else if (isSelfStack(code, order, 4)) {
                String second = fieldMap.get(code.get(5));
                String first = getFromStack(code.get(3), indexOfStack(code.get(2)));
                if(first.equals("temp")) {
                    first = "t" + getFromStack(code.get(3) + "temp", indexOfStack(code.get(2)));
                }
                if(second.equals("temp")) {
                    second = "t" + fieldMap.get(code.get(5)+"temp");
                }
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            } else if (code.get(2).equals("id")) {
                String first = ((Word)lrStack.get(
                        indexOfStack("id")).element.nodeSymbol).lexeme;
                String second = getFromStack(code.get(5), indexOfStack(code.get(4)));
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            } else {
                String first = getFromStack(code.get(3), indexOfStack(code.get(2)));
                String second = getFromStack(code.get(5), indexOfStack(code.get(4)));
                if(getFromStack("lookup", indexOfStack(code.get(2))) != null) {
                    return;
                }
                if(code.get(3).equals("array")){
                    first = getFromStack("addr", indexOfStack(code.get(2)));
                }
                if(first.equals("temp")) {
                    first = "t" + getFromStack(code.get(3) + "temp", indexOfStack(code.get(2)));
                }
                if(second.equals("temp")) {
                    second = "t" + getFromStack(code.get(5) + "temp", indexOfStack(code.get(4)));
                }
                codeList.add(first + " = " + second);
                codeList.add("=", second, null, first);
            }
        } else if(code.size() == 9) {
            if(code.get(6).equals("*") && code.get(8).equals("typewidth")) {
                int indexE = indexOfStack("E");
                String StringE = lrStack.get(indexE).fieldMap.get("addr");
                if(StringE.equals("temp"))
                    StringE = "t" + lrStack.get(indexE).fieldMap.get("addrtemp");
                String typeWidth = typewidth(indexOfStack("M14"));
                String t = fieldMap.get("temp");
                codeList.add("t"+t + " = " + StringE + " * " + typeWidth);
                codeList.add("=", StringE, typeWidth, "t"+t);
            } else {
                String result = "t" + fieldMap.get("addrtemp");
                int index1 = indexOfStack(code.get(4));
                String content1 = getFromStack(code.get(5), index1);
                int index2 = indexOfStack(code.get(7));
                String content2 = getFromStack(code.get(8), index2);
                if(content2.equals("temp")) {
                    content2 = getFromStack(code.get(8) + "temp", index2);
                }
                if(content1 == null) {
                    codeList.add(result + " = " + content2);
                    codeList.add("=", content2, null, result);
                } else {
                    if(content1.equals("temp")) {
                        content1 = getFromStack(code.get(5) + "temp", index1);
                    }
                    codeList.add(result + " = " + content1 + " " + code.get(6) + " " + content2);
                    codeList.add(code.get(6), content2, content1, result);
                }
            }
        } else if(code.size() == 10) {
            if(code.get(3).equals("offset") && code.get(6).equals("offset")) {
                String offsetL = "t" + fieldMap.get("offsettemp");
                String tempt = "t" + fieldMap.get("temp");
                int indexSub = indexOfStack("L'");
                int indexM = indexOfStack("M14");
                if(!lrStack.get(indexM).fieldMap.containsKey("array")) {
                    codeList.add(offsetL + " = " + getFromStack(
                            "offset", indexSub) + " + " + tempt);
                    codeList.add("=", tempt, getFromStack("offset", indexSub), offsetL);
                    return;
                }
                String offsetM = "t" + getFromStack("offsettemp", indexSub);
                codeList.add(offsetL + " = " + offsetM + " + " + tempt);
                codeList.add("+", offsetM, tempt, offsetL);
            }
        }
    }

    public void genGoto(List<String> code, Map<String, String> fieldMap, int order) {
        if(code.get(2).equals("null")){
            codeList.add("goto");
            codeList.add("j", null, null, null);
        }
    }
    public void genIfGoto(List<String> code, Map<String, String> fieldMap, int order) {
        int index1 = indexOfStack("E", 2);
        String addr1 = "t" + getFromStack("addrtemp", index1);
        int index2 = indexOfStack("E", 1);
        String addr2 = "t" + getFromStack("addrtemp", index2);
        int indexR = indexOfStack("relop");
        String relop = lrStack.get(indexR).element.nodeSet.get(0).nodeSymbol.element();
        codeList.add("if " + addr1 + " " + relop + " " + addr2 + " goto");
        codeList.add("j"+relop, addr1, addr2, null);
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
            case "exit":
                codeList.add("exit");
                codeList.add("exit", null, null, null);
                break;
        }

    }

    public void enter(List<String> code){
        int index = indexOfStack(code.get(2));
        int idIndex = indexOfStack("id");
        String id = ((Word)lrStack.get(idIndex).element.nodeSymbol).lexeme;
        if(signList.contain(id)) {
            semanticErrors.addS(new Errors.ErrorInfo(lrStack.peek().element.nodeSymbol.line
                    , "Duplicate declaration of an existed variable \"" + id + "\""));
        }
        signList.enter(id, lrStack.get(index), offset);
    }

    public void back(List<String> code, Map<String, String> fieldMap, int order) {
        if(code.size() == 5) {
            int indexQuad = indexOfStack(code.get(3));
            String codeNum = getFromStack("quad", indexQuad);
            int indexB = indexOfStack(code.get(1));
            String tempContent = getFromStack(code.get(2), indexB);
            if(tempContent == null) {
                return;
            } else {
                int listNum = Integer.parseInt(tempContent);
                List<Integer> list = jumplist.get(listNum);
                for (Integer i : list) {
                    codeList.append(codeNum, i);
                }
            }
        } else if(code.size() == 6) {
            int indexQuad = indexOfStack(code.get(4));
            String codeNum = getFromStack("quad", indexQuad);
            int indexB = indexOfStack(code.get(1), Integer.parseInt(code.get(2)));
            String tempContent= getFromStack(code.get(3), indexB);
            if(tempContent == null) {
                return;
            } else {
                int listNum = Integer.parseInt(tempContent);
                List<Integer> list = jumplist.get(listNum);
                for (Integer i : list) {
                    codeList.append(codeNum, i);
                }
            }
        }
    }

    public void merge(List<String> code, Map<String, String> fieldMap, int order) {
        if(code.size()==11) {
            fieldMap.put(code.get(2), jumplist.size()+"");
            List<Integer> list = new LinkedList<>();

            int index1 = indexOfStack("S", 1);
            String int1 = getFromStack("nextlist", index1);
            if(int1!=null) {
                int list1 = Integer.parseInt(int1);
                list.addAll(jumplist.get(list1));
            }
            int index2 = indexOfStack("S", 2);
            String int2 = getFromStack("nextlist", index2);
            if(int2!=null) {
                int list2 = Integer.parseInt(int2);
                list.addAll(jumplist.get(list2));
            }
            int index3 = indexOfStack("N1");
            String int3 = getFromStack("nextlist", index3);
            if(int3!=null) {
                int list3 = Integer.parseInt(int3);
                list.addAll(jumplist.get(list3));
            }
            jumplist.add(list);

        } else if(code.size() == 9) {
            fieldMap.put(code.get(2), jumplist.size()+"");
            List<Integer> list = new LinkedList<>();

            int index1 = indexOfStack(code.get(3), 2);
            String int1 = getFromStack(code.get(5), index1);
            if(int1!=null) {
                int list1 = Integer.parseInt(int1);
                list.addAll(jumplist.get(list1));
            }
            int index2 = indexOfStack(code.get(6), 1);
            String int2 = getFromStack(code.get(8), index2);
            if(int2!=null) {
                int list2 = Integer.parseInt(int2);
                list.addAll(jumplist.get(list2));
            }
            jumplist.add(list);

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
                    enter(code);
                    break;
                case "offset":
                    int firstIndex = indexOfStack(code.get(1));
                    int width = Integer.parseInt(lrStack.get(
                            firstIndex).fieldMap.get(code.get(2)));
                    offset = offset + width;
                    break;
                case "value":
                    value(code, fieldMap, order);
                    break;
                case "merge":
                    merge(code, fieldMap, order);
                    break;
                case "back":
                    back(code, fieldMap, order);
                    break;
                case "gen":
                    generate(code, fieldMap, order);
                    break;
            }
        }
        return fieldMap;
    }
}
