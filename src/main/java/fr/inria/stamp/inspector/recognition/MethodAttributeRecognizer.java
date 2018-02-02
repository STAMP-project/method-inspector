package fr.inria.stamp.inspector.recognition;

public class MethodAttributeRecognizer implements MethodSignatureRecognizer {

    public MethodAttributeRecognizer(int attributes) {
        this.attributes = attributes;
    }

    private int attributes;

    public int getAttributes() { return attributes; }

    @Override
    public boolean matches(String name, String desc, int access) {
        return (access & attributes) != 0;
    }

}
