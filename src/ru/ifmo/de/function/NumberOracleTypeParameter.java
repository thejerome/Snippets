package ru.ifmo.de.function;

import com.google.common.base.Preconditions;
import jdk.nashorn.internal.codegen.CompilerConstants;
import oracle.jdbc.driver.OracleTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 28.03.17.
 */
public class NumberOracleTypeParameter extends OracleTypeParameter<BigDecimal> {
    public NumberOracleTypeParameter(String name, Direction direction, int oracleType, int position) {
        super(name, BigDecimal.class, direction, oracleType, position);
    }

    @Override
    public boolean prepareCallableStatement(ParamValueSource valueSource, CallableStatement cs) {
        checkNotNull(cs);
        if (isInput()) {
            BigDecimal value = lookForValue(valueSource);
            if (value == null) {
                return false;
            } else {
                try {
                    cs.setBigDecimal(position, value);
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BigDecimal getValue(CallableStatement cs){
        checkNotNull(cs);
        BigDecimal value = null;
        if (isOutput()){
            try {
                value = cs.getBigDecimal(position);
            } catch (Exception e){
                //value stay null
            }
        }
        return value;
    }

}
