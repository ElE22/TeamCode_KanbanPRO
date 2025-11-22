/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.client;

import java.io.Serializable;

/**
 *
 * @author Emanuel
 */
public class Response implements Serializable{
    private boolean success;
    private String msg;
    private Object data;

    public Response(boolean success, String msg){
        this.success = success;
        this.msg = msg;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return msg; }
    public void setMessage(String message) { this.msg = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
}
