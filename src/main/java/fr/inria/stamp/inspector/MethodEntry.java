package fr.inria.stamp.inspector;

import com.google.gson.annotations.SerializedName;

import java.util.EnumSet;

public class MethodEntry {

    @SerializedName("package")
    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @SerializedName("class")
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullClassName() { return packageName + className; }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private EnumSet<MethodClassification> classifications = EnumSet.noneOf(MethodClassification.class);

    public EnumSet<MethodClassification> getClassifications() {
        return classifications;
    }

    public void setClassifications(EnumSet<MethodClassification> classifications) {
        this.classifications = classifications;
    }

    public void addClassification(MethodClassification cls) {
        classifications.add(cls);
    }

    public void removeClassification(MethodClassification cls) {
        classifications.remove(cls);
    }

    public String getFullName() {
        return packageName +  className + "." + name + description;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof  MethodEntry))
            return false;
        MethodEntry other = (MethodEntry) obj;
        return getFullName().equals(other.getFullName()) &&
                classifications.size() == other.classifications.size() &&
                classifications.containsAll(other.classifications);
    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 37 * result + getFullName().hashCode();
        result = 37 * result + getClassifications().hashCode();
        return result;
    }
}

