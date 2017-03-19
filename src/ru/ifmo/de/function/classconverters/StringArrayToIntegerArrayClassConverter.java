package ru.ifmo.de.function.classconverters;

import ru.ifmo.de.function.ClassConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToIntegerArrayClassConverter extends AbstractStringArrayToArrayClassConverter<Integer> {
    @Override
    Function<String, Integer> getSingleElementMapper() {
        return Integer::valueOf;
    }

    @Override
    Integer[] createConsumerArray(int length) {
        return new Integer[length];
    }
}
