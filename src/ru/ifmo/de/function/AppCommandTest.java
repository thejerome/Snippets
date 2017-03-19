package ru.ifmo.de.function;

import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.Mockito;
import ru.ifmo.de.function.classconverters.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

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
        AppFunction af = new SoutParamValueAppFunction("output");
        ParamValueSource paramValueSource = new SingleParamValueSource("output", "sum41");
        AppFunctionResult result = af.call(paramValueSource);
        assertThat(result.isSuccessful(), is(true));
        assertThat(soutRule.getLog().trim(), is("sum41"));


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
    public void httpServletRequestParamValueSourceBasicTest(){

        HttpServletRequest request = getHttpServletRequestMock();

        ParamValueSource paramValueSource = new HttpServleRequestParamsParamValueSource(request);

        assertThat(paramValueSource.getParamValue("1"), is(new String[]{"http11", "http12"}));
        assertThat(paramValueSource.getParamValue("2"), is(new String[]{"http21"}));
        assertThat(paramValueSource.getParamValue("1", String.class), nullValue());

    }


    @Test
    public void ClassConverterParamValueSourceBasicApi(){
        ParamValueSource innerSource = new HttpServleRequestParamsParamValueSource(getHttpServletRequestMock());
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
    public void integerClassConverterFromHttpRequestSuccessful(){
        ParamValueSource innerSource = new HttpServleRequestParamsParamValueSource(
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
        assertThat(source.getParamValue("1", Integer[].class), is( new Integer[]{111, 112}));
        assertThat(source.getParamValue("1", Long.class), is(111L));
        assertThat(source.getParamValue("1", Long[].class), is( new Long[]{111L, 112L}));
        assertThat(source.getParamValue("2"), is(new String[]{"222"}));
        assertThat(source.getParamValue("2", String.class), is("222"));
        assertThat(source.getParamValue("2", Integer.class), is(222));
        assertThat(source.getParamValue("2", Integer[].class), is( new Integer[]{222}));

    }















    private HttpServletRequest getHttpServletRequestMock() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getParameter("1")).thenReturn("http11");
        when(request.getParameter("2")).thenReturn("http21");
        when(request.getParameterValues("1")).thenReturn(new String[]{"http11", "http12"});
        when(request.getParameterValues("2")).thenReturn(new String[]{"http21"});

        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>(){
            {
                put("1", new String[]{"http11", "http12"});
                put("2", new String[]{"http21"});
            }
        });
        return request;
    }

    private HttpServletRequest getHttpServletRequestIntegerMock() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getParameter("1")).thenReturn("111");
        when(request.getParameter("2")).thenReturn("222");
        when(request.getParameterValues("1")).thenReturn(new String[]{"111", "112"});
        when(request.getParameterValues("2")).thenReturn(new String[]{"222"});

        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>(){
            {
                put("1", new String[]{"111", "112"});
                put("2", new String[]{"222"});
            }
        });
        return request;
    }
}
