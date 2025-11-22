/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.util;
import java.io.IOException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.InputStream;

/**
 *
 * @author Emanuel
 */
public class DBUtil {

    private static  String DB_URL;
    private static  String DB_USER;
    private static  String DB_PASSWORD ;
    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("kanbanPro.properties")) {
           Properties props = new Properties();
            props.load(in);
            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();    
            throw new ExceptionInInitializerError("No se pudo cargar la configuración de la base de datos");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("No se pudo cargar la configuración de la base de datos");
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
}
