/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import com.sun.javadoc.ClassDoc;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author os
 */
public class Resource /*implements Comparable<Resource>*/ {
    private String paths[];
    
    private String entityType;
    
    private ClassDoc classDoc;
    
    private final Set<Method> methods = new TreeSet<Method>(new Comparator<Method>() {

        @Override
        public int compare(Method o1, Method o2) {
            try {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
            catch (NullPointerException why) {
                return 1;
            }
        }
    });

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
    
}
