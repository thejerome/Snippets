package ru.ifmo.de.function.recparam;

import dlc.servlet.RecParam;

/**
 * Created by efimchick on 31.03.17.
 */
public class RetValRecParamFilter implements RecParamFilter {
    @Override
    public boolean accept(RecParam rp) {
        return rp.name.equals("func_ret") && rp.type.equals("NUMBER") && rp.position == 0 && rp.in_out.equals("OUT");
    }
}
