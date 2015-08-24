package com.opdar.seed.extra.utils;

/**
 * Created by 俊帆 on 2015/8/23.
 */
public class DefaultDecoder implements Decoder {

    @Override
    public byte[] decoder(byte[] result) {
        return result;
    }
}
