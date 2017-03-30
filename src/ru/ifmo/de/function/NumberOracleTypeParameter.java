package ru.ifmo.de.function;

import oracle.jdbc.driver.OracleCallableStatement;

import java.math.BigDecimal;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 28.03.17.
 */
public class NumberOracleTypeParameter extends OracleTypeParameter<BigDecimal> {
    public NumberOracleTypeParameter(String name, Direction direction, int oracleType, int position) {
        super(name, BigDecimal.class, direction, oracleType, position);
    }

    @Override
    public boolean prepareCallableStatement(ParamValueSource valueSource, OracleCallableStatement cs) {
        return inAndOutCommonPreparing(valueSource, cs);
    }

}
