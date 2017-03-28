package ru.ifmo.de.function;

import com.google.common.base.Preconditions;

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
    public boolean prepareCallableStatement(ParamValueSource valueSource, CallableStatement cs) {
        checkNotNull(cs);
        boolean res = false;
        if (isInput()){
            try{
                cs.setString(position, lookForValue(valueSource));
                res = true;
            } catch (Exception e){
                //stay false
            }
        }

        if (isOutput()){
            try {
                cs.registerOutParameter(position, oracleType);
                res = true;
            } catch (SQLException e) {
                res = false;
            }
        }
        return res;
    }

    @Override
    public String getValue(CallableStatement cs) {
        checkNotNull(cs);
        String res = null;
        return res;
    }
}
