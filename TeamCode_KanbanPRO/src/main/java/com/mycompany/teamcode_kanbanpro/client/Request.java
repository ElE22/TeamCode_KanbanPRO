package com.mycompany.teamcode_kanbanpro.client;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Emanuel
 */
public class Request implements Serializable {
    private String action;
    private Map<String, Object> parameters;

     public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Object> getPayload() { return parameters; }
    public void setPayload(Map<String, Object> payload) { this.parameters = payload; }
}
