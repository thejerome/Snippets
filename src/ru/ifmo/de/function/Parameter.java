package ru.ifmo.de.function;

import static com.google.common.base.Preconditions.checkNotNull;

import static ru.ifmo.de.function.Parameter.Direction.IN;
import static ru.ifmo.de.function.Parameter.Direction.INOUT;
import static ru.ifmo.de.function.Parameter.Direction.OUT;

/**
 * Created by efimchick on 27.03.17.
 */
public class Parameter<T> {
    protected final String name;
    protected final Class<T> clazz;
    protected final Direction direction;

    enum Direction{
        IN, OUT, INOUT
    }

    public Parameter(String name, Class<T> clazz, Direction direction) {
        checkNotNull(name);
        checkNotNull(clazz);
        checkNotNull(direction);
        this.name = name;
        this.clazz = clazz;
        this.direction = direction;
    }

    public boolean isInput() {
        return direction == IN || direction == INOUT;
    }

    public boolean isOutput() {
        return direction == OUT || direction == INOUT;
    }


    public T lookForValue(ParamValueSource paramValueSource) {
        checkNotNull(paramValueSource);
        if (clazz == null) return (T) paramValueSource.getParamValue(name);
        else return paramValueSource.getParamValue(name, clazz);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", clazz=" + clazz +
                ", direction=" + direction +
                '}';
    }
}
