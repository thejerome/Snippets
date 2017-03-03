package ru.ifmo.de.function;

/**
 * Created by efimchick on 02.03.17.
 */
public class SoutParamValueAppFunction implements AppFunction {
    private String paramName;

    public SoutParamValueAppFunction(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public AppFunctionResult call(ParamValueSource paramValueSource) {
        System.out.println(paramValueSource.getParamValue(paramName));
        return new SuccessfulEmptyAppFunctionResult();
    }
}
