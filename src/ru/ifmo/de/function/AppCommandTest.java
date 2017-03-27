package ru.ifmo.de.function;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.oracore.OracleType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.ifmo.de.function.Parameter.Direction.IN;
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
            Parameter toOutputParam = new Parameter("output", IN);
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
        //cs = conn.prepareCall("{? = call " + schema + "." + "DE_COMMON" + ".sch_addScheduleEvent(?,?,?,?,?,?,?,?)}");

        Parameter p = new OracleTypeParameter("name", OracleTypes.VARCHAR, IN);



    }

    @Test
    public void sqlCallableStatementFunctionBasicApi() {
        //cs = conn.prepareCall("{? = call " + schema + "." + "DE_COMMON" + ".sch_addScheduleEvent(?,?,?,?,?,?,?,?)}");




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
