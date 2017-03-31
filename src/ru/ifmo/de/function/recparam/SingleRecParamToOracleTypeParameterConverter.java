package ru.ifmo.de.function.recparam;

import dlc.servlet.RecParam;
import ru.ifmo.de.function.OracleTypeParameter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 31.03.17.
 */
public class SingleRecParamToOracleTypeParameterConverter<T> {
    private final RecParamFilter filter;
    private final OracleTypeParameterFactory<T> factory;

    public SingleRecParamToOracleTypeParameterConverter(RecParamFilter filter, OracleTypeParameterFactory<T> factory) {
        checkNotNull(filter);
        checkNotNull(factory);
        this.filter = filter;
        this.factory = factory;
    }

    public OracleTypeParameter<T> convert(RecParam recParam){
        if (filter.accept(recParam))
            return factory.newParameter(recParam);
        return null;
    }
}
