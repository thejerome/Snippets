package ru.ifmo.de.function.classconverters;

import ru.ifmo.de.function.ClassConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toCollection;

/**
 * Created by EE on 2017-03-16.
 */
public abstract class AbstractStringArrayToObjectOfFirstElementClassConverter<T> implements ClassConverter<String[], T> {

    @Override
    public T convert(String[] input) {
        checkNotNull(input);
        checkElementIndex(0, input.length);
        return getSingleElementMapper().apply(input[0]);
    }

    abstract Function<String, T> getSingleElementMapper() ;
}
