package com.opdar.seed.io.token;

import com.opdar.seed.io.utils.ByteArrayCompare;
import com.opdar.seed.io.utils.ByteArrayStartWithCompare;
import javassist.bytecode.ByteArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class TokenUtil {
    protected static List<Token> tokens = new LinkedList<Token>();

    public static Token add(Token token) {
        tokens.add(token);
        return token;
    }


    public static boolean startWith(byte[] token) {
        return tokens.contains(new ByteArrayStartWithCompare(token,"token"));
    }

    public static boolean contains(byte[] token) {
        return tokens.contains(new ByteArrayCompare(token,"token"));
    }

    public static void main(String[] args) {

        TokenUtil.add(new ClusterToken());
        byte[] header = new byte[]{'c','s','a'};
        if(TokenUtil.startWith(header)){
            System.out.println("Start With Match");
            if (TokenUtil.contains(header)) {
                System.out.println("Contains");
            }
        }
    }
    public static Token get(byte[] token) {
        return tokens.get(tokens.indexOf(new ByteArrayCompare(token,"token")));
    }
}
