package ru.ifmo.de.function.classconverters;

import com.google.common.base.Preconditions;
import ru.ifmo.de.function.ClassConverter;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToIntegerClassConverter
                extends AbstractStringArrayToObjectOfFirstElementClassConverter<Integer>{
    @Override
    Function<String, Integer> getSingleElementMapper() {
        return Integer::valueOf;
    }
}
