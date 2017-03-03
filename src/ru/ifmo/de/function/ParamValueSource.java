package ru.ifmo.de.function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 02.03.17.
 */
public interface ParamValueSource {
    Object getParamValue(String paramName);

    default <T> T getParamValue(String paramName, Class<T> clazz) {
        checkNotNull(clazz);
        Object val = getParamValue(paramName);
        if (clazz.isInstance(val))
            return (T) val;
        else
            return null;

    }
}
