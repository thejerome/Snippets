package ru.ifmo.de.function;

/**
 * Created by efimchick on 30.03.17.
 */
public class EmptyParamValueSource implements ParamValueSource {
    @Override
    public Object getParamValue(String paramName) {
        return null;
    }

    @Override
    public <T> T getParamValue(String paramName, Class<T> clazz) {
        return null;
    }
}
