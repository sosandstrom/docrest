/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.wadpam.docrest.domain.Method;
import com.wadpam.docrest.domain.Param;
import com.wadpam.docrest.domain.Resource;
import com.wadpam.docrest.domain.RestReturn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 *
 * @author os
 */
public class DocRestDoclet {

    static final Logger LOG = Logger.getLogger(DocRestDoclet.class.getName());
    static final String INDENT = "&nbsp;&nbsp;&nbsp;";

    protected final VelocityContext vc = new VelocityContext();
    private static RootDoc rootDoc;
    private static final Map<String, ClassDoc> classDocs = new HashMap<String, ClassDoc>();
    
    /** for JSON Object classes */
    private static final Map<String,Class> jsonClassMap = new TreeMap<String,Class>(); 
    private static final Map<String,ClassDoc> jsonDocMap = new TreeMap<String,ClassDoc>();
    

/*
https://warburtons-test.appspot.com/oauth/wbt/authorize?client_id=localhost.generic-app&redirect_uri=/ugly/%23providerId=gekko&response_type=token
*/
    private String basePath;
    private String baseUrl;
    private String clientId;

    public DocRestDoclet() throws Exception {
        final Properties p = new Properties();
        p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        p.setProperty("resource.loader", "class");
        Velocity.init(p);
    }

    protected void addAttribute(String name, Object value) {
        vc.put(name, value);
    }

