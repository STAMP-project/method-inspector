package fr.inria.stamp.inspector.recognition;

import fr.inria.stamp.inspector.MethodClassification;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static fr.inria.stamp.inspector.MethodClassification.*;
import static fr.inria.stamp.inspector.recognition.MethodRecognizer.*;


public class MethodClassifier {

    private Map<MethodClassification, MethodRecognizer> recognizers;

    public MethodClassifier() {
        //TODO: Make it extensible later

        recognizers = new HashMap<>();
        recognizers.put(RETURNS_CONSTANT, returnsConstant());
        recognizers.put(SIMPLE_GETTER, simpleGetter());
        recognizers.put(SIMPLE_SETTER, simpleSetter());
        recognizers.put(EMPTY, emptyMethod());
    }

    public void advance(int opcode) {
        recognizers.values().forEach(recg -> recg.advance(opcode));
    }

    public void stopRecognition() {
        recognizers.values().forEach( recg -> recg.stop());
    }

    public EnumSet<MethodClassification> getClassifications() {
        EnumSet<MethodClassification> result = EnumSet.noneOf(MethodClassification.class);

        recognizers.forEach( (cls, recg) -> {
            if(recg.isInFinalState())
                result.add(cls);
        });

        return result;
    }

}
