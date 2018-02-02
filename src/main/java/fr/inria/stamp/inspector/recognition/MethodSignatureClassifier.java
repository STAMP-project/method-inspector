package fr.inria.stamp.inspector.recognition;

import fr.inria.stamp.inspector.MethodClassification;
import fr.inria.stamp.inspector.Utils;
import org.objectweb.asm.Opcodes;


import static fr.inria.stamp.inspector.MethodClassification.*;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class MethodSignatureClassifier {

    private Map<MethodClassification, MethodSignatureRecognizer> recognizers;

    public MethodSignatureClassifier() {
        recognizers = new HashMap<>();

        set(ABSTRACT, new MethodAttributeRecognizer(Opcodes.ACC_ABSTRACT));
        set(SYNTHETIC, new MethodAttributeRecognizer(Opcodes.ACC_SYNTHETIC));
        set(DEPRECATED, new MethodAttributeRecognizer(Opcodes.ACC_DEPRECATED));
        set(ACCESSIBLE, new MethodAttributeRecognizer(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED));

        set(HASH_CODE, new MethodNameDescRecognizer("hashCode", "()I"));
        set(TO_STRING, new MethodNameDescRecognizer("toString", "()Ljava/lang/String;"));
        set(STATIC_INITIALIZER, new MethodNameDescRecognizer("<clinit>", "()V"));

        set(CONSTRUCTOR, (name, desc, access) -> name.equals("<init>"));

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
