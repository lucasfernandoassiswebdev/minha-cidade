package br.com.eddydata.minhacidade.util;

import javax.ws.rs.QueryParam;

public class FilterBean {
    private @QueryParam("offset") int offset;
    private @QueryParam("limit") int limit;
    private @QueryParam("name") String name;
     
    public int getOffset() {
        return offset;
    }
 
    public void setOffset(int offset) {
        this.offset = offset;
    }
 
    public int getLimit() {
        return limit;
    }
 
    public void setLimit(int limit) {
        this.limit = limit;
    }
     
    public String getName() {
        return name;
    }
     
    public void setName(String name) {
        this.name = name;
    }
}
