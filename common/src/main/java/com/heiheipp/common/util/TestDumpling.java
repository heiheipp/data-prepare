package com.heiheipp.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestDumpling {

    public static void main(String[] args){
        InputStream in = null;
        try {
            Process pro = Runtime.getRuntime().exec(new String[]{"/home/tidb/.tiup/components/dumpling/v5.4.0/dumpling",
                    "-u root -p sa123456 -h 172.16.4.81 -P 5000 --filetype csv -o /home/zx --sql \"select * from zx.t\""});
            pro.waitFor();
            in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String result = read.readLine();
            System.out.println("INFO:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
