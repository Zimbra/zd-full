/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

public abstract class Response {

    protected int retCode;
    private String respString;

    public Response(int retCode, String resp) {
        this.retCode = retCode;
        this.respString = resp;
    }

    protected String getResp() {
        return this.respString;
    }
}
