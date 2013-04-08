/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author os
 */
public class Method implements Comparable<Method> {
    
    private final Resource resource;
    
    private String paths[];
    
    private String method = "*";
    
    private String name;
    
    private ClassDoc classDoc;
    
    private MethodDoc methodDoc;
    
    private List<Param> pathVariables = new ArrayList<Param>();
    
    private List<Param> modelAttributes = new ArrayList<Param>();
    
    private List<Param> parameters = new ArrayList<Param>();
    
    private AnnotationDesc restReturn;
    
    private String returnType;
    
    private String entityType;
    
    private String json;
    
    private Param body = null;
    
    private boolean supportsClassParams = false;
    
    public Method(Resource resource) {
        this.resource = resource;
    }

    @Override
    public int compareTo(Method t) {
        return name.compareToIgnoreCase(t.name);
    }
    
    public ClassDoc getClassDoc() {
        return classDoc;
    }

    public void setClassDoc(ClassDoc classDoc) {
        this.classDoc = classDoc;
    }

    public MethodDoc getMethodDoc() {
        return methodDoc;
    }

    public void setMethodDoc(MethodDoc methodDoc) {
        this.methodDoc = methodDoc;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String paths[]) {
        this.paths = paths;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Param> getParameters() {
        return parameters;
    }

    public void setParameters(List<Param> parameters) {
        this.parameters = parameters;
    }

    public List<Param> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(List<Param> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public AnnotationDesc getRestReturn() {
        return restReturn;
    }

    public void setRestReturn(AnnotationDesc restReturn) {
        this.restReturn = restReturn;
    }

    public String getEntityType() {
        if ((null == entityType || Object.class.getName().equals(entityType)) && 
                !Object.class.getName().equals(resource.getEntityType())) {
            return resource.getEntityType();
        }
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getReturnType() {
        String returnValue = returnType;
        if (null != returnType && returnType.startsWith(List.class.getName())) {
            returnValue = String.format("%s<%s>", returnType, getEntityType());
        }
        return returnValue;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Resource getResource() {
        return resource;
    }

    public Param getBody() {
        return body;
    }

    public void setBody(Param body) {
        this.body = body;
    }

    public List<Param> getModelAttributes() {
        return modelAttributes;
    }

    public void setModelAttributes(List<Param> modelAttributes) {
        this.modelAttributes = modelAttributes;
    }

    public boolean isSupportsClassParams() {
        return supportsClassParams;
    }

    public void setSupportsClassParams(boolean supportsClassParams) {
        this.supportsClassParams = supportsClassParams;
    }
    
    
}
