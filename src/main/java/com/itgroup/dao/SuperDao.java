package com.itgroup.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SuperDao {
    public SuperDao(){
        String driver = "oracle.jdbc.driver.OracleDriver";

        try {
            Class.forName(driver);


        }catch (ClassNotFoundException e){
            System.out.println("해당 드라이버가 존재하지 않습니다.");
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        Connection conn = null;

        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String id = "mycar";
        String password = "hello1234";

        try{
            conn = DriverManager.getConnection(url, id, password);

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    return conn;
    }


}
