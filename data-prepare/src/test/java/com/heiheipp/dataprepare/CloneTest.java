package com.heiheipp.dataprepare;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangxi
 * @version 1.0
 * @className CloneTest
 * @desc TODO
 * @date 2022/3/8 15:51
 */
public class CloneTest {

    @Test
    public void testClone() throws CloneNotSupportedException {
        Person person1 = new Person();
        person1.setEmail("1-11");
        person1.setName("1-22");

        Person person2 = (Person) person1.clone();
        person2.setName("2-22");

        System.out.println("person1:person2=" + (person1 == person2));
        System.out.println("person1=" + person1 + ", pointer1=" + System.identityHashCode(person1));
        System.out.println("person2=" + person2 + ", pointer2=" + System.identityHashCode(person2));
    }

    @Test
    public void testFor() throws Exception {
        List<Person> personList = new ArrayList<>();

        long begin = System.currentTimeMillis();
        Person originPerson = new Person();
        Person person = null;
        for (int i = 0; i < 6000000; i++) {
            //person = (Person) originPerson.clone();
            person = new Person();
            person.setName(String.valueOf(i + 1));
            personList.add(person);
        }

        System.out.println("execution time=" + (System.currentTimeMillis() - begin));

//        for (Person p : personList) {
//            System.out.println("person=" + p + ", pointer=" + System.identityHashCode(p));
//        }
    }
}
