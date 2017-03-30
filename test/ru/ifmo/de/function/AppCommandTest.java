package ru.ifmo.de.function;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import oracle.jdbc.driver.OracleCallableStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.powermock.reflect.Whitebox;
import ru.ifmo.de.function.classconverters.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Boolean.*;
import static oracle.jdbc.driver.OracleTypes.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.ifmo.de.function.Parameter.Direction.IN;
import static ru.ifmo.de.function.Parameter.Direction.INOUT;
import static ru.ifmo.de.function.Parameter.Direction.OUT;

public class AppCommandTest {

    @Rule
    public final SystemOutRule soutRule = new SystemOutRule().enableLog();

    @Test
    public void appFunctionBasicAPI() throws Exception {
        AppFunction af = null;
        ParamValueSource paramValueSource = null;
        AppFunctionResult result = null;
    }

    @Test
    public void appFunctionBasicImplementationsAPI() throws Exception {

        {
            Parameter<String> toOutputParam = new Parameter<>("output", String.class, IN);
            AppFunction af = new SoutParamValueAppFunction(toOutputParam);

            ParamValueSource paramValueSource = new SingleParamValueSource("output", "sum41");
            AppFunctionResult result = af.call(paramValueSource);
            assertThat(result.isSuccessful(), is(true));
            assertThat(soutRule.getLog().trim(), is("sum41"));
        }
        {
            Parameter toOutputParam = new Parameter("output", Object.class, IN);
            AppFunction af = new SoutParamValueAppFunction(toOutputParam);

            ParamValueSource paramValueSource = new SingleParamValueSource("output", "sum41");
            AppFunctionResult result = af.call(paramValueSource);
            assertThat(result.isSuccessful(), is(true));
            assertThat(soutRule.getLog().trim(), is("sum41\nsum41"));
        }

    }

    @Test
    public void parameterApiInAnonimousAppFunction() throws Exception {
        Parameter nonClassedInParam = new Parameter("nonClassedIn", Object.class, IN);
        Parameter<String> stringClassedInParam = new Parameter<String>("stringClassedIn", String.class, IN);
        Parameter nonClassedOutParam = new Parameter("stringClassedOut", Object.class, OUT);
        Parameter<String> stringClassedOutParam = new Parameter<String>("stringClassedOut", String.class, OUT);


        AppFunction function = new AppFunction() {
            @Override
            public AppFunctionResult call(ParamValueSource paramValueSource) {
                nonClassedInParam.lookForValue(paramValueSource);
                stringClassedInParam.lookForValue(paramValueSource);

                return new SuccessfulEmptyAppFunctionResult();
            }
        };


        ArrayList<String> keys = Lists.<String>newArrayList("nonClassedIn", "stringClassedOut");
        Function<String, String> stringStringFunction = s -> s;
        ImmutableMap<String, String> paramAndValues = Maps.toMap(keys, stringStringFunction);

        ParamValueSource paramValueSource = new MultiParamValueSource(
                paramAndValues
        );
        AppFunctionResult result = function.call(paramValueSource);
        assertThat(result.isSuccessful(), is(true));

    }

    @Test
    public void singleParamValueSourceFindParamByNameAndClass() throws Exception {
        ParamValueSource paramValueSource = new SingleParamValueSource("output", "sum41");

        assertThat(paramValueSource.getParamValue("output"), is("sum41"));
        assertThat(paramValueSource.getParamValue("output", Object.class), is("sum41"));
        assertThat(paramValueSource.getParamValue("output", String.class), is("sum41"));
        assertThat(paramValueSource.getParamValue("output", Integer.class), nullValue());
    }

    @Test(expected = NullPointerException.class)
    public void singleParamValueSourceNullParamNameIsNotAllowed() throws Exception {
        new SingleParamValueSource(null, "sum41");
    }

    @Test
    public void singleValueAppFunctionResultBasicAPI() throws Exception {
        SingleValueAppFunctionResult result = new SingleValueAppFunctionResult(true, "funcValue");
        assertThat(result.isSuccessful(), is(true));
        assertThat(result.getValue(), is("funcValue"));

        SingleValueAppFunctionResult<String> strResult = new SingleValueAppFunctionResult<>(false, "stringValue");
        assertThat(strResult.isSuccessful(), is(false));
        assertThat(strResult.getValue(), is("stringValue"));

        SingleValueAppFunctionResult<Integer> intResult = new SingleValueAppFunctionResult<>(false, 1);
        assertThat(intResult.isSuccessful(), is(false));
        assertThat(intResult.getValue(), is(1));

    }

