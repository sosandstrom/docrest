/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

/**
 *
 * @author os
 */
public class Param {
    private String name;
    private String type;
    private boolean required;
    private String  defaultValue;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    
}
