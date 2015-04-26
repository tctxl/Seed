package com.opdar.framework.db.impl;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Jeffrey on 2015/4/26.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface MappingFilter {
    HashSet<String> getFilter();
    HashMap<String,String> getRedefinItionField();
}
