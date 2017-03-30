package ru.ifmo.de.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static oracle.jdbc.OracleTypes.LONGVARCHAR;
import static oracle.jdbc.OracleTypes.NUMBER;
import static org.junit.Assert.assertTrue;
import static ru.ifmo.de.function.Parameter.Direction.IN;
import static ru.ifmo.de.function.Parameter.Direction.INOUT;
import static ru.ifmo.de.function.Parameter.Direction.OUT;

public class OracleFunctionsTest {

    @Rule
    public final SystemOutRule soutRule = new SystemOutRule().enableLog();


    @BeforeClass
    public static void DbConnectionProvider(){
        DbConnectionProvider.init();
    }

    @Test
    public void isDBConnectionOk() throws SQLException {
        Connection conn = DbConnectionProvider.connect();

        assertTrue(!conn.isClosed());
        conn.close();
    }

    @Test
    public void simpleSqlFunctionTest() throws SQLException {

        //DE_PORTFOLIO
        //FUNCTION signAwardPortfolio(key IN NUMBER, ID_ IN NUMBER, back IN LONG, xml IN OUT CLOB, xsl IN OUT CLOB, xsl_id IN OUT NUMBER) RETURN NUMBER;

        NumberOracleTypeParameter ret = new NumberOracleTypeParameter("return", OUT, NUMBER, 1);
        NumberOracleTypeParameter key = new NumberOracleTypeParameter("KEY", IN, NUMBER, 2);
        NumberOracleTypeParameter id = new NumberOracleTypeParameter("ID_", IN, NUMBER, 3);
        TextOracleTypeParameter back = new TextOracleTypeParameter("BACK", IN, LONGVARCHAR, 4);
        OutputClobOracleTypeParameter xml = new OutputClobOracleTypeParameter("XML", 5);
        OutputClobOracleTypeParameter xsl = new OutputClobOracleTypeParameter("XSL", 6);
        NumberOracleTypeParameter xslid = new NumberOracleTypeParameter("XSL_ID", INOUT, NUMBER, 7);

        List<OracleTypeParameter> params = ImmutableList.of(ret, key, id, back, xml, xsl, xslid);




        ConnectionBasedSimpleSqlStoredFunctionAppFunction function = new ConnectionBasedSimpleSqlStoredFunctionAppFunction(
                DbConnectionProvider.connect(),
                new SimpleSqlStoredFunctionAppFunction("DE_PORTFOLIO.signAwardPortfolio", params)
        );


        ParamValueSource valueSource = new MultiParamValueSource(
                ImmutableMap.<String, Object>builder()
                        .put("KEY", new BigDecimal(1))
                        .put("ID_", new BigDecimal(1))
                        .put("BACK", "back")
                        .build()
        );

        function.call(valueSource);




    }

}