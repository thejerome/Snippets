package ru.ifmo.de.function.classconverters;

import java.util.function.Function;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToDoubleClassConverter
                extends AbstractStringArrayToObjectOfFirstElementClassConverter<Double>{
    @Override
    Function<String, Double> getSingleElementMapper() {
        return Double::valueOf;
    }
}
