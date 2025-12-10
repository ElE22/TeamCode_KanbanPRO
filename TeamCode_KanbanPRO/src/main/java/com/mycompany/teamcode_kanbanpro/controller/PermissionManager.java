/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;

/**
 *
 * @author Emanuel
 */
public class PermissionManager {
    private String userRole;
    
    public PermissionManager(String userRole) {
        this.userRole = userRole;
    }
    
    public boolean isScrumOrProduct() {
        return userRole.equalsIgnoreCase("Scrum Master") || 
               userRole.equalsIgnoreCase("Product Owner");
    }
//    
    
    public boolean canEditTask() {
        //validar esto, ya que no se si todos pueden o hay que ver el scope
        return true;
    }
    
    public boolean canDeleteProject() {
        return userRole.equalsIgnoreCase("Product Owner");
    }
}
