//package eu.stamp_project.inspector;
//
//
//import org.hamcrest.CoreMatchers;
//import org.hamcrest.core.IsCollectionContaining;
//import org.junit.Test;
//import org.objectweb.asm.ClassReader;
//import java.io.IOException;
//import java.util.EnumSet;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.hasItems;
//import static org.hamcrest.CoreMatchers.not;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.core.IsCollectionContaining.hasItem;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//public class InspectorClassVisitorTest {
//
//    private static
//
//    //TODO; Explore the idea of a custom hamcrest matcher, only if we can provide rich information about the failure
//

//
//

//
//
//
//
//
//    @Test
//    public void testOtherMethodsInEnum() throws IOException {
//        InspectorClassVisitor visitor = getVisitorFor("ComplexEnum");
//        for(MethodEntry entry : visitor.getMethods()) {
//            if(!entry.getName().equals("values") && !entry.getName().equals("valueOf")) {
//                assertThat(entry.getClassifications(), not(IsCollectionContaining.hasItem(MethodClassification.ENUM_METHOD)));
//            }
//        }
//    }
//
//}