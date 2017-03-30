package ru.ifmo.de.function;

import com.google.common.collect.ImmutableList;
import oracle.jdbc.driver.OracleCallableStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;

/**
 * Created by efimchick on 28.03.17.
 */
public class ConnectionBasedSimpleSqlStoredFunctionAppFunction implements AppFunction {

    private Connection connection;
    private SimpleSqlStoredFunctionAppFunction inner;

    public ConnectionBasedSimpleSqlStoredFunctionAppFunction(Connection connection, SimpleSqlStoredFunctionAppFunction inner) {
        this.connection = connection;
        this.inner = inner;
    }

    @Override
    public AppFunctionResult call(ParamValueSource paramValueSource) {
        return inner.call(connection, paramValueSource);
    }
}
