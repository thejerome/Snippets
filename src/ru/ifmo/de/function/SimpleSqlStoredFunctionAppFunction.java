package ru.ifmo.de.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.lang.String.join;

/**
 * Created by efimchick on 28.03.17.
 */
public class SimpleSqlStoredFunctionAppFunction implements AppFunction {
    private final String fullName;
    private final List<OracleTypeParameter> parameters;
    private String parameterTemplate = null;
    private String query = null;


    public SimpleSqlStoredFunctionAppFunction(String fullName, List<OracleTypeParameter> parameters) {
        this.fullName = fullName;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    @Override
    public AppFunctionResult call(ParamValueSource paramValueSource){
        throw new RuntimeException("Not implemented here");
    };

    AppFunctionResult call(Connection connection, ParamValueSource paramValueSource) {

        try {
            OracleCallableStatement cs = (OracleCallableStatement) connection.prepareCall(getQuery());

            parameters.stream()
                    .peek(System.out::println)
                    .map(p -> p.prepareCallableStatement(paramValueSource, cs))
                    .forEach(System.out::println);

            cs.execute();

            parameters.stream()
                    .filter(p -> p.callIsNeededAfterCallableStatementExecuted())
                    .peek(System.out::println)
                    .map(p -> p.afterCallableStatementExecuted(paramValueSource, cs))
                    .forEach(System.out::println);


            parameters.stream()
                    .filter(p -> p.isOutput())
                    .peek(System.out::println)
                    .map(p -> p.getValue(cs))
                    .forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getParameterTemplate(){
        if (parameterTemplate == null)
            parameterTemplate = join("", "(",
                    parameters.stream().sequential()
                            .filter(p -> p.position != 0)
                            .map(p -> "?")
                            .collect(Collectors.joining(",")),
                    ")");
        return parameterTemplate;
    }

    private String getQuery(){
        if (query == null)
            query = join("",
                "{",
                parameters.stream().anyMatch(p -> p.position == 0) ? "? = call " : "call ",
                fullName,
                getParameterTemplate(),
                "}"
        );
        return query;
    }


}
