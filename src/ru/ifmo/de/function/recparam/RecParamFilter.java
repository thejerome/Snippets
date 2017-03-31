package ru.ifmo.de.function.recparam;

import dlc.servlet.RecParam;

/**
 * Created by efimchick on 31.03.17.
 */
public interface RecParamFilter {
    boolean accept(RecParam recParam);
}
