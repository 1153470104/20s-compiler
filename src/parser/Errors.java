package parser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Errors {

    public List<String> symbolList = new LinkedList<>();

    List<String> operators = Arrays.asList(new String[]{"+", "-", "*"});
    List<String> components = Arrays.asList(new String[]{"id", "("});
    List<String> rightBrackets = Arrays.asList(new String[]{"]", "}", ")"});
    List<String> leftBrackets = Arrays.asList(new String[]{"[", "{", "("});
    List<String> relop = Arrays.asList(new String[]{"<", "<=", "!=", ">", ">=", "=="});
    List<String> reserveds = Arrays.asList(new String[]{"call", "false", "true"
            , "not", "and", "while", "do", "or", "else", "then", "if", "record", "proc"});

    List<String> errorMessage = Arrays.asList(new String[]{"lack of operator", "lack of computing component", "lack of right bracket"
            , "lack of comparision operator", "lack of reserved symbol", "lack of right bracket", "unmatched right parenthesis )", "unmatched right bracket ]"
            , "unmatched right curly brace }"});

    List<ErrorInfo> errors = new LinkedList<>();

    public Errors(){};
    public Errors(List<String> symbolList) {
        this.symbolList = symbolList;
    }

    public int existCount(int row, int[][] chart) {
        int length = chart[0].length;
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (chart[row][i] > 5000) {
                count++;
            }
        }
        return  count;
    }

    public String theOnlyOne(int row, int[][] chart) {
        if(existCount(row, chart) != 1) {
            return null;
        }
        int length = chart[0].length;
        String next = "";
        for (int i = 0; i < length; i++) {
            if (chart[row][i] > 5000) {
                next = symbolList.get(i);
            }
        }
        return next;

    }

    public boolean existType(int row, int[][] chart, String element) {
        int length = chart[0].length;
        if(element.equals("operators")) {
            for(int i = 0; i < length; i++) {
                if(operators.contains(symbolList.get(i))) {
                    if(chart[row][i] != 1000) {
                        return true;
                    }
                }
            }
        }else if(element.equals("components")) {
            for(int i = 0; i < length; i++) {
                if(components.contains(symbolList.get(i))) {
                    if(chart[row][i] != 1000) {
                        return true;
                    }
                }
            }
        }else if(element.equals("rightBrackets")) {
            for(int i = 0; i < length; i++) {
                if(rightBrackets.contains(symbolList.get(i))) {
                    if(chart[row][i] != 1000) {
                        return true;
                    }
                }
            }
        }else if(element.equals("relop")) {
            for(int i = 0; i < length; i++) {
                if(relop.contains(symbolList.get(i))) {
                    if(chart[row][i] != 1000) {
                        return true;
                    }
                }
            }
        }else if(element.equals("reserveds")) {
            for (int i = 0; i < length; i++) {
                if (reserveds.contains(symbolList.get(i))) {
                    if (chart[row][i] != 1000) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setRow(int row, int[][] chart, int errorNum) {
        int length = chart[0].length;
        for(int i = 0; i < length; i++) {
            if(chart[row][i] > 5000) {
                chart[row][i] = errorNum;
            }
        }
    }

    public void setChart(int[][] chart) {
        int length = chart[0].length;
        int height = chart.length;
        for(int i = 0; i < height; i++) {
            boolean operator, component, right, relop, reserve;
            operator = existType(i, chart, "operators");
            component = existType(i, chart, "components");
            right = existType(i, chart, "rightBrackets");
            relop = existType(i, chart, "relop");
            reserve = existType(i, chart, "reserveds");
            //这里只能决定在表格内采取什么样的措施，而不能让表格内直接填上错误内容
            //错误内容是放在另一个method里面的
            if(operator) {
                setRow(i, chart, 6001);
            } else if(component) {
                setRow(i, chart, 6002);
            } else if(right) {
            setRow(i, chart, 6003);
            } else if(relop) {
                setRow(i, chart, 6004);
            } else if(reserve) {
                setRow(i, chart, 6005);
            }
        }
        int endOrder = symbolList.indexOf("$");
        int lbOrder = symbolList.indexOf("(");
        int lcOrder = symbolList.indexOf("[");
        int lpOrder = symbolList.indexOf("{");
        for(int j = 0; j < height; j ++) {
            if(existType(j, chart, "rightBrackets") && chart[j][endOrder] > 5000) {
                chart[j][endOrder] = 6006;
            }
            if(chart[j][lbOrder] > 5000)
                chart[j][lbOrder] = 6007;
            //盲目复制代码导致的问题。。。
            if(chart[j][lcOrder] > 5000)
                chart[j][lcOrder] = 6008;
            if(chart[j][lpOrder] > 5000)
                chart[j][lpOrder] = 6009;
        }

    }

    public void addError(int code, int line, int row, int[][] chart, String peek) {
        String onlyOne = "";
        if(existCount(row, chart) == 1) {
            onlyOne = theOnlyOne(row, chart);
        }
        switch (code){
            case 6001:
                errors.add(new ErrorInfo(line, errorMessage.get(0) + " " + onlyOne));
                break;
            case 6002:
                errors.add(new ErrorInfo(line, errorMessage.get(1) + " " + onlyOne));
                break;
            case 6003:
                errors.add(new ErrorInfo(line, errorMessage.get(2) + " " + onlyOne));
                break;
            case 6004:
                errors.add(new ErrorInfo(line, errorMessage.get(3) + " " + onlyOne));
                break;
            case 6005:
                errors.add(new ErrorInfo(line, errorMessage.get(4) + " " + onlyOne));
                break;
            case 6006:
                errors.add(new ErrorInfo(line, errorMessage.get(5)));
                break;
            case 6007:
                errors.add(new ErrorInfo(line, errorMessage.get(6)));
                break;
            case 6008:
                errors.add(new ErrorInfo(line, errorMessage.get(7)));
                break;
            case 6009:
                errors.add(new ErrorInfo(line, errorMessage.get(8)));
                break;

        }
    }

    static class ErrorInfo {
        public int line;
        public String errorInfo;

        public ErrorInfo(int line, String errorInfo) {
            this.line = line;
            this.errorInfo = errorInfo;
        }
    }

    public void errorPrint() {
        System.out.println("The Error: ");
        for(ErrorInfo e: errors) {
            System.out.println("Error "+ "[" + e.line + "]" + ": " + e.errorInfo);
        }
    }
}
