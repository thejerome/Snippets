package ru.ifmo.de.function.recparam;

import dlc.servlet.RecParam;
import ru.ifmo.de.function.OracleTypeParameter;

/**
 * Created by efimchick on 31.03.17.
 */
public interface OracleTypeParameterFactory<T> {
    OracleTypeParameter<T> newParameter(RecParam recParam);
}
