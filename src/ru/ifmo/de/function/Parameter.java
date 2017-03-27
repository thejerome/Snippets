package ru.ifmo.de.function;

import static com.google.common.base.Preconditions.checkNotNull;

import static ru.ifmo.de.function.Parameter.Direction.IN;
import static ru.ifmo.de.function.Parameter.Direction.INOUT;
import static ru.ifmo.de.function.Parameter.Direction.OUT;

/**
 * Created by efimchick on 27.03.17.
 */
public class Parameter<T> {
    private final String name;
    private final Class<T> clazz;
    private final Direction direction;

    enum Direction{
        IN, OUT, INOUT
    }

    public Parameter(String name, Class<T> clazz, Direction direction) {
        checkNotNull(name);
        checkNotNull(direction);
        this.name = name;
        this.clazz = clazz;
        this.direction = direction;
    }

    public Parameter(String name, Direction direction) {
        this(name, null, direction);
    }

    public boolean isInput() {
        return direction == IN || direction == INOUT;
    }

    public boolean isOutput() {
        return direction == OUT || direction == INOUT;
    }


    public T lookForValue(ParamValueSource paramValueSource) {
        if (clazz == null) return (T) paramValueSource.getParamValue(name);
        else return paramValueSource.getParamValue(name, clazz);
    }

}
