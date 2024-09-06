package com.heiheipp.common;

import java.io.*;
import java.sql.*;

public class PictureReadAndRead {

    //1.连接数据库
    Connection conn=null;
    public PictureReadAndRead () {
        try {
            String url="jdbc:mysql://172.16.4.81:5000/boc_poc";
            Class.forName("com.mysql.jdbc.Driver");
            conn= DriverManager.getConnection(url,"root","sa123456");
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException ce) {
            ce.printStackTrace();
        }
    }

    //2.向数据库中添加一条记录
    public void Insert() {
        try {
            String sql="insert into test13(id, data) values (?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            File f = new File("D:\\WeChat\\WeChat Files\\All Users\\0b646dc3552d3ea4a5b3c67bf0aeadac.jpg");


            FileInputStream input= new FileInputStream(f);
            ps.setInt(1,1);
            ps.setBinaryStream(2, input, (int)f.length());
            ps.executeUpdate();
            System.out.println("插入成功");
            ps.close();
            input.close();
        }
        catch(SQLException e) {
            System.out.println("SQL异常");
            e.printStackTrace();
        }
        catch(IOException ie) {
            System.out.println("IO异常");
            ie.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("异常");
            e.printStackTrace();
        }
    }

    //3.从数据库中读取图片数据
    public void Read() {
        try {
            String sql="select data from test13 where id=?";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ResultSet rs=ps.executeQuery();
            byte [] b=new byte[10240 * 10];

            while(rs.next()) {
                //获取photo字段的图片数据
                InputStream in=rs.getBinaryStream("data");
                //将数据存储在字节数组b中
                in.read(b);
                //从数据库获取图片保存的位置
                File f = new File("D:/2.jpg ");
                FileOutputStream out = new FileOutputStream(f);
                out.write(b, 0, b.length);
                out.close();
                System.out.println("成功获取图片");
            }

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        catch(IOException ie) {
            ie.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PictureReadAndRead  picture = new PictureReadAndRead();
        picture.Insert();
        picture.Read();
    }
}
