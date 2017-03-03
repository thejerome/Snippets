package ru.ifmo.de.function;

/**
 * Created by efimchick on 02.03.17.
 */
public class SuccessfulEmptyAppFunctionResult implements AppFunctionResult {
    @Override
    public boolean isSuccessful() {
        return true;
    }
}
