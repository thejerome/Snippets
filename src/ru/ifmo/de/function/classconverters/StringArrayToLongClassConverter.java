package ru.ifmo.de.function.classconverters;

import java.util.function.Function;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToLongClassConverter
                extends AbstractStringArrayToObjectOfFirstElementClassConverter<Long>{
    @Override
    Function<String, Long> getSingleElementMapper() {
        return Long::valueOf;
    }
}
