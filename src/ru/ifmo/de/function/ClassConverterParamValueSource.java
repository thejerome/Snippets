package ru.ifmo.de.function;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by EE on 2017-03-16.
 */
public class ClassConverterParamValueSource implements ParamValueSource {
    private final ParamValueSource innerSource;
    private final List<ClassConverter> converters;

    public ClassConverterParamValueSource(ParamValueSource innerSource, List<ClassConverter> converters) {
        this.innerSource = innerSource;
        this.converters = Lists.newArrayList(converters);
    }

    @Override
    public Object getParamValue(String paramName) {
        return innerSource.getParamValue(paramName);
    }

    @Override
    public <T> T getParamValue(String paramName, Class<T> clazz) {
        T retVal = innerSource.getParamValue(paramName, clazz);
        if(retVal == null){
            Object retCandidate = innerSource.getParamValue(paramName);
            retVal = converters.stream().sequential()
                .map(converter -> converter.convert(retCandidate))
                .filter(clazz::isInstance)
                .map(convertedRetCandidate -> (T) convertedRetCandidate)
                .findFirst().orElse(null);
        }
        return retVal;
    }
}
