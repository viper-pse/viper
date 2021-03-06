package edu.kit.ipd.pp.viper.model.interpreter;

import java.util.List;

/**
 * Represents a unification that could not be completed because of an error.
 * Other than a fail-result this represents not a failed unification itself
 * but an unification that failed because something else was wrong.
 */
public class ErrorUnificationResult extends UnificationResult {
    private final String errorMessage;

    /**
     * Initializes an error-result with an error message.
     *
     * @param message error message
     */
    public ErrorUnificationResult(String message) {
        super();

        this.errorMessage = message;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String toHtml() {
        return this.getErrorMessage();
    }

    @Override
    public List<Substitution> getSubstitutions() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
