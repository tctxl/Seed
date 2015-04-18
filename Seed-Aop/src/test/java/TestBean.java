// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.opdar.framework.aop.base.TypeReference;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TestBean {
    public static Type getType(){
        return new TypeReference<ArrayList<TestBean>>(){}.getType();
    }
}
