package com.example;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public class SQLconnection {
    protected static String ip = "10.0.2.2";
    protected static String port = "3306";
    protected static String db = "newschema";
    protected static String un = "root";
    protected static String password = "Soidopro1";
    public Connection CONN(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String conURL = "jdbc:mysql://"+ip+ ":" +port+"/" +db;
            con = DriverManager.getConnection(conURL, un, password);
        } catch (Exception e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
        return con;
    }
}
