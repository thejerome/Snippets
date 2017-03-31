package ru.ifmo.de.function;

import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;
import oracle.sql.CLOB;

import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.ifmo.de.function.Parameter.Direction.INOUT;

/**
 * Created by efimchick on 30.03.17.
 */
public class InputClobOracleTypeParameter extends OracleTypeParameter {
    public InputClobOracleTypeParameter(String name, int position) {
        super(name, Object.class, INOUT, OracleTypes.CLOB, position);
    }

    @Override
    public boolean prepareCallableStatement(ParamValueSource valueSource, OracleCallableStatement cs) {
        checkNotNull(valueSource);
        checkNotNull(cs);

        try {
            cs.registerOutParameter(position,oracleType);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public String lookForValue(ParamValueSource paramValueSource) {
        checkNotNull(paramValueSource);
        return paramValueSource.getParamValue(this.name, String.class);
    }

    @Override
    public boolean callIsNeededAfterCallableStatementExecuted() {
        return true;
    }

    @Override
    public boolean afterCallableStatementExecuted(ParamValueSource valueSource, OracleCallableStatement cs) {
        checkNotNull(valueSource);
        checkNotNull(cs);
        try {
            String value = lookForValue(valueSource);
            if (value != null) {
                CLOB clob = cs.getCLOB(position);
                Writer clobWriter = clob.setCharacterStream(1L);
                clobWriter.write(value);
                clobWriter.close();
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
