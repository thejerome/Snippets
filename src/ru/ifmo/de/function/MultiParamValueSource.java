package ru.ifmo.de.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 02.03.17.
 */
public class MultiParamValueSource implements ParamValueSource{

    protected interface Storage{
        Object get(String paramName);
    }

    private static class MapStorage implements Storage{
        private final Map<String, Object> paramAndValues = new HashMap<>();

        public MapStorage(Map<String, ? extends Object> paramAndValues) {
            this.paramAndValues.putAll(paramAndValues);
        }

        @Override
        public Object get(String paramName) {
            return paramAndValues.get(paramName);
        }
    }

    private Storage storage;

    public MultiParamValueSource(Map<String, ? extends Object> paramAndValues) {
        this (new MapStorage(paramAndValues));
    }

    protected MultiParamValueSource(Storage storage){
        this.storage = storage;
    }

    @Override
    public Object getParamValue(String paramName) {
        return storage.get(paramName);
    }


}
