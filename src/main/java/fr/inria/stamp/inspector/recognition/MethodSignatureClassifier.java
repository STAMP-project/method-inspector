package fr.inria.stamp.inspector.recognition;

import fr.inria.stamp.inspector.MethodClassification;
import fr.inria.stamp.inspector.Utils;

import static fr.inria.stamp.inspector.MethodClassification.*;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class MethodSignatureClassifier {

    private Map<MethodClassification, MethodSignatureRecognizer> recognizers;

    public MethodSignatureClassifier() {
        recognizers = new HashMap<>();

        set(ACCESSIBLE, (name, desc, access) -> Utils.isAccessibleToPackage(access));

        set(DEPRECATED, (name, desc, access) -> Utils.isDeprecated(access));

        set(HASH_CODE,
                (name, desc, access) -> name.equals("hashCode") && desc.equals("()I"));

        set(TO_STRING,
                (name, desc, access) -> name.equals("toString") && desc.equals("()Ljava/lang/String;"));

    }

    public void set(MethodClassification classification, MethodSignatureRecognizer recognizer) {
        recognizers.put(classification, recognizer);
    }

    public EnumSet<MethodClassification> classify(String name, String desc, int access) {

        EnumSet<MethodClassification> result = EnumSet.noneOf(MethodClassification.class);


        for(Map.Entry<MethodClassification, MethodSignatureRecognizer> supported : recognizers.entrySet()) {
            if(supported.getValue().matches(name, desc, access))
                result.add(supported.getKey());
        }

        return result;
    }

}
