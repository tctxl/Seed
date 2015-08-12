package com.opdar.framework.template.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.CharBuffer;
import java.util.LinkedList;

/**
 * Created by 俊帆 on 2015/8/12.
 * 逆波兰式四则混合字符串运算
 */
public class ReversePolish {
    private char add = '+';
    private char sub = '-';
    private char mul = '*';
    private char div = '/';
    private char remainder = '%';
    private LinkedList<String> values = new LinkedList<String>();
    private LinkedList<Character> exp = new LinkedList<Character>();

    public static void main(String[] args) {
        System.out.println(new ReversePolish().execute("\"a\" == \"a\""));
    }

    public String execute(String s) {
        CharBuffer buffer = CharBuffer.wrap(s);
        StringBuilder builder = new StringBuilder();
        boolean isNewExp = false;
        int newExp = 0;
        boolean isMulDiv = false;
        boolean isStr = false;
        int token = 0;
        while (buffer.length() != 0) {
            char c = buffer.get();
            if (c == '\"') {
                isStr = !isStr;
            }
            if (!isStr) {
                if (c == ' ') continue;
                if (c == '\n') continue;
                if (c == '\r') continue;
            }
            if (!isStr) {
                if (c == '=' || c == '>' || c == '<' || c == '!') {
                    char c2 = buffer.get();
                    switch (c) {
                        case '>':
                            token = 2;
                            break;
                        case '<':
                            token = 3;
                            break;
                        case '!':
                            token = 6;
                            break;
                    }
                    if (c2 == '=') {
                        switch (c) {
                            case '=':
                                token = 1;
                                break;
                            case '>':
                                token = 4;
                                break;
                            case '<':
                                token = 5;
                                break;
                        }
                    } else if (c == '=' && c2 != '=') {
                        token = 0;
                        System.out.println("parse error" + c);
                    }

                    values.push(builder.toString());
                    builder.delete(0, builder.length());
                    continue;
                }
                if (c == '(') {
                    if (isNewExp) newExp++;
                    else {
                        isNewExp = true;
                        continue;
                    }
                }
                if (isNewExp && c == ')') {
                    if (newExp > 0) newExp--;
                    else {
                        isNewExp = false;
                        ReversePolish reversePolish = new ReversePolish();
                        String result = reversePolish.execute(builder.toString());
                        builder.delete(0, builder.length());
                        values.push(result);
                        continue;
                    }
                }
                if (!isNewExp) {
                    boolean matchExp = false;
                    if (c == mul) {
                        matchExp = true;
                        isMulDiv = true;
                    } else if (c == div) {
                        matchExp = true;
                        isMulDiv = true;
                    } else if (c == remainder) {
                        matchExp = true;
                        isMulDiv = true;
                    }
                    if (c == add) {
                        matchExp = true;
                    } else if (c == sub) {
                        matchExp = true;
                    }
                    if (matchExp) {
                        if (builder.length() > 0)
                            values.push(builder.toString());
                        if (isMulDiv && (c == add || c == sub)) {
                            mulDiv();
                            isMulDiv = false;
                        }
                        exp.push(c);
                        builder.delete(0, builder.length());
                        continue;
                    }
                }
            }
            builder.append(c);
        }
        if (builder.length() > 0) {
            values.push(builder.toString());
        }
        if (isMulDiv) {
            mulDiv();
            isMulDiv = false;
        }
        if (token > 0) {
            if (values.size() == 2) {
                String s1 = values.pollLast();
                String s2 = values.pollLast();
                try {
                    Number s1Num = Double.parseDouble(s1);
                    Number s2Num = Double.parseDouble(s2);
                    switch (token) {
                        case 1:
                            values.push(String.valueOf(s1Num.doubleValue() == s2Num.doubleValue()));
                            break;
                        case 2:
                            values.push(String.valueOf(s1Num.doubleValue() > s2Num.doubleValue()));
                            break;
                        case 3:
                            values.push(String.valueOf(s1Num.doubleValue() < s2Num.doubleValue()));
                            break;
                        case 4:
                            values.push(String.valueOf(s1Num.doubleValue() >= s2Num.doubleValue()));
                            break;
                        case 5:
                            values.push(String.valueOf(s1Num.doubleValue() <= s2Num.doubleValue()));
                            break;
                        case 6:
                            values.push(String.valueOf(s1Num.doubleValue() != s2Num.doubleValue()));
                            break;
                    }
                } catch (NumberFormatException e) {
                    switch (token) {
                        case 1:
                            values.push(String.valueOf(s1.equals(s2)));
                            break;
                        case 6:
                            values.push(String.valueOf(!s1.equals(s2)));
                            break;
                        default:
                            System.out.println("parse error");
                            break;
                    }
                }
            } else {
                System.out.println("parse error");
            }
        }
        while (values.size() != 1) {
            String s1 = values.pollLast();
            String s2 = values.pollLast();
            values.addLast(calculate(s1, exp.pollLast(), s2));
        }
        String result = values.pop();
        if (exp.size() > 0) result = exp.pop() + result;
        if (result.indexOf("\"") == 0 || result.lastIndexOf("\"") == result.length() - 1) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }

    private void mulDiv() {
        String result = calculate(values.pop(), exp.pop(), values.pop());
        values.push(result);
    }

    private String calculate(String s1, char exp, String s2) {
        String result = "";
        boolean isString = false;
        s1 = s1.trim();
        s2 = s2.trim();
        if (s1.indexOf("\"") == 0 && s1.lastIndexOf("\"") == s1.length() - 1) {
            isString = true;
            s1 = s1.substring(1, s1.length() - 1);
        }
        if (s2.indexOf("\"") == 0 && s2.lastIndexOf("\"") == s2.length() - 1) {
            isString = true;
            s2 = s2.substring(1, s2.length() - 1);
        }
        if (isString) {
            if (exp != add) {
                System.out.println("exp error [" + exp + "]");
            }
            result = "\"" + s1 + s2 + "\"";
        } else {
            BigDecimal s1Decimal = new BigDecimal(s1);
            BigDecimal s2Decimal = new BigDecimal(s2);
            switch (exp) {
                case '+':
                    result = s1Decimal.add(s2Decimal).toPlainString();
                    break;

                case '-':
                    result = s1Decimal.subtract(s2Decimal).toPlainString();
                    break;

                case '*':
                    result = s1Decimal.multiply(s2Decimal).toPlainString();
                    break;

                case '/':
                    result = s1Decimal.divide(s2Decimal, 0, RoundingMode.HALF_UP).toPlainString();
                    break;
                case '%':
                    result = s1Decimal.remainder(s2Decimal).toPlainString();
                    break;
            }
        }
        return result;
    }
}
