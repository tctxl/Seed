package com.opdar.seed.io.act;

/**
 * Created by 俊帆 on 2015/8/27.
 */
public abstract class Act {
    public abstract <A,P> A execute(P...acp);
}
