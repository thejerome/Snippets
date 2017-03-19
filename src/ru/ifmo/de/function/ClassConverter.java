package ru.ifmo.de.function;

/**
 * Created by EE on 2017-03-16.
 */
public interface ClassConverter<I, O> {
    O convert(I input);
}
