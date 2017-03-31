package ru.ifmo.de.function.recparam;

import dlc.servlet.RecParam;
import ru.ifmo.de.function.OracleTypeParameter;
import ru.ifmo.de.function.OutNumberOracleTypeParameter;

import java.math.BigDecimal;

import static oracle.jdbc.OracleTypes.NUMBER;

/**
 * Created by efimchick on 31.03.17.
 */
public class RetValOracleTypeParameterFactory implements OracleTypeParameterFactory<BigDecimal> {
    @Override
    public OracleTypeParameter<BigDecimal> newParameter(RecParam recParam) {
        return new OutNumberOracleTypeParameter("retVal", NUMBER, 1);
    }
}