    @Test
    public void MultipleValueParamValueSource() throws Exception {
        Map<String, Object> paramAndValues = newHashSet("1", "2", "3", "4").stream().collect(Collectors.toMap(i -> i, i -> (Object) (i + i + i)));
        paramAndValues.put("5", new BigDecimal("5"));
        MultiParamValueSource source = new MultiParamValueSource(paramAndValues);


        assertThat(source.getParamValue("1"), is("111"));
        assertThat(source.getParamValue("2"), is("222"));
        assertThat(source.getParamValue("3"), is("333"));
        assertThat(source.getParamValue("4"), is("444"));
        assertThat(source.getParamValue("5"), is(new BigDecimal(5)));
        assertThat(source.getParamValue("5", String.class), is(nullValue()));

    }

    @Test
    public void compositeParamValueSourceMainFunctionsDoingRight() throws Exception {
        Map<String, Object> paramAndValues = newHashSet("1", "2", "3", "4").stream().collect(Collectors.toMap(i -> i, i -> (Object) (i + i + i)));
        paramAndValues.put("5", new BigDecimal("5"));
        paramAndValues.put("6", new BigDecimal(6));

        MultiParamValueSource multiParamValueSource = new MultiParamValueSource(paramAndValues);
        SingleParamValueSource<String> nullSingleParamValueSource = new SingleParamValueSource<>("6", null);
        SingleParamValueSource<String> stringSingleParamValueSource = new SingleParamValueSource<>("6", "666");
        CompositeParamValueSource source = new CompositeParamValueSource(multiParamValueSource, nullSingleParamValueSource, stringSingleParamValueSource);


        assertThat(source.getParamValue("1"), is("111"));
        assertThat(source.getParamValue("2"), is("222"));
        assertThat(source.getParamValue("3"), is("333"));
        assertThat(source.getParamValue("4"), is("444"));
        assertThat(source.getParamValue("5"), is(new BigDecimal(5)));
        assertThat(source.getParamValue("5", String.class), is(nullValue()));
        assertThat(source.getParamValue("6", String.class), is("666"));
        assertThat(source.getParamValue("6", BigDecimal.class), is(new BigDecimal(6)));
        assertThat(source.getParamValue("6", Integer.class), is(nullValue()));

    }

    @Test
    public void httpServletRequestParamValueSourceBasicTest() {

        HttpServletRequest request = getHttpServletRequestMock();

        ParamValueSource paramValueSource = new HttpServletRequestParamsParamValueSource(request);

        assertThat(paramValueSource.getParamValue("1"), is(new String[]{"http11", "http12"}));
        assertThat(paramValueSource.getParamValue("2"), is(new String[]{"http21"}));
        assertThat(paramValueSource.getParamValue("1", String.class), nullValue());

    }


    @Test
    public void ClassConverterParamValueSourceBasicApi() {
        ParamValueSource innerSource = new HttpServletRequestParamsParamValueSource(getHttpServletRequestMock());
        List<ClassConverter> converters = Lists.newArrayList(
                new StringArrayToStringClassConverter()
        );
        ParamValueSource source = new ClassConverterParamValueSource(innerSource, converters);

        assertThat(source.getParamValue("1"), is(new String[]{"http11", "http12"}));
        assertThat(source.getParamValue("1", String.class), is("http11,http12"));
        assertThat(source.getParamValue("2"), is(new String[]{"http21"}));
        assertThat(source.getParamValue("2", String.class), is("http21"));

        assertThat(source.getParamValue("2", Integer.class), nullValue());
    }

    @Test
    public void integerClassConverterFromHttpRequestSuccessful() {
        ParamValueSource innerSource = new HttpServletRequestParamsParamValueSource(
                getHttpServletRequestIntegerMock()
        );
        List<ClassConverter> converters = Lists.newArrayList(
                new StringArrayToIntegerArrayClassConverter(),
                new StringArrayToIntegerClassConverter(),
                new StringArrayToStringClassConverter(),
                new StringArrayToBigDecimalArrayClassConverter(),
                new StringArrayToBigDecimalClassConverter(),
                new StringArrayToDoubleArrayClassConverter(),
                new StringArrayToDoubleClassConverter(),
                new StringArrayToLongArrayClassConverter(),
                new StringArrayToLongClassConverter(),
                new StringArrayToTimestampClassConverter()
        );
        ParamValueSource source = new ClassConverterParamValueSource(innerSource, converters);

        assertThat(source.getParamValue("1"), is(new String[]{"111", "112"}));
        assertThat(source.getParamValue("1", String.class), is("111,112"));
        assertThat(source.getParamValue("1", Integer.class), is(111));
        assertThat(source.getParamValue("1", Integer[].class), is(new Integer[]{111, 112}));
        assertThat(source.getParamValue("1", Long.class), is(111L));
        assertThat(source.getParamValue("1", Long[].class), is(new Long[]{111L, 112L}));
        assertThat(source.getParamValue("2"), is(new String[]{"222"}));
        assertThat(source.getParamValue("2", String.class), is("222"));
        assertThat(source.getParamValue("2", Integer.class), is(222));
        assertThat(source.getParamValue("2", Integer[].class), is(new Integer[]{222}));

    }


