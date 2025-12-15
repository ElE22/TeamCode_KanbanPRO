package com.mycompany.teamcode_kanbanpro.client;

import java.io.Serializable;

/**
 *
 * @author Emanuel
 */
public class Response implements Serializable{
    private boolean success;
    private String msg;
    private boolean isBroadcast;
    private Object data;
    private String requestId;
    private String action;

    public Response(boolean success, String msg){
        this.success = success;
        this.msg = msg;
    }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return msg; }
    public void setMessage(String message) { this.msg = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    public boolean isBroadcast() { return isBroadcast; }
    public void setBroadcast(boolean isBroadcast) { this.isBroadcast = isBroadcast; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
}
