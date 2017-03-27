package ru.ifmo.de.function;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by efimchick on 02.03.17.
 */
public class SoutParamValueAppFunction implements AppFunction {
    private final List<Parameter> params;

    public SoutParamValueAppFunction(Collection<Parameter> params) {
        this.params = Lists.newArrayList(params);
    }

    public SoutParamValueAppFunction(Parameter param) {
        this(Lists.newArrayList(param));
    }

    @Override
    public AppFunctionResult call(ParamValueSource paramValueSource) {
        params.stream().filter(p -> p.isInput())
                .map(p -> p.lookForValue(paramValueSource))
                .forEach(System.out::println);
        return new SuccessfulEmptyAppFunctionResult();
    }
}
