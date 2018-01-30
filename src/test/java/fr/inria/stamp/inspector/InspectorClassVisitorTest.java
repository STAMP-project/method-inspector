package fr.inria.stamp.inspector;


import org.junit.Test;
import org.objectweb.asm.ClassReader;
import java.io.IOException;
import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static fr.inria.stamp.inspector.MethodClassification.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InspectorClassVisitorTest {

    @Test
    public void testClassifier() throws IOException {

        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/WithGettersAndSetters");

        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        //All in the correct class
        for(MethodEntry method : visitor.getMethods()) {
            assertEquals("Unexpected class name " + method.getClassName() + " for method " + method.getName(),
                    reader.getClassName(), method.getFullClassName());

            assertThat(method.getClassifications(), hasItem(MethodClassification.ACCESSIBLE));
            if(method.getName().startsWith("set"))
                assertThat(method.getClassifications(), hasItem(SIMPLE_SETTER));
            if(method.getName().startsWith("get"))
                assertThat(method.getClassifications(), hasItem(SIMPLE_GETTER));
        }

    }

    public void find(String inClass, String target, MethodClassification... classifications) throws IOException {

        ClassReader reader = new ClassReader(inClass);
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for(MethodEntry method: visitor.getMethods()) {
            if(method.getName().equals(target)) {
                assertThat(method.getClassifications(), hasItems(classifications));
                return;
            }
        }

        fail(target + " method not found");


    }

    @Test
    public void testEmptyMethod() throws IOException {
        find("fr/inria/stamp/inspector/test/input/WithEmptyMethod", "empty", ACCESSIBLE, EMPTY);
    }

    @Test
    public void testHashCode() throws IOException {
        find("fr/inria/stamp/inspector/test/input/WithHashCode", "hashCode", ACCESSIBLE, HASH_CODE, DELEGATION);
    }

    @Test
    public void testToString() throws IOException {
        find("fr/inria/stamp/inspector/test/input/WithToString", "toString", ACCESSIBLE, TO_STRING, DELEGATION);
    }

    @Test
    public void testReturningConstants() throws IOException {

        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/ReturningConstants");
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for(MethodEntry method: visitor.getMethods()) {
            if(method.getName().equals("<init>")) continue; //Don't check constructor
            assertThat(method.getClassifications(), hasItems(ACCESSIBLE, RETURNS_CONSTANT));
        }
    }

    @Test
    public void testDeprecatedMethod() throws IOException {
        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/WithDeprecatedMethod");
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for(MethodEntry method: visitor.getMethods()) {
            if(!method.getName().equals("<init>")) {
                assertThat(method.getClassifications(), hasItem(DEPRECATED));
            }
        }
    }

    @Test
    public void testDeprecatedClass() throws IOException {
        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/DeprecatedClass");
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for(MethodEntry method: visitor.getMethods()) {
            assertThat(method.getClassifications(), hasItem(DEPRECATED));
        }
    }

    @Test
    public void testAccessibleOnly() throws IOException {
        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/AccessibleOnly");
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for (MethodEntry method : visitor.getMethods()) {
            assertThat(method.getClassifications(), hasItem(ACCESSIBLE));
        }
    }

    @Test
    public void testNonAccessible() throws IOException {

        String[] suffixes = { "", "$NestedPublic", "$NestedPrivate", "$NestedProtected" };

        for(String value: suffixes) {

            ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/NonAccessible" + value);
            InspectorClassVisitor visitor = new InspectorClassVisitor();
            reader.accept(visitor, 0);

            for (MethodEntry method : visitor.getMethods()) {
                assertThat(method.getClassifications(), not(hasItem(ACCESSIBLE)));
            }
        }

    }

    @Test
    public void testDelegation() throws IOException {

        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/WithDelegationMethods");
        InspectorClassVisitor visitor = new InspectorClassVisitor();

        reader.accept(visitor, 0);

        for (MethodEntry method :
                visitor.getMethods()) {
            EnumSet<MethodClassification> classifications = method.getClassifications();
            String name = method.getName();

            assertTrue( name + " was not classified correctly",
                    (name.startsWith("delegate") && classifications.contains(DELEGATION)) ||
                             (!name.startsWith("delegate") && !classifications.contains(DELEGATION)));

        }

    }

}