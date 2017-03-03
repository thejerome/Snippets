package ru.ifmo.de.function;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 02.03.17.
 */
public class MultiParamValueSource implements ParamValueSource{

    private Map<String, Object> storage = new HashMap<>();

    public MultiParamValueSource(Map paramAndValues) {
        storage.putAll(paramAndValues);
    }

    @Override
    public Object getParamValue(String paramName) {
        return storage.get(paramName);
    }


}
