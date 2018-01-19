package fr.inria.stamp.inspector;


import org.junit.Test;
import org.objectweb.asm.ClassReader;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static fr.inria.stamp.inspector.MethodClassification.*;
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

    @Test
    public void testEmptyMethod() throws IOException {
        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/WithEmptyMethod");
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for(MethodEntry method: visitor.getMethods()) {
            if(method.getName().equals("empty")) {
                assertThat(method.getClassifications(), hasItems(ACCESSIBLE, EMPTY));
                return;
            }
        }

        fail("Empty method not found");
    }

    //TODO: If this pattern is used more times, then refactor.
    @Test
    public void testHashCode() throws IOException {
        ClassReader reader = new ClassReader("fr/inria/stamp/inspector/test/input/WithHashCode");
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);

        for(MethodEntry method: visitor.getMethods()) {
            if(method.getName().equals("hashCode")) {
                assertThat(method.getClassifications(), hasItems(ACCESSIBLE, HASH_CODE));
                return;
            }
        }

        fail("hashCode method not found");
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

        for (MethodEntry method :
                visitor.getMethods()) {
            assertThat(method.getClassifications(), hasSize(1));
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

}