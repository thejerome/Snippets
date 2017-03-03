package ru.ifmo.de.function;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(soutRule.getLog(), is("sum41\n"));


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
    public void CombinedValueParamValueSource() throws Exception {
        Map<String, Object> paramAndValues = newHashSet("1", "2", "3", "4").stream().collect(Collectors.toMap(i -> i, i -> (Object) (i + i + i)));
        paramAndValues.put("5", new BigDecimal("5"));
        paramAndValues.put("6", new BigDecimal(6));

        MultiParamValueSource multiParamValueSource = new MultiParamValueSource(paramAndValues);
        SingleParamValueSource<String> nullSingleParamValueSource = new SingleParamValueSource<>("6", null);
        SingleParamValueSource<String> stringSingleParamValueSource = new SingleParamValueSource<>("6", "666");
        CombinedValueParamValueSource source = new CombinedValueParamValueSource(multiParamValueSource, nullSingleParamValueSource, stringSingleParamValueSource);


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






}
