package org.example;

import java.util.Map;
import java.util.Set;

public class CodeElement {
    private String id;
    private String type;                 // class, method, field
    private String name;
    private String packageName;
    private String declaringClass;       // for methods / fields
    private Set<String> annotations;     // fully qualified or simple names
    private Map<String, String> properties;

    public CodeElement(String id, String type, String name, String packageName,
                       String declaringClass, Set<String> annotations,
                       Map<String, String> properties) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.packageName = packageName;
        this.declaringClass = declaringClass;
        this.annotations = annotations;
        this.properties = properties;
    }
    public void addProperty(String key, String value){

    }
    public void addAnnotation(String annotation){
        annotations.add(annotation);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<String> annotations) {
        this.annotations = annotations;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
