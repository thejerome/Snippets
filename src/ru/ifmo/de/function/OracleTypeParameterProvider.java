package ru.ifmo.de.function;

import com.google.common.collect.ImmutableList;
import dlc.servlet.RecParam;
import javassist.bytecode.analysis.Type;

import java.util.List;

/**
 * Created by efimchick on 31.03.17.
 */
public class OracleTypeParameterProvider {
    private final List<RecParam> recParams;

    public OracleTypeParameterProvider(List<RecParam> recParams) {
        this.recParams = ImmutableList.copyOf(recParams);
    }

    public List<OracleTypeParameter> getParameters(){
        boolean addOneToPosition = recParams.stream().anyMatch(p -> p.name.equals("func_ret"));
        return null;
    }
}
