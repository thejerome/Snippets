package ru.ifmo.de.function.classconverters;

import ru.ifmo.de.function.ClassConverter;

import java.util.StringJoiner;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToStringClassConverter implements ClassConverter<String[], String> {
    @Override
    public String convert(String[] input) {
        StringJoiner joiner = new StringJoiner(",");
        for (String s : input) {
            joiner.add(s);
        }
        return joiner.toString();
    }
}
