package ru.ifmo.de.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.sql.CLOB;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.*;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.google.common.io.CharStreams.*;
import static oracle.jdbc.driver.OracleTypes.CLOB;
import static oracle.jdbc.driver.OracleTypes.LONGVARCHAR;
import static oracle.jdbc.driver.OracleTypes.NUMBER;
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
    public void referenceFunctionCallTest() throws SQLException, UnsupportedEncodingException {

        String query = "{? = call DE_PORTFOLIO.signAwardPortfolio(?,?,?,?,?,?)}";

        try (Connection conn = DbConnectionProvider.connect()) {
            try (OracleCallableStatement callableStatement = (OracleCallableStatement) conn.prepareCall(query)) {


                callableStatement.registerOutParameter(1, NUMBER);
                callableStatement.setBigDecimal(2, new BigDecimal(1));
                callableStatement.setBigDecimal(3, new BigDecimal(1));
                callableStatement.setString(4, "back");
                callableStatement.registerOutParameter(5, CLOB);
                callableStatement.registerOutParameter(6, CLOB);
                callableStatement.registerOutParameter(7, NUMBER);

                callableStatement.execute();

                BigDecimal retVal = callableStatement.getBigDecimal(1);
                oracle.sql.CLOB xmlClob = callableStatement.getCLOB(5);
                oracle.sql.CLOB xslClob = callableStatement.getCLOB(6);
                BigDecimal xsl_id = callableStatement.getBigDecimal(7);

                System.out.println(xmlClob.length());
                String xml = xmlClob.getSubString(1, (int) xmlClob.length());
                System.out.println(xmlClob.length());



                String xsl = xslClob.getSubString(1, (int) xslClob.length());
/*
                StringWriter writer = new StringWriter((int) xmlClob.length());

                Reader xmlStream = xmlClob.getCharacterStream();
                copy(xmlStream, writer);
                String xml = writer.toString();
*/

                System.out.println("retVal = " + retVal);
                System.out.println("----------------------------XML-------------------------------------");
                System.out.println(xml.replace("\r", ""));
                System.out.println("----------------------------XSL-------------------------------------");
                //System.out.println(xsl);
                System.out.println("xsl_id = " + xsl_id);
            }
        }


    }

    @Test
    public void simpleSqlFunctionTest() throws SQLException {

        //DE_PORTFOLIO
        //FUNCTION signAwardPortfolio(key IN NUMBER, ID_ IN NUMBER, back IN LONG, xml IN OUT CLOB, xsl IN OUT CLOB, xsl_id IN OUT NUMBER) RETURN NUMBER;

        NumberOracleTypeParameter ret = new NumberOracleTypeParameter("retVal", OUT, NUMBER, 1);
        NumberOracleTypeParameter key = new NumberOracleTypeParameter("KEY", IN, NUMBER, 2);
        NumberOracleTypeParameter id = new NumberOracleTypeParameter("ID_", IN, NUMBER, 3);
        TextOracleTypeParameter back = new TextOracleTypeParameter("BACK", IN, LONGVARCHAR, 4);
        OutputClobOracleTypeParameter xml = new OutputClobOracleTypeParameter("XML", 5);
        OutputClobOracleTypeParameter xsl = new OutputClobOracleTypeParameter("XSL", 6);
        NumberOracleTypeParameter xslid = new OutNumberOracleTypeParameter("XSL_ID", NUMBER, 7);

        List<OracleTypeParameter> params = ImmutableList.of(ret, key, id, back, xml, xsl, xslid);

        ParamValueSource valueSource = new MultiParamValueSource(
                ImmutableMap.<String, Object>builder()
                        .put("KEY", new BigDecimal(1))
                        .put("ID_", new BigDecimal(1))
                        .put("BACK", "back")
                        .build()
        );

        try (Connection connection = DbConnectionProvider.connect()) {

            ConnectionBasedSimpleSqlStoredFunctionAppFunction function = new ConnectionBasedSimpleSqlStoredFunctionAppFunction(
                    connection,
                    new SimpleSqlStoredFunctionAppFunction("DE_PORTFOLIO.signAwardPortfolio", params)
            );

            function.call(valueSource);
        }


    }

}