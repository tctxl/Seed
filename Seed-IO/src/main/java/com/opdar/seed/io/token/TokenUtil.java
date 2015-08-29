package com.opdar.seed.io.token;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class TokenUtil {
    protected static Map<Long, Token> tokens = new HashMap<Long, Token>();

    public static Token add(Token token) {
        return tokens.put(token.getToken(), token);
    }

    public static boolean contains(long i) {
        return tokens.containsKey(i);
    }

    public static Token get(long i) {
        return tokens.get(i);
    }
}
