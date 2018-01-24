package fr.inria.stamp.inspector.recognition;

import fr.inria.stamp.inspector.MethodClassification;

import java.util.*;

import static fr.inria.stamp.inspector.MethodClassification.*;
import static fr.inria.stamp.inspector.recognition.MethodRecognizer.*;


public class MethodClassifier {

    class ClassificationRecognizerPair {

        public MethodRecognizer recognizer;
        public MethodClassification classification;

        public ClassificationRecognizerPair(MethodClassification classification, MethodRecognizer recognizer) {
            this.classification = classification;
            this.recognizer = recognizer;
        }

        //Talking about delegation (^_^)

        public void advance(int opcode) {
            recognizer.advance(opcode);
        }

        public void stop(){ recognizer.stop(); }

        public boolean isInFinalState() {
            return recognizer.isInFinalState();
        }
    }

    private List<ClassificationRecognizerPair> pairs;

    public MethodClassifier() {
        //TODO: Make it extensible later

        pairs = new ArrayList<>(6);

        add(RETURNS_CONSTANT, returnsConstant());
        add(SIMPLE_GETTER, simpleGetter());
        add(SIMPLE_SETTER, simpleSetter());
        add(EMPTY, emptyMethod());

        add(DELEGATION, instanceDelegation());
        add(DELEGATION, staticDelegation());
    }

    private void add(MethodClassification classification, MethodRecognizer recognizer) {
        pairs.add(new ClassificationRecognizerPair(classification, recognizer));

    }

    public void advance(int opcode) {
        pairs.forEach(p -> p.advance(opcode));
    }

    public void stopRecognition() {
        pairs.forEach(ClassificationRecognizerPair::stop);
    }

    public EnumSet<MethodClassification> getClassifications() {
        EnumSet<MethodClassification> result = EnumSet.noneOf(MethodClassification.class);

        pairs.forEach( p -> {
            if(p.isInFinalState())
                result.add(p.classification);
        });

        return result;
    }

}
