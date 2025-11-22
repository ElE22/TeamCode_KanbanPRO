/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Emanuel
 */
public class BCryptUtil {
    public static String hashPwd(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }
    
    public static boolean checkPwd(String plain, String hashed) {
        if (plain == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(plain, hashed);
        } catch (Exception ex) {
            return false;
        }
    }
}
