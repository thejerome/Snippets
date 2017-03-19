package ru.ifmo.de.function.classconverters;

import com.google.common.collect.Lists;
import ru.ifmo.de.function.ClassConverter;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by EE on 2017-03-16.
 */
public class StringArrayToTimestampClassConverter
                extends AbstractStringArrayToObjectOfFirstElementClassConverter<Timestamp> {

    private List<SimpleDateFormat> formats = newArrayList(
        new SimpleDateFormat(),
        new SimpleDateFormat("dd.MM.yy"),
        new SimpleDateFormat("yyyy.MM.dd"),
        new SimpleDateFormat("yyyy-MM-dd"),
        new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"),
        new SimpleDateFormat("yyyy.MM.dd H:mm:ss"),
        new SimpleDateFormat("dd.MM.yyyyy HH:mm:ss")
    );
    private Function<SimpleDateFormat, Date> dateFormatsTryFunction;


    @Override
    Function<String, Timestamp> getSingleElementMapper() {
        return new Function<String, Timestamp>() {
            @Override
            public Timestamp apply(String s) {
                try {
                    return Timestamp.valueOf(s);
                } catch (NumberFormatException nfe){
                    dateFormatsTryFunction = (format) -> {
                        try {
                            return format.parse(s);
                        } catch (ParseException pe) {
                            return null;
                        }
                    };

                    return formats.stream().map(
                        dateFormatsTryFunction
                    )
                    .filter(d -> d != null)
                    .map(Date::getTime)
                    .map(Timestamp::new)
                    .findFirst().orElse(null);
                }
            }
        };
    }
}