    @Test
    public void oracleTypeParameterBasicApi() {
        //FUNCTION signAwardPortfolio(key IN NUMBER, ID_ IN NUMBER, back IN LONG, xml IN OUT CLOB, xsl IN OUT CLOB, xsl_id IN OUT NUMBER) RETURN NUMBER;

        //cs = conn.prepareCall("{? = call " + schema + "." + "DE_COMMON" + ".sch_addScheduleEvent(?,?,?,?,?,?,?,?)}");

        //used in AcademicNT
        int integer = INTEGER;
        int varchar = VARCHAR;
        int clob = CLOB;
        int cursor = CURSOR;
        int decimal = DECIMAL;
        int date = DATE;
        int longvarchar = LONGVARCHAR;
        int blob = BLOB;


        int position = 1;

        OracleTypeParameter intparam = new NumberOracleTypeParameter("intparam", IN, INTEGER, 1);
        OracleTypeParameter varcharparam = new TextOracleTypeParameter("varcharparam", IN, VARCHAR, 2);
        OracleTypeParameter clobparam = new InputClobOracleTypeParameter("clobparam", 3);
        OracleTypeParameter xmlparam = new OutputClobOracleTypeParameter("XML", 4);

        List<OracleTypeParameter> params = ImmutableList.of(intparam, varcharparam);
        OracleCallableStatement cs = mock(OracleCallableStatement.class);
        ParamValueSource valueSource = new SingleParamValueSource<>("1", "1");
        intparam.prepareCallableStatement(valueSource, cs);

    }


