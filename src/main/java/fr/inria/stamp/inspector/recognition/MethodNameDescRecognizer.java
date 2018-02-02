package fr.inria.stamp.inspector.recognition;

public class MethodNameDescRecognizer implements MethodSignatureRecognizer{

    public MethodNameDescRecognizer(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    private String name, desc;

    public String getName() { return name; }

    public String getDescription() { return desc; }

    public boolean matches(String name, String desc, int access) {
        return name.equals(this.name) && desc.equals(this.desc);
    }
}
