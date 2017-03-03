package ru.ifmo.de.function;

/**
 * Created by efimchick on 02.03.17.
 */
public class SingleValueAppFunctionResult <T> implements AppFunctionResult {
    private final boolean successful;
    private final T value;

    public SingleValueAppFunctionResult(boolean successful, T value) {
        this.successful = successful;
        this.value = value;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

    public T getValue() {
        return value;
    }
}