    @Test
    public void sqlCallableStatementFunctionRealExampleQueryIsOk() {
        NumberOracleTypeParameter ret = new NumberOracleTypeParameter("return", OUT, NUMBER, 0);
        NumberOracleTypeParameter key = new NumberOracleTypeParameter("KEY", IN, NUMBER, 1);
        NumberOracleTypeParameter id = new NumberOracleTypeParameter("ID_", IN, NUMBER, 2);
        TextOracleTypeParameter back = new TextOracleTypeParameter("BACK", IN, LONGVARCHAR, 3);
        OutputClobOracleTypeParameter xml = new OutputClobOracleTypeParameter("XML", 4);
        OutputClobOracleTypeParameter xsl = new OutputClobOracleTypeParameter("XSL", 5);
        NumberOracleTypeParameter xslid = new NumberOracleTypeParameter("XSL_ID", INOUT, NUMBER, 6);

        List<OracleTypeParameter> params = ImmutableList.of(ret, key, id, back, xml, xsl, xslid);




        SimpleSqlStoredFunctionAppFunction sqlFunction = new SimpleSqlStoredFunctionAppFunction("DE_PORTFOLIO.signAwardPortfolio", params);


        String parameterTemplate = null;
        String query = null;
        try {
            parameterTemplate = Whitebox.invokeMethod(sqlFunction, "getParameterTemplate");
            query = Whitebox.invokeMethod(sqlFunction, "getQuery");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(parameterTemplate, is("(?,?,?,?,?,?)"));
        assertThat(query, is("{? = call DE_PORTFOLIO.signAwardPortfolio(?,?,?,?,?,?)}"));

    }

    @Test
    public void directionIsSetProperly(){
        NumberOracleTypeParameter ret = new NumberOracleTypeParameter("return", OUT, NUMBER, 1);
        assertThat(ret.direction, is(OUT));
        assertThat(ret.isInput(), is(is(FALSE)));
        assertThat(ret.isOutput(), is(is(TRUE)));
        ret = new NumberOracleTypeParameter("return", IN, NUMBER, 1);
        assertThat(ret.direction, is(IN));
        assertThat(ret.isInput(), is(is(TRUE)));
        assertThat(ret.isOutput(), is(is(FALSE)));
        ret = new NumberOracleTypeParameter("return", INOUT, NUMBER, 1);
        assertThat(ret.direction, is(INOUT));
        assertThat(ret.isInput(), is(is(TRUE)));
        assertThat(ret.isOutput(), is(is(TRUE)));
    }

    @Test
    public void sqlCallableStatementFunctionBasicApi() {
        //cs = conn.prepareCall("{? = call " + schema + "." + "DE_COMMON" + ".sch_addScheduleEvent(?,?,?,?,?,?,?,?)}");

        OracleTypeParameter retparam = new NumberOracleTypeParameter("returning", OUT, INTEGER, 0);
        OracleTypeParameter intparam = new NumberOracleTypeParameter("intparam", IN, INTEGER, 1);
        OracleTypeParameter int2param = new NumberOracleTypeParameter("intparam", IN, INTEGER, 1);

        List<OracleTypeParameter> params = ImmutableList.of(intparam, int2param);

        String fullName = "DE_COMMON.LOGON";
        AppFunction sqlFunction = new SimpleSqlStoredFunctionAppFunction(fullName, params);

        String parameterTemplate = null;
        String query = null;
        try {
            parameterTemplate = Whitebox.invokeMethod(sqlFunction, "getParameterTemplate");
            query = Whitebox.invokeMethod(sqlFunction, "getQuery");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(parameterTemplate, is("(?,?)"));
        assertThat(query, is("{call DE_COMMON.LOGON(?,?)}"));

        try {
            parameterTemplate = Whitebox.invokeMethod(sqlFunction, "getParameterTemplate");
            query = Whitebox.invokeMethod(sqlFunction, "getQuery");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(parameterTemplate, is("(?,?)"));
        assertThat(query, is("{call DE_COMMON.LOGON(?,?)}"));

        sqlFunction = new SimpleSqlStoredFunctionAppFunction("DE_COMMON.LOGON", ImmutableList.of(retparam, intparam, int2param));

        try {
            parameterTemplate = Whitebox.invokeMethod(sqlFunction, "getParameterTemplate");
            query = Whitebox.invokeMethod(sqlFunction, "getQuery");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(parameterTemplate, is("(?,?)"));
        assertThat(query, is("{? = call DE_COMMON.LOGON(?,?)}"));

    }


    /*
        new RecParam("KEY", "NUMBER", 1, "IN"),
        new RecParam("DIGITAL_CODE", "NUMBER", 2, "IN"),
        new RecParam("XML", "CLOB", 3, "OUT"),
        new RecParam("XSL", "CLOB", 4, "OUT"),
        new RecParam("XSL_ID", "NUMBER", 5, "OUT")
    */
    @Test
    public void HttpSessionParamValueSourceBasicApi() {
        HttpSession session = getHttpSessionMock();
        ParamValueSource source = new HttpSessionParamValueSource(session);

        assertThat(source.getParamValue("1"), is("111"));
        assertThat(source.getParamValue("1", String.class), is("111"));
        assertThat(source.getParamValue("1", Integer.class), nullValue());
        assertThat(source.getParamValue("2"), is("222"));
        assertThat(source.getParamValue("3"), is(new BigDecimal(333)));
    }


    private HttpServletRequest getHttpServletRequestMock() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("1")).thenReturn("http11");
        when(request.getParameter("2")).thenReturn("http21");
        when(request.getParameterValues("1")).thenReturn(new String[]{"http11", "http12"});
        when(request.getParameterValues("2")).thenReturn(new String[]{"http21"});

        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>() {
            {
                put("1", new String[]{"http11", "http12"});
                put("2", new String[]{"http21"});
            }
        });
        return request;
    }

    private HttpServletRequest getHttpServletRequestIntegerMock() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("1")).thenReturn("111");
        when(request.getParameter("2")).thenReturn("222");
        when(request.getParameterValues("1")).thenReturn(new String[]{"111", "112"});
        when(request.getParameterValues("2")).thenReturn(new String[]{"222"});

        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>() {
            {
                put("1", new String[]{"111", "112"});
                put("2", new String[]{"222"});
            }
        });
        return request;
    }

    private HttpSession getHttpSessionMock() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("1")).thenReturn("111");
        when(session.getAttribute("2")).thenReturn("222");
        when(session.getAttribute("3")).thenReturn(new BigDecimal(333));

        return session;
    }
}
