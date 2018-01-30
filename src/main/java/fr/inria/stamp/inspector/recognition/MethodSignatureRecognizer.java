package fr.inria.stamp.inspector.recognition;

public interface MethodSignatureRecognizer {

    boolean matches(String name, String desc, int access);

}
