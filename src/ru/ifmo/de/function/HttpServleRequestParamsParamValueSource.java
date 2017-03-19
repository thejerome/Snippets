package ru.ifmo.de.function;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by EE on 2017-03-15.
 */
public class HttpServleRequestParamsParamValueSource extends MultiParamValueSource implements ParamValueSource{

    public HttpServleRequestParamsParamValueSource(HttpServletRequest request) {
        super(request.getParameterMap());
    }


}
