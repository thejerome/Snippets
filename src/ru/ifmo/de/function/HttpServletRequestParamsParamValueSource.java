package ru.ifmo.de.function;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by EE on 2017-03-15.
 */
public class HttpServletRequestParamsParamValueSource extends MultiParamValueSource implements ParamValueSource{

    public HttpServletRequestParamsParamValueSource(HttpServletRequest request) {
        super(request.getParameterMap());
    }


}
