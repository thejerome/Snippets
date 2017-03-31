package ru.ifmo.de.function;

import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.sql.CLOB;

import java.io.*;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 30.03.17.
 */
public class OutputClobOracleTypeParameter extends OracleTypeParameter<String> {
    public OutputClobOracleTypeParameter(String name, int position) {
        super(name, String.class, Direction.INOUT, OracleTypes.CLOB, position);
    }


    @Override
    public boolean prepareCallableStatement(ParamValueSource valueSource, OracleCallableStatement cs) {
        checkNotNull(cs);
        checkNotNull(valueSource);
        try{
            System.out.println("registring " + oracleType + " at " + position + " for " + name);
            cs.registerOutParameter(position,oracleType);
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    @Override
    public String getValue(OracleCallableStatement cs) {
        try {
            CLOB clob = cs.getCLOB(position);
            if(clob != null) {
                String value = clob.getSubString(1, (int) clob.length());
                return value;
            }
        } catch (SQLException e) {
        }
        return null;
    }
}
