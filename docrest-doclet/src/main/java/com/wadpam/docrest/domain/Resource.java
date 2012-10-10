/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import com.sun.javadoc.ClassDoc;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author os
 */
public class Resource {
    private String paths[];
    
    private String entityType;
    
    private String simpleType;
    
    private ClassDoc classDoc;
    
    private Map<String, Collection<Method>> operationsMap = new TreeMap<String, Collection<Method>>();
    
    private final Set<Method> methods = new TreeSet<Method>();

    public ClassDoc getClassDoc() {
        return classDoc;
    }

    public void setClassDoc(ClassDoc classDoc) {
        this.classDoc = classDoc;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String paths[]) {
        this.paths = paths;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public void setSimpleType(String simpleType) {
        this.simpleType = simpleType;
    }

    public Map<String, Collection<Method>> getOperationsMap() {
        return operationsMap;
    }

    public void setOperationsMap(Map<String, Collection<Method>> operationsMap) {
        this.operationsMap = operationsMap;
    }

}
