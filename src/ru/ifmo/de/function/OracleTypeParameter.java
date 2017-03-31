package ru.ifmo.de.function;

import com.google.common.collect.ImmutableMap;
import oracle.jdbc.driver.OracleCallableStatement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by efimchick on 27.03.17.
 */
public abstract class OracleTypeParameter<T> extends Parameter<T> {
    protected final int oracleType;
    protected final int position;

    protected OracleTypeParameter(String name, Class<T> valueClass, Direction direction, int oracleType, int position) {
        super(name, valueClass, direction);
        this.oracleType = oracleType;
        this.position = position;
    }

    public boolean callIsNeededAfterCallableStatementExecuted(){
        return false;
    }
    public boolean afterCallableStatementExecuted(ParamValueSource paramValueSource, OracleCallableStatement cs){
        return true;
    };

    public abstract boolean prepareCallableStatement(ParamValueSource valueSource, OracleCallableStatement cs);
    public T getValue(OracleCallableStatement cs){
        checkNotNull(cs);
        T value = null;
        if (isOutput()) {
            try {
                value = (T) getters.get(clazz).getValueFromCS(cs, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    };

    @Override
    public String toString() {
        return super.toString() + "||{" +
                "oracleType=" + oracleType +
                ", position=" + position +
                "} " + super.toString();
    }

    protected boolean inAndOutCommonPreparing(ParamValueSource valueSource, OracleCallableStatement cs) {
        boolean res = inCommonPreparingCS(valueSource, cs);
        res = outCommonPreparingCS(cs);
        return res;
    }

    protected boolean inCommonPreparingCS(ParamValueSource valueSource, OracleCallableStatement cs) {
        checkNotNull(cs);
        boolean res = false;
        if (isInput()){
            try{
                T value = lookForValue(valueSource);

                System.out.println("setting " + value + " to " + name + ":" + position);
                setters.get(clazz).setValueToCS(cs, position, value);

                if(value != null){
                    res = true;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return res;
    }

    protected boolean outCommonPreparingCS(OracleCallableStatement cs) {
        boolean res = false;
        checkNotNull(cs);
        if (isOutput()){
            try {
                System.out.println("registring " + oracleType + " at " + position + " for " + name);
                cs.registerOutParameter(position, oracleType);
                res = true;
            } catch (SQLException e) {
                res = false;
            }
        }
        return res;
    }


    public boolean isRetValParameter() {
        return name.equals("retVal");
    }

    private static Map<Class, CSValueSetter> setters = ImmutableMap.<Class, CSValueSetter>builder()
            .put(String.class, (cs, pos, val) -> cs.setString(pos, (String) val))
            .put(BigDecimal.class, (cs, pos, val) -> cs.setBigDecimal(pos, (BigDecimal) val))
            .build();

    private static Map<Class, CSValueGetter> getters = ImmutableMap.<Class, CSValueGetter>builder()
            .put(String.class, (cs, pos) -> cs.getString(pos))
            .put(BigDecimal.class, (cs, pos) -> cs.getBigDecimal(pos))
            .build();

    private interface CSValueSetter<V>{
        void setValueToCS(OracleCallableStatement cs, int position, V value) throws SQLException;
    }

    private interface CSValueGetter<V>{
        V getValueFromCS(OracleCallableStatement cs, int position) throws SQLException;
    }

}