    protected void merge(String templateFilename, File folder, String javaFilename) throws FileNotFoundException, IOException {
        final File javaFile = (null != folder) ? new File(folder, javaFilename) : new File(javaFilename);

        // create destination folder?
        File destinationFolder = javaFile.getParentFile();
        if (null != destinationFolder && false == destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
        
        // copy css file
        final InputStream cssIn = getClass().getResourceAsStream("/api.css");
        final File cssFile = new File(destinationFolder, "api.css");
        final FileOutputStream cssOut = new FileOutputStream(cssFile);
        byte b[] = new byte[1024];
        int count;
        while (0 < (count = cssIn.read(b))) {
            cssOut.write(b, 0, count);
        }
        cssOut.close();
        cssIn.close();

        final PrintWriter writer = new PrintWriter(javaFile);
        Template template;
        try {
            template = Velocity.getTemplate(templateFilename);
            template.merge(vc, writer);
        } catch (ResourceNotFoundException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseErrorException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
    }

    protected void merge(String templateFilename, Resource res) throws FileNotFoundException, IOException {
        final File javaFile = new File(res.getSimpleType() + ".json");

        final PrintWriter writer = new PrintWriter(javaFile);
        Template template;
        try {
            template = Velocity.getTemplate(templateFilename);
            vc.put("resource", res);
            template.merge(vc, writer);
        } catch (ResourceNotFoundException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseErrorException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
    }

    protected static String[] getValue(AnnotationDesc annotationDesc, String name) {
        String[] returnValue = new String[1];
        returnValue[0] = "";
        AnnotationTypeElementDoc element;
        AnnotationValue value;
        for (ElementValuePair evp : annotationDesc.elementValues()) {
            element = evp.element();
            value = evp.value();
            if (name.equals(element.name())) {
                Object[] values = (Object[]) value.value();
                returnValue = new String[values.length];
                int i = 0;
                for (Object v : values) {
                    StringBuffer sb = new StringBuffer(v.toString());
                  // trim leading and tailing quotes
                    if (0 == sb.indexOf("\"")) {
                        sb.deleteCharAt(0);
                    }
                    if (sb.length() - 1 == sb.lastIndexOf("\"")) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    returnValue[i++] = sb.toString();
                }
            }
        }
        return returnValue;
    }

    protected static Object getValueAsObject(AnnotationDesc annotationDesc, String name) {
        AnnotationTypeElementDoc element;
        AnnotationValue value;
        for (ElementValuePair evp : annotationDesc.elementValues()) {
            element = evp.element();
            value = evp.value();
            if (name.equals(element.name())) {
                return value.value();
            }
        }
        return null;
    }
    
    protected Collection<Resource> traverse(RootDoc root) {
        this.rootDoc = root;
        vc.put("basePath", getBasePath());
        vc.put("baseUrl", getBaseUrl());
        vc.put("clientId", getClientId());
        
        for (ClassDoc classDoc : root.classes()) {
            classDocs.put(classDoc.qualifiedName(), classDoc);
        }
        final Collection<Resource> resources = new TreeSet<Resource>(new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
                //return o1.getEntityType().compareToIgnoreCase(o2.getEntityType());
//                return o1.getPaths()[0].compareToIgnoreCase(o2.getPaths()[0]);
            }
        });

        // pre-filter @Controllers
        AnnotationTypeDoc type;
        for (ClassDoc classDoc : root.classes()) {
            Resource resource = new Resource();
            
            String paths[] = {""};
            for (AnnotationDesc classAnnotation : classDoc.annotations()) {
                type = classAnnotation.annotationType();

                if ("org.springframework.stereotype.Controller".equals(type.qualifiedName())) {
                    resource.setClassDoc(classDoc);
                    System.out.println("========= @Controller " + classDoc.qualifiedName() + " ========");
                } else if ("org.springframework.web.bind.annotation.RequestMapping".equals(type.qualifiedName())) {
                    paths = getValue(classAnnotation, "value");
                }
                else if (RestReturn.class.getName().equals(type.qualifiedName())) {
                    resource.setIncludeApi(true);
                    resource.setName(classDoc.qualifiedName());
                    for (ElementValuePair element : classAnnotation.elementValues()) {
                        if ("value".equals(element.element().name())) {
                            final String className = element.value().value().toString();
                            resource.setEntityType(className);
                            resource.setSimpleType(className);
                            int beginIndex = className.lastIndexOf('.');
                            if (-1 < beginIndex) {
                                resource.setSimpleType(className.substring(beginIndex+1));
                            }
                        }
                    }
                    System.out.println("========= @RestReturn " + classDoc.qualifiedName() + " ========");
                    System.out.println(String.format("             entity = %s", resource.getEntityType()));
                }
            }

            if (null != resource.getClassDoc()) {
                resource.setPaths(paths);

                // traverse ancestors
                boolean include = traverseAncestors(classDoc, resource);
                //if (include && null != resource.getEntityType()) {
                if (include && resource.isIncludeApi()) {
                    resources.add(resource);
                }
            }

        }
        
        vc.put("helper", this);
        vc.put("resources", resources);
        vc.put("encoder", new StringEscapeUtils());
        vc.put("jsonMap", jsonClassMap);
        vc.put("jsonDoc", jsonDocMap);
        vc.put("readme", getReadme());
        String path;
        Collection<Method> methods;
        for (Resource r : resources) {
            for (String rp : r.getPaths()) {
                for (Method m : r.getMethods()) {
                    for (String mp : m.getPaths()) {
                        path = String.format("%s/%s", rp, mp);
                        methods = r.getOperationsMap().get(path);
                        if (null == methods) {
                            methods = new TreeSet<Method>();
                            r.getOperationsMap().put(path, methods);
                        }
                        methods.add(m);
                    }
                }
            }
        }
        
        return resources;
    }

    protected boolean traverseAncestors(ClassDoc classDoc, Resource resource) {
        boolean include = traverseMethods(classDoc, resource);

        // now do the same for parent:
        if (false == Object.class.getName().equals(classDoc.superclassType().qualifiedTypeName())) {
            include |= traverseAncestors(classDoc.superclass(), resource);
        }
        
        return include;
    }

    protected boolean traverseMethods(ClassDoc classDoc, Resource resource) {
        AnnotationTypeDoc type;
        boolean include = false;
        // find mapped methods
        for (MethodDoc methodDoc : classDoc.methods()) {
            boolean includeMethod = false;
            Method method = new Method(resource);
            for (AnnotationDesc methodAnnotation : methodDoc.annotations()) {
                type = methodAnnotation.annotationType();
                if ("org.springframework.web.bind.annotation.RequestMapping".equals(type.qualifiedName())) {
                    LOG.info("---- @RequestMapping " + classDoc.simpleTypeName() + "." + methodDoc.name() + "() ----");
                    method.setClassDoc(classDoc);
                    method.setMethodDoc(methodDoc);
                    method.setName(methodDoc.name());
                    method.setPaths(getValue(methodAnnotation, "value"));
                    String m[] = getValue(methodAnnotation, "method");
                    StringBuilder methodType = new StringBuilder();
                    if (m.length > 0 && m.length < 7) {
                        for (int i = 0; i < m.length; i++) {
                            if (m[i].startsWith("org.springframework.web.bind.annotation.RequestMethod.")) {
                                methodType.append(m[i].substring("org.springframework.web.bind.annotation.RequestMethod.".length())+ ", ");
                            }
                        }
                        
                        method.setMethod(methodType.toString().substring(0, methodType.length() - 2));
                        
                    }
                    else {
                        method.setMethod("*");
                    }


                    traverseParameters(methodDoc, method);

                }
                
                if (RestReturn.class.getName().equals(type.qualifiedName())) {
                    method.setSupportsClassParams(Boolean.TRUE.equals(getValueAsObject(methodAnnotation, "supportsClassParams")));
                    LOG.info("       supportsClassParams=" + method.isSupportsClassParams());
                    
                    include = true;
                    includeMethod = true;
                    LOG.info("---- @RestReturn " + methodDoc.name() + "() of " + classDoc.simpleTypeName() + " ----");
                    method.setRestReturn(methodAnnotation);
                    for (ElementValuePair element : methodAnnotation.elementValues()) {
                        if ("value".equals(element.element().name())) {
                            method.setReturnType(element.value().value().toString());
                        }
                        else if ("entity".equals(element.element().name())) {
                            method.setEntityType(element.value().value().toString());
                        }
                    }
                }
            }
            if (includeMethod) {
                resource.getMethods().add(method);
            }
            
            if (null != method.getReturnType()) {
                LOG.info("  ReturnType=" + method.getReturnType());
                method.setJson(getJson(method.getReturnType(), method.getEntityType()));
               // method.setJsonEntity(getJson(method.getReturnType(), null));
            }
        }
        return include;
    }

    protected static String getReturnType(ClassDoc classDoc, MethodDoc methodDoc) {
        String methodName = methodDoc.name();
        try {
            Class c = Class.forName(classDoc.qualifiedName());

            // build parameter class list
            List<Class> paramClasses = new ArrayList<Class>();
            int i = 0;
            for (Parameter p : methodDoc.parameters()) {
                String className = p.type().qualifiedTypeName();
                try {
                    paramClasses.add(Class.forName(className));
                }
                catch (ClassNotFoundException skipSimple) {
                    
                }
            }

            java.lang.reflect.Method rm = c.getMethod(methodName, paramClasses.toArray(new Class[0]));
            LOG.info("  -> genericReturnType " + rm.getGenericReturnType());
            if (rm.getGenericReturnType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) rm.getGenericReturnType();
                LOG.info("     parameterizedType " + parameterizedType.toString());
                java.lang.reflect.Type argTypes[] = parameterizedType.getActualTypeArguments();
                if (1 == argTypes.length) {
                    String returnValue = argTypes[0].toString();
                    if (returnValue.startsWith("class ")) {
                        return returnValue.substring("class ".length());
                    }
                    return returnValue;
                }
            }
            return rm.getGenericReturnType().toString();
        } catch (ClassNotFoundException e) {
            LOG.warning("getReturnType ClassNotFound " + classDoc.qualifiedName() + " " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            LOG.warning("getReturnType NoSuchMethod " + methodName + " " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    
    protected static void appendJsonMembers(StringBuffer sb, String entityName, String indent) throws ClassNotFoundException {
        Class c = Class.forName(entityName);
        LOG.info("  Producing JSON/HTML for " + c);
        
        if (null != c.getSuperclass() && !Object.class.equals(c.getSuperclass())) {
            appendJsonMembers(sb, c.getSuperclass().getName(), indent);
        }
        
        jsonClassMap.put(entityName, c);
        
        Field fields[] = c.getDeclaredFields();
        Arrays.sort(fields, new Comparator<Field>() {

            @Override
            public int compare(Field o1, Field o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
            
        });
        
        for (int i = 0; i < fields.length; i++) {
            sb.append("<div>");
            sb.append(indent);
            sb.append(INDENT);
            sb.append("<b>\"");
            sb.append(fields[i].getName());
            sb.append("\"</b>:");
            String paramClass = fields[i].getType().getName();
            ClassDoc p = classDocs.get(paramClass);
            if (null != p) {
                sb.append(getJson(paramClass, paramClass, indent + INDENT));
            }
            else {
                sb.append(fields[i].getType().getSimpleName());
            }
            if (i < fields.length-1) {
                sb.append(',');
            }
            sb.append("</div>");
        }
    }

    protected static String getJson(String className, String entityName) {
        return getJson(className, entityName, "");
    }
    
    protected static String getJson(String className, String entityName, String indent) {
        final StringBuffer sb = new StringBuffer();
        
        if (className.startsWith(List.class.getName()) || className.endsWith("]")) {
            className = List.class.getName();
            sb.append('[');
        }
        else if (null == entityName) {
            entityName = className;
        }
        
        if (null != entityName) {
            ClassDoc classDoc = rootDoc.classNamed(entityName);
            LOG.info("JSON lookup " + className + "<" + entityName + ">  gives " + classDoc);
            if (null == classDoc) {
                return entityName;
            }
            jsonDocMap.put(entityName, classDoc);

            if (String.class.getName().equals(entityName)) {
                sb.append(String.class.getSimpleName());
            }
            else if (Long.class.getName().equals(entityName)) {
                sb.append(Long.class.getSimpleName());
            }
            else if (Integer.class.getName().equals(entityName)) {
                sb.append(Integer.class.getSimpleName());
            }
            else {

                sb.append("{");
                try {
                    appendJsonMembers(sb, entityName, indent);
                }
                catch (ClassNotFoundException e) {
                    
                    for (MethodDoc method : getInheritedMethods(classDoc)) {
                        if (isGetter(method.name())) {
                        sb.append("<div>");
                        sb.append(indent);
                        sb.append(INDENT);
                        sb.append("<b>\"");
                        sb.append(getMemberName(method.name()));
                        sb.append("\"</b>&nbsp;:&nbsp;");
                        String paramClass = method.returnType().qualifiedTypeName();
                        ClassDoc p = classDocs.get(paramClass);
                        LOG.info(String.format("    -- for return attribute \"%s\" : %s the classDoc is " + p, getMemberName(method.name()), paramClass));
//                        if (null != p) {
                            sb.append(getJson(paramClass, paramClass, indent + INDENT));
//                        }
//                        else {
//                            sb.append(paramClass);
//                        }
                        sb.append(',');
                        sb.append("</div>");
                        }
                    }
                }
                sb.append(indent);
                sb.append("}");
            }
        }
        
        if (List.class.getName().equals(className)) {
            sb.append(",<div>");
            sb.append(INDENT);
            sb.append("...</div>");
            sb.append(INDENT);
            sb.append("]");
        }

        return sb.toString();
    }
    
    public static boolean isGetter(String methodName) {
        return null != methodName && (methodName.startsWith("get") || methodName.startsWith("is"));
    }
    
    protected static void addMethodsRecursive(Set<MethodDoc> methods, ClassDoc classDoc) {
        LOG.info("+++ addMethods for " + classDoc.name());
        if (!Object.class.getSimpleName().equals(classDoc.name())) {
            
            if (null != classDoc.superclass()) {
                addMethodsRecursive(methods, classDoc.superclass());
            }
            for (MethodDoc m : classDoc.methods()) {
                LOG.info("   ??? addMethod " + m.name());
                if (isGetter(m.name())) {
                    methods.add(m);
                }
            }
        }
    }
    
    public String renderType(String memberType) {
        String returnValue = memberType;
        for (String t : jsonDocMap.keySet()) {
            returnValue = returnValue.replace(t, 
                    String.format("<a href=\"api.html#%s\" class=\"link\">%s</a>", t, t));
        }
        return returnValue;
    }
    
    public String renderAnchor(String canonicalName) {
        if (null == canonicalName) {
            return "";
        }
        return canonicalName.replaceAll("[\\.\\/\\{\\}\\(\\)]", "").toLowerCase();
    }

    protected static Iterable<MethodDoc> getInheritedMethods(ClassDoc classDoc) {
        final Set<MethodDoc> methods = new TreeSet<MethodDoc>(new Comparator<MethodDoc>() {

            @Override
            public int compare(MethodDoc o1, MethodDoc o2) {
                return getMemberName(o1.name()).compareToIgnoreCase(getMemberName(o2.name()));
            }
        });
        
        addMethodsRecursive(methods, classDoc);
        
        return methods;
    }
    
    
    public static String getMemberName(String methodName) {
        int beginIndex = methodName.startsWith("get") ? 3 : 2;
        StringBuffer sb = new StringBuffer();
        if (beginIndex+1 < methodName.length()) {
            sb.append(methodName.substring(beginIndex, beginIndex+1).toLowerCase());
            sb.append(methodName.substring(beginIndex+1));
        }
        return sb.toString();
    }

    protected static String getComment(MethodDoc methodDoc, String parameterName) {
        for (ParamTag p : methodDoc.paramTags()) {
            if (parameterName.equals(p.parameterName())) {
                return p.parameterComment();
            }
        }
        return "";
    }

    protected void traverseParameters(MethodDoc methodDoc, Method method) {
        AnnotationTypeDoc type;
        for (Parameter p : methodDoc.parameters()) {
            LOG.info("        parameter " + p.typeName() + " " + p.name());
            for (AnnotationDesc paramAnnotation : p.annotations()) {
                type = paramAnnotation.annotationType();
                Param param = new Param();
                param.setName(p.name());
                param.setComment(getComment(methodDoc, p.name()));
                // set as default
                param.setRequired(true);
                param.setDefaultValue("");
                
                if ("org.springframework.web.bind.annotation.PathVariable".equals(type.qualifiedName())) {
                    param.setType(p.typeName());
                    method.getPathVariables().add(param);
                } else if ("org.springframework.web.bind.annotation.RequestBody".equals(type.qualifiedName())) {
                    param.setType(p.typeName());
                    method.setBody(param);
                } else if ("org.springframework.web.bind.annotation.RequestParam".equals(type.qualifiedName())) {
                    
                    for (ElementValuePair element : paramAnnotation.elementValues()) {
                        /*if ("org.springframework.web.bind.annotation.RequestParam.value".equals(element.element()
                                .qualifiedName())) {*/
                        if ("value".equals(element.element().name())) {
                            param.setName(element.value().value().toString());
                         
                        }
                        else if ("org.springframework.web.bind.annotation.RequestParam.required".equals(element.element()
                                .qualifiedName())) {
                            param.setRequired(Boolean.valueOf(element.value().value().toString()));
                        }
                        else if ("org.springframework.web.bind.annotation.RequestParam.defaultValue".equals(element
                                .element().qualifiedName())) {
                            param.setDefaultValue(element.value().value().toString());
                        }
                    }
                    
                    param.setType(p.typeName());
                    method.getParameters().add(param);
                } else if ("org.springframework.web.bind.annotation.ModelAttribute".equals(type.qualifiedName())) {
                    param.setType(getJson(p.type().qualifiedTypeName(), null));
                    method.getModelAttributes().add(param);
                }
                LOG.info("                    @" + type.name() + " " + param.getType() + " " + param.getName() + " /** " + param.getComment() + " */");
            }
        }

    }

    protected static void setRootDoc(RootDoc rootDoc) {
        DocRestDoclet.rootDoc = rootDoc;
    }
    
    public static int optionLength(String option) {
        if ("-basePath".equals(option)) {
            return 2;
        }
        if ("-baseUrl".equals(option)) {
            return 2;
        }
        if ("-clientId".equals(option)) {
            return 2;
        }
        return 0;
    }



	
	 public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public static boolean start(RootDoc root) {
        try {
            DocRestDoclet doclet = new DocRestDoclet();
            for (String[] options : root.options()) {
                if ("-basePath".equals(options[0])) {
                    doclet.setBasePath(options[1]);
                }
                if ("-baseUrl".equals(options[0])) {
                    doclet.setBaseUrl(options[1]);
                }
                if ("-clientId".equals(options[0])) {
                    doclet.setClientId(options[1]);
                }
            }
            doclet.addAttribute("root", root);
            Collection<Resource> resources = doclet.traverse(root);

            doclet.merge("api_html.vm", null, "api.html");
            doclet.merge("api_md.vm", null, "api.md");
            doclet.merge("api_resources.vm", null, "resources.json");

            doclet.merge("backendAPI.vm", null, "backendAPI.js");
            doclet.merge("project.backend.vm", null, "project.backend.js");
            doclet.merge("backend.index.vm", null, "index.html");

            int count = 0;

            for (Resource r : resources) {
                Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, "yo");
                r.setCount(count);
                doclet.merge("api_resource.vm", r);
                count++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
    
    protected static String getReadme() {

        String fileName = "";
        StringBuilder contents = new StringBuilder();
        String line;
        BufferedReader bReader = null;

        // cross platforms
        String[] pwd = System.getProperty("user.dir").split("/target/");
        if (pwd.length > 0) {
            fileName = pwd[0] + "/readme-docrest.txt";
        }

        try {
            bReader = new BufferedReader(new FileReader(new File(fileName)));
            while ((line = bReader.readLine()) != null) {
                contents.append(line + "<br>");
            }
        }
        catch (FileNotFoundException e) {
            // e.printStackTrace();
            LOG.info("readme-docrest.txt file directly under project directory will be used as top guideline of REST API.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bReader != null)
                    bReader.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return contents.toString();
    }
}


