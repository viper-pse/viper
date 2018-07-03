package edu.kit.ipd.pp.viper.model.ast;

public class GreaterThanEqualGoal extends ComparisonGoal {
    /**
     * Initializes a greater-than-equal comparison goal with a left and right hand side.
     *
     * @param lhs left hand side of the comparison
     * @param rhs right hand side of the comparison
     */
    public GreaterThanEqualGoal(Term lhs, Term rhs) {
        super(null, null);
        // TODO
    }

    /**
     * Checks whether lhs is greater than or equal to rhs.
     * 
     * @param lhs left hand side of the comparison
     * @param rhs right hand side of the comparison
     * @return
     */
    @Override
    public boolean compareNumbers(int lhs, int rhs) {
        // TODO
        return false;
    }

    /**
     * Getter-method for the symbol of this comparison ("&gt;=").
     *
     * @return a greater-than/equals sign
     */
    @Override
    protected String getSymbol() {
        // TODO
        return null;
    }
}
