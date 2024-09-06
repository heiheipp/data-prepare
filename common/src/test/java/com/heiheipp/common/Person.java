package com.heiheipp.common;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangxi
 * @version 1.0
 * @className Person
 * @desc TODO
 * @date 2022/3/8 15:50
 */
@Data
@ToString
public class Person implements Cloneable {

    public String name;

    public String email;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
