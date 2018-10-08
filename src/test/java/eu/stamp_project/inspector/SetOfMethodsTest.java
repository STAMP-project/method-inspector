package eu.stamp_project.inspector;

import eu.stamp_project.inspector.test.input.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
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
import static eu.stamp_project.inspector.test.Utils.getTreeFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DisplayName("Set of methods in a class with a classification")
public class SetOfMethodsTest {


    private Stream<DynamicTest> testExpectedClassification(
            Class<?> clazz,
            Predicate<MethodTree> methodFilter,
            MethodClassification expected,
            String description) throws IOException {

        ClassTree targetClass = getTreeFor(clazz);

        List<MethodTree> methods = targetClass.methods().stream().filter(methodFilter).collect(Collectors.toList());
        assertThat(methods, not(empty()));

        return methods.stream().map( method -> {
            EnumSet<MethodClassification> actual = new MethodClassifier().classify(targetClass, method);

            String methodName = method.rawNode().name;
            return DynamicTest.dynamicTest(
                    String.format("Method %s should be %s",  methodName, description),
                    () -> assertTrue(
                            actual.contains(expected),
                            String.format("Method %s was not classified as %s. Instead is was classified as %s", methodName, description, actual.toString())
                    ));
        });
    }

    //TODO: Refactor these two methods
    private Stream<DynamicTest> testUnexpectedClassification(
            Class<?> clazz,
            Predicate<MethodTree> methodFilter,
            MethodClassification expected,
            String description) throws IOException {

        ClassTree targetClass = getTreeFor(clazz);

        List<MethodTree> methods = targetClass.methods().stream().filter(methodFilter).collect(Collectors.toList());
        assertThat(methods, not(empty()));

        return methods.stream().map(method -> {
            EnumSet<MethodClassification> actual = new MethodClassifier().classify(targetClass, method);

            String methodName = method.rawNode().name;
            return DynamicTest.dynamicTest(
                    String.format("Method %s should not be %s", methodName, description),
                    () -> assertFalse(actual.contains(expected),
                            String.format("Method %s was classified as %s", methodName, actual.toString())));

        });

    }






    @TestFactory
    @DisplayName("Test accessible methods")
    public Stream<DynamicTest> testAccessibleMethods() throws IOException {
        return testExpectedClassification(
                AccessibleOnly.class,
                m -> true,
                ACCESSIBLE,
                "accessible");
    }

    @TestFactory
    @DisplayName("Test methods returning constants")
    public Stream<DynamicTest> testReturningConstants() throws IOException {
        return testExpectedClassification(
                ReturningConstants.class,
                m-> !m.rawNode().name.equals("<init>"),
                RETURNS_CONSTANT,
                "a method returning a constant"
        );
    }

    @TestFactory
    @DisplayName("Test deprecated methods")
    public Stream<DynamicTest> testDeprecatedMethods() throws IOException {
        return testExpectedClassification(
                WithDeprecatedMethod.class,
                m -> !m.rawNode().name.equals("<init>"),
                DEPRECATED,
                "deprecated");
    }

    @TestFactory
    @DisplayName("Test deprecated methods in a deprecated class")
    public Stream<DynamicTest> testDeprecatedMethodsInDeprecatedClass() throws IOException {
        return testExpectedClassification(
                DeprecatedClass.class,
                m -> true,
                DEPRECATED,
                "deprecated");
    }

    @TestFactory
    @DisplayName("Test simple getters")
    public Stream<DynamicTest> testSimpleGetters() throws IOException {
        return testExpectedClassification(
                WithGettersAndSetters.class,
                m -> m.rawNode().name.startsWith("get"),
                SIMPLE_GETTER,
                "a simple getter");
    }

    @TestFactory
    @DisplayName("Test simple setters")
    public Stream<DynamicTest> testSimpleSetters() throws IOException {
        return testExpectedClassification(
                WithGettersAndSetters.class,
                m -> m.rawNode().name.startsWith("set"),
                SIMPLE_SETTER,
                "a simple setter");
    }

    @TestFactory
    @DisplayName("Test delegation methods")
    public Stream<DynamicTest> testDelegationMethods() throws IOException {
        return testExpectedClassification(
                WithDelegationMethods.class,
                m -> m.rawNode().name.startsWith("delegate"),
                DELEGATION,
                "a delegation");
    }

    @TestFactory
    @DisplayName("Test non-delegation methods")
    public Stream<DynamicTest> testNonDelegationMethods() throws IOException {
        return testUnexpectedClassification(
                WithDelegationMethods.class,
                m -> !m.rawNode().name.startsWith("delegate"),
                DELEGATION,
                "a delegation");
    }

    @TestFactory
    @DisplayName("Test non-accessible methods")
    public Stream<DynamicTest>  testNonAccessible() throws IOException, ClassNotFoundException {
        Stream<DynamicTest> tests = testUnexpectedClassification(NonAccessible.class,m -> true, ACCESSIBLE, "accessible");
        for(Class<?> innerClass : NonAccessible.class.getDeclaredClasses()) {
            tests = Stream.concat(tests, testUnexpectedClassification(innerClass, m -> true, ACCESSIBLE, "accessible"));
        }
        return tests;
    }

    @TestFactory
    @DisplayName("Test non-enum methods in enum")
    public Stream<DynamicTest> testNonEnumMethods() throws IOException {
        return testUnexpectedClassification(ComplexEnum.class, m -> {
                    String name = m.rawNode().name;
                    return !name.equals("values") && !name.equals("valueOf");
                },
                ENUM_METHOD,
                "enum");
    }
}
