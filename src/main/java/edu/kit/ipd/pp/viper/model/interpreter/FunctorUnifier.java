package edu.kit.ipd.pp.viper.model.interpreter;

import edu.kit.ipd.pp.viper.model.ast.Functor;
import edu.kit.ipd.pp.viper.model.ast.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * Tries to unify a functor with a term.
 */
public class FunctorUnifier extends Unifier {
    private final Functor functor;

    /**
     * Initializes a functor-unifier with the functor to try unification with.
     *
     * @param functor functor to try unification with
     */
    public FunctorUnifier(Functor functor) {
        super();

        this.functor = functor;
    }

    @Override
    protected Functor getTerm() {
        return this.functor;
    }

    /**
     * How to unify two functors. If their names or arity are not equal, unification
     * fails. If they are, try to unify all their parameters. If any of the
     * unifications of parameters fails, unification fails. For every unification of
     * parameters, store the resulting substitutions. If all unifications of
     * parameters have succeeded, unification succeeds with the stored
     * substitutions.
     *
     * @param functor functor to unify with
     * @return an UnificationResult according to the rules stated above
     */
    @Override
    public UnificationResult visit(Functor functor) {
        Functor me = this.getTerm();

        if (!me.matches(functor)) {
            return UnificationResult.fail(me, functor);
        }

        List<Substitution> substitutions = new ArrayList<>();

        for (int index = 0; index < me.getArity(); index++) {
            Term lhs = me.getParameters().get(index);
            Term rhs = functor.getParameters().get(index);

            // Previously found substitutions must be applied in case a variable has been
            // used multiple times.
            // Consider the unification "max(42, 17, Max) = max(X, Y, X)".
            for (Substitution s : substitutions) {
                SubstitutionApplier replacer = new SubstitutionApplier(s);

                lhs = lhs.accept(replacer);
                rhs = rhs.accept(replacer);
            }

            UnificationResult partialResult = rhs.accept(lhs.accept(new UnifierCreator()));

            if (!partialResult.isSuccess()) {
                return partialResult;
            }

            substitutions.addAll(partialResult.getSubstitutions());
        }

        return UnificationResult.success(substitutions);
    }
}
