package com.opdar.seed.io.protocol;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class Command {
    String[] commandDesc;
    public Command(byte[] buf) {
        commandDesc = new String(buf).split(":", 2);
    }
    public String getCommand(){
        return commandDesc[0];
    }

    public String getValue(){
        if(commandDesc.length >1){
            return commandDesc[1];
        }
        return null;
    }
}
