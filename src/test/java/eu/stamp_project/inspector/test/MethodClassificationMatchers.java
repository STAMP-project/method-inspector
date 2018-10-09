package eu.stamp_project.inspector.test;

import eu.stamp_project.inspector.MethodClassification;
import org.hamcrest.*;

import java.util.EnumSet;

public interface MethodClassificationMatchers {


    public static Matcher<EnumSet<MethodClassification>> taggedAs(MethodClassification tag) {

        return new TypeSafeDiagnosingMatcher<EnumSet<MethodClassification>>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Method should be tagged as ").appendValue(tag);
            }

            @Override
            protected boolean matchesSafely(EnumSet<MethodClassification> classifications, Description mismatchDescription) {
                if (classifications.contains(tag))
                    return true;
                mismatchDescription
                        .appendText("Method was instead tagged as ")
                        .appendText(classifications.toString());
                return false;

            }
        };

    }

    public static Matcher<EnumSet<MethodClassification>> notTaggedAs(MethodClassification tag) {
        return new TypeSafeDiagnosingMatcher<EnumSet<MethodClassification>>() {

            @Override
            protected boolean matchesSafely(EnumSet<MethodClassification> classifications, Description mismatchDescription) {
                if(!classifications.contains(tag))
                    return true;
                mismatchDescription
                        .appendText("Method was tagged as ")
                        .appendText(classifications.toString())
                        .appendText(" which contains ")
                        .appendValue(tag);
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Method should not be tagged as ").appendValue(tag);
            }
        };
    }


}
