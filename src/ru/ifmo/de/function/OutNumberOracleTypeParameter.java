package ru.ifmo.de.function;

import oracle.jdbc.driver.OracleCallableStatement;

import static ru.ifmo.de.function.Parameter.Direction.INOUT;

/**
 * Created by efimchick on 31.03.17.
 */
public class OutNumberOracleTypeParameter extends NumberOracleTypeParameter {
    public OutNumberOracleTypeParameter(String name, int oracleType, int position) {
        super(name, INOUT, oracleType, position);
    }

    @Override
    public boolean prepareCallableStatement(ParamValueSource valueSource, OracleCallableStatement cs) {
        return super.outCommonPreparingCS(cs);
    }
}
