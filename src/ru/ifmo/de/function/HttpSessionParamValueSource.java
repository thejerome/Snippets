package ru.ifmo.de.function;

import javax.servlet.http.HttpSession;

/**
 * Created by EE on 2017-03-22.
 */
public class HttpSessionParamValueSource  extends MultiParamValueSource
{
    private static class HttpSessionStorage implements Storage{

        private final HttpSession session;

        public HttpSessionStorage(HttpSession session) {
            this.session = session;
        }

        @Override
        public Object get(String paramName) {
            return session.getAttribute(paramName);
        }
    }

    public HttpSessionParamValueSource(HttpSession session) {
        super(new HttpSessionStorage(session));
    }
}
