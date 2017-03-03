package ru.ifmo.de.function;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by efimchick on 02.03.17.
 */
public class CombinedValueParamValueSource implements ParamValueSource {
    private final ParamValueSource[] sources;

    public CombinedValueParamValueSource(ParamValueSource... sources) {
        this.sources = sources;
    }

    @Override
    public Object getParamValue(String paramName) {
        return lookForValueInSources(paramName);
    }

    @Override
    public <T> T getParamValue(String paramName, Class<T> clazz) {
        return lookForValueInSources(paramName, clazz);
    }

    private Object lookForValueInSources(String paramName) {
        return lookForValueInSources(paramName, null);
    }

    private <T> T lookForValueInSources(String paramName, Class<T> clazz) {

        Optional<Object> optValue = Arrays.stream(sources).sequential()
                .map(s -> s.getParamValue(paramName))
                .filter(v -> v != null)
                .filter(v -> clazz != null && clazz.isInstance(v))
                .findFirst();

        return optValue.isPresent() ? (T) optValue.get() : null;
    }


}
