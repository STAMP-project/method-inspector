package fr.inria.stamp.inspector;

import fr.inria.stamp.inspector.recognition.MethodBodyClassifier;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.EnumSet;

public class InspectorMethodVisitor extends MethodVisitor {

    public InspectorMethodVisitor(MethodEntry method) {
        super(Opcodes.ASM5);
        this.method = method;
    }

    private MethodBodyClassifier classifier = new MethodBodyClassifier();

    private MethodEntry method;


    public MethodEntry getMethod() {
        return method;
    }

    @Override
    public void visitCode() {

    }

    @Override
    public void visitEnd() {
        EnumSet<MethodClassification> classifications = classifier.getClassifications();
        method.getClassifications().addAll(classifications);
    }

    @Override
    public void	visitLineNumber(int line, Label start) {

    }

    @Override
    public void	visitInsn(int opcode) {
        if(opcode < 0) return; //Ignore new frame declaration
        classifier.advance(opcode);
    }

    @Override
    public void	visitIntInsn(int opcode, int operand) {
        classifier.advance(opcode);
    }

    @Override
    public void	visitJumpInsn(int opcode, Label label) {
        classifier.advance(opcode);
    }

    @Override
    public void	visitVarInsn(int opcode, int var) {
        classifier.advance(opcode);
    }

    @Override
    public void	visitTypeInsn(int opcode, String type){
        classifier.advance(opcode);
    }

    @Override
    public void	visitFieldInsn(int opcode, String owner, String name, String desc) {
        classifier.advance(opcode);
    }

    @Override
    public void	visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        classifier.advance(opcode);
    }

    @Override
    public void	visitIincInsn(int var, int increment) {
        classifier.advance(Opcodes.IINC);
    }

    @Override
    public void	visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        classifier.advance(Opcodes.INVOKEDYNAMIC);
    }

    @Override
    public void	visitLdcInsn(Object cst) {
        classifier.advance(Opcodes.LDC);
    }

    @Override
    public void	visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        classifier.advance(Opcodes.LOOKUPSWITCH);
    }

    @Override
    public void	visitMultiANewArrayInsn(String desc, int dims){
        classifier.advance(Opcodes.MULTIANEWARRAY);
    }

    @Override
    public void	visitTableSwitchInsn(int min, int max, Label dflt, Label... labels){
        classifier.advance(Opcodes.TABLESWITCH);
    }

    @Override
    public void	visitTryCatchBlock(Label start, Label end, Label handler, String type){
        classifier.stopRecognition();
    }
}
