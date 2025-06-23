package org.seams;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;

public class BinOpMutator extends AbstractProcessor<CtElement>{
    @Override
    public boolean isToBeProcessed(CtElement candidate) {
        return candidate instanceof CtBinaryOperator;
    }

    @Override
    public void process(CtElement candidate) {
        if (!(candidate instanceof CtBinaryOperator)) {
            System.out.println("It's not binary operator!");
            return;
        }
        CtBinaryOperator op = (CtBinaryOperator)candidate;
        op.setKind(BinaryOperatorKind.MINUS);
    }
}

