package ru.ifmo.de.function.classconverters;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToBigDecimalArrayClassConverter extends AbstractStringArrayToArrayClassConverter<BigDecimal> {
    @Override
    Function<String, BigDecimal> getSingleElementMapper() {
        return BigDecimal::new;
    }

    @Override
    BigDecimal[] createConsumerArray(int length) {
        return new BigDecimal[length];
    }
}
