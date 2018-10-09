package eu.stamp_project.inspector;

import eu.stamp_project.inspector.test.input.*;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.stamp_project.inspector.MethodClassification.*;
import static eu.stamp_project.inspector.test.MethodClassificationMatchers.notTaggedAs;
import static eu.stamp_project.inspector.test.MethodClassificationMatchers.taggedAs;
import static eu.stamp_project.inspector.test.Utils.all;
import static eu.stamp_project.inspector.test.Utils.getTreeFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;


@DisplayName("Set of methods in a class with a classification")
public class SetOfMethodsTest {

    private Stream<DynamicTest> testMethodsInClass(
            Class<?> clazz,
            Predicate<MethodTree> methodFilter,
            Matcher<EnumSet<MethodClassification>> matcher) {


        ClassTree targetClass;
        try {
            targetClass = getTreeFor(clazz);
        }
        catch (IOException exc) {
            fail(String.format("Could not read the class %s. Details: %s", clazz.getName(), exc));
            return Stream.empty();
        }

        List<MethodTree> methods = targetClass.methods().stream().filter(methodFilter).collect(Collectors.toList());
        assertThat(methods, not(empty()));

        return methods.stream().map(method -> {

            String methodName = method.rawNode().name;

            return DynamicTest.dynamicTest(
                    String.format("Checking method %s ", methodName),
                    () -> {
                        EnumSet<MethodClassification> actual = new MethodClassifier().classify(targetClass, method);
                        assertThat(actual, matcher);
                    });
        });

    }

    @TestFactory
    @DisplayName("Test accessible methods")
    public Stream<DynamicTest> testAccessibleMethods() {
        return testMethodsInClass(AccessibleOnly.class, all, taggedAs(ACCESSIBLE));
    }

    @TestFactory
    @DisplayName("Test methods returning constants")
    public Stream<DynamicTest> testReturningConstants() {
        return testMethodsInClass(
                ReturningConstants.class,
                m-> !m.rawNode().name.equals("<init>"),
                taggedAs(RETURNS_CONSTANT)
        );
    }

    @TestFactory
    @DisplayName("Test deprecated methods")
    public Stream<DynamicTest> testDeprecatedMethods() {
        return testMethodsInClass(
                WithDeprecatedMethod.class,
                m -> !m.rawNode().name.equals("<init>"),
                taggedAs(DEPRECATED)
        );
    }

    @TestFactory
    @DisplayName("Test deprecated methods in a deprecated class")
    public Stream<DynamicTest> testDeprecatedMethodsInDeprecatedClass() {
        return testMethodsInClass(DeprecatedClass.class, all, taggedAs(DEPRECATED));
    }


    @TestFactory
    @DisplayName("Test simple getters")
    public Stream<DynamicTest> testSimpleGetters() {
        return testMethodsInClass(
                WithGettersAndSetters.class,
                m -> m.rawNode().name.startsWith("get"),
                taggedAs(SIMPLE_GETTER));
    }

    @TestFactory
    @DisplayName("Test simple setters")
    public Stream<DynamicTest> testSimpleSetters() {
        return testMethodsInClass(
                WithGettersAndSetters.class,
                m -> m.rawNode().name.startsWith("set"),
                taggedAs(SIMPLE_SETTER));
    }

    @TestFactory
    @DisplayName("Test delegation methods")
    public Stream<DynamicTest> testDelegationMethods() {
        return testMethodsInClass(
                WithDelegationMethods.class,
                m -> m.rawNode().name.startsWith("delegate"),
                taggedAs(DELEGATION)
        );
    }

    @TestFactory
    @DisplayName("Test non-delegation methods")
    public Stream<DynamicTest> testNonDelegationMethods() {
        return testMethodsInClass(
                WithDelegationMethods.class,
                m -> !m.rawNode().name.startsWith("delegate"),
                notTaggedAs(DELEGATION));
    }

    @TestFactory
    @DisplayName("Test non-enum methods in enum")
    public Stream<DynamicTest> testNonEnumMethods() {
        return testMethodsInClass(ComplexEnum.class,
                m -> {
                    String name = m.rawNode().name;
                    return !name.equals("values") && !name.equals("valueOf");
                },
                notTaggedAs(ENUM_METHOD));
    }

    @TestFactory
    @DisplayName("Test non-accessible methods")
    public Stream<DynamicTest>  testNonAccessible() {
        Stream<DynamicTest> tests = testMethodsInClass(NonAccessible.class, all, notTaggedAs(ACCESSIBLE));
        for(Class<?> innerClass : NonAccessible.class.getDeclaredClasses()) {
            tests = Stream.concat(tests, testMethodsInClass(innerClass, all, notTaggedAs(ACCESSIBLE)));
        }
        return tests;
    }


}
