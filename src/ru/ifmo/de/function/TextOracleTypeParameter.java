package ru.ifmo.de.function;

import com.google.common.base.Preconditions;
import oracle.jdbc.driver.OracleCallableStatement;

import java.sql.CallableStatement;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 28.03.17.
 */
public class TextOracleTypeParameter extends OracleTypeParameter<String> {
    public TextOracleTypeParameter(String name, Direction direction, int oracleType, int position) {
        super(name, String.class, direction, oracleType, position);
    }


    @Override
    public boolean prepareCallableStatement(ParamValueSource valueSource, OracleCallableStatement cs) {
        return inAndOutCommonPreparing(valueSource, cs);
    }



    @Override
    public String getValue(OracleCallableStatement cs) {
        checkNotNull(cs);
        String res = null;
        return res;
    }
}
