package org.seams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

import org.seams.BinOpMutator;
//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {

    private static void replace(CtElement e, CtElement op) {
        if (e instanceof CtStatement  && op instanceof CtStatement) {
            e.replace(op);
            return;
        }
        if (e instanceof CtExpression && op instanceof CtExpression) {
            e.replace(op);
            return;
        }
        throw new IllegalArgumentException(e.getClass()+" "+op.getClass());
    }

    public static void main(String[] args) {
        Launcher l = new Launcher();
        l.addInputResource("C:\\Users\\juju6\\IdeaProjects\\HelloWorld\\src\\Main.java");
        l.buildModel();
        BinOpMutator mutator = new BinOpMutator();
        CtClass origClass = (CtClass) l.getFactory().Package().getRootPackage()
                .getElements(new TypeFilter(CtClass.class)).get(0);

        List<CtClass> mutants = new ArrayList<>();

        // now we apply a transformation
        // we replace "+" and "*" by "-"
        List<CtElement> elementsToBeMutated = origClass.getElements(new Filter<CtElement>() {

            @Override
            public boolean matches(CtElement arg0) {
                return mutator.isToBeProcessed(arg0);
            }
        });

        for (CtElement e : elementsToBeMutated) {
            // this loop is the trickiest part
            // because we want one mutation after the other

            // cloning the AST element
            CtElement op = l.getFactory().Core().clone(e);

            // mutate the element
            mutator.process(op);

            // temporarily replacing the original AST node with the mutated element
            replace(e,op);

            // creating a new class containing the mutating code
            CtClass klass = l.getFactory().Core()
                    .clone(op.getParent(CtClass.class));
            // setting the package
            klass.setParent(origClass.getParent());

            // adding the new mutant to the list
            mutants.add(klass);

            // restoring the original code
            replace(op, e);
        }

        for (CtClass mutant : mutants){
            System.out.println(mutant);
        }
    }
}
