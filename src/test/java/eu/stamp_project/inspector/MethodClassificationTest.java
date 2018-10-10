package eu.stamp_project.inspector;


import eu.stamp_project.inspector.test.input.*;
import eu.stamp_project.inspector.test.Utils;

import static eu.stamp_project.inspector.test.MethodClassificationMatchers.taggedAs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static eu.stamp_project.inspector.MethodClassification.*;

public class MethodClassificationTest {

    @DisplayName("Check specific methods")
    @ParameterizedTest(name="Method {1} in class {0} should be {2}")
    @MethodSource("classMethodProvider")
    public void testClassHasMethod(Class<?> clazz, String methodName, MethodClassification expected)
            throws IOException {

        ClassTree classTree = Utils.getTreeFor(clazz);
        List<MethodTree> methods = classTree.methods().stream()
                .filter(m -> m.rawNode().name.equals(methodName))
                .collect(Collectors.toList());
        if(methods.size() == 0) {
            fail(String.format("Method %s was not found in class %s", methodName, clazz.getName()));
        }
        if(methods.size() > 1) {
            fail(String.format("There is more than one overload for method %s in class %s", methodName, clazz.getName()));
        }

        EnumSet<MethodClassification> actual = new MethodClassifier().classify(classTree, methods.get(0));
        assertThat(actual, taggedAs(expected));
    }

    public static Stream classMethodProvider() {
        return Stream.of(
                Arguments.of(WithEmptyMethod.class, "empty", EMPTY, ACCESSIBLE),
                Arguments.of(WithHashCode.class, "hashCode", HASH_CODE),
                Arguments.of(WithToString.class, "toString", TO_STRING),
                Arguments.of(SimpleEnum.class, "values", ENUM_METHOD),
                Arguments.of(SimpleEnum.class, "valueOf", ENUM_METHOD),
                Arguments.of(ReturningConstants.class, "getNull", RETURNS_CONSTANT),
                Arguments.of(ReturningConstants.class, "getNull", RETURNS_NULL)
        );
    }
}

