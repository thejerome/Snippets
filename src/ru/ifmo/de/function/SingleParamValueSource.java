package ru.ifmo.de.function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 02.03.17.
 */
public class SingleParamValueSource <T> implements ParamValueSource {

    private String paramName;
    private T paramValue;


    public SingleParamValueSource(String paramName, T paramValue) {
        checkNotNull(paramName);
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    @Override
    public Object getParamValue(String paramName) {
        if (this.paramName.equals(paramName))
            return paramValue;
        return null;
    }
}
