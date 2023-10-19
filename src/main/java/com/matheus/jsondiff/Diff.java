
package com.matheus.jsondiff;


public class Diff {
    
    private String op;
    private String path;
    private String value;
    private String name;
    private String type;
    
    public Diff(){}
    
    public Diff(String op, String path, String value, String name, String type){
        this.op = op;
        this.path = path;
        this.value = value;
        this.name = name;
        this.type = type;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
