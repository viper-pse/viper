package edu.kit.ipd.pp.viper.model.interpreter;

import edu.kit.ipd.pp.viper.model.ast.Number;

import java.util.Arrays;

/**
 * Tries to unify number with another term.
 */
public class NumberUnifier extends Unifier {
    private final Number number;

    /**
     * Initializes the unifier with a number to do unification with
     * 
     * @param number number to unify
     */
    public NumberUnifier(Number number) {
        super();

        this.number = number;
    }

    @Override
    protected Number getTerm() {
        return this.number;
    }

    /**
     * This method specifies how to unify two numbers. If they are equal, the
     * unification is successful, but does not yield any subsitutions. If they are
     * not equal, the unification fails.
     *
     * @param number number to unify with
     * @return an UnificationResult according to the rules stated above
     */
    @Override
    public UnificationResult visit(Number number) {
        if (number.equals(this.getTerm())) {
            return UnificationResult.success(Arrays.asList());
        } else {
            return UnificationResult.fail(this.getTerm(), number);
        }
    }
}
