package ru.ifmo.de.function;

import java.sql.CallableStatement;
import java.sql.SQLException;

import static oracle.jdbc.OracleTypes.*;
import static oracle.jdbc.OracleTypes.BLOB;

/**
 * Created by efimchick on 27.03.17.
 */
public abstract class OracleTypeParameter<T> extends Parameter<T> {
    protected final int oracleType;
    protected final int position;

    protected OracleTypeParameter(String name, Class<T> valueClass, Direction direction, int oracleType, int position) {
        super(name, valueClass, direction);
        this.oracleType = oracleType;
        this.position = position;
    }

    public abstract boolean prepareCallableStatement(ParamValueSource valueSource, CallableStatement cs);
    public abstract T getValue(CallableStatement cs);
}
