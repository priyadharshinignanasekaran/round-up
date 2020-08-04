package tech.test.validation;

import spark.Request;
import tech.test.exception.InvalidArgumentException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import static java.time.ZonedDateTime.parse;
import static java.time.temporal.ChronoUnit.DAYS;

public class Validator {
    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT);

    public static boolean validate(Request request) {
        final String minTransactionTimestamp = request.queryParams("minTransactionTimestamp");
        final String maxTransactionTimestamp = request.queryParams("maxTransactionTimestamp");

        if (!isValidDateTime(minTransactionTimestamp) ||
                !isValidDateTime(maxTransactionTimestamp)) {
            throw new InvalidArgumentException(String.format("supported format for timestamps is %s", FORMAT));
        }



        if (!parse(minTransactionTimestamp).isBefore(parse(maxTransactionTimestamp))) {
            throw new InvalidArgumentException(
                    String.format("minTransactionTimestamp %s should be before %s",
                            minTransactionTimestamp, maxTransactionTimestamp));
        }
        if (7 != zonedDateTimeDifference(parse(minTransactionTimestamp), parse(maxTransactionTimestamp), DAYS)) {
            throw new InvalidArgumentException("DateTime range is for more than a week. Please Retry with a week's range");
        }
        return true;
    }

    private static boolean isValidDateTime(final String aDateTime) {
        try {
            parse(aDateTime).format(formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static long zonedDateTimeDifference(final ZonedDateTime d1, final ZonedDateTime d2, final ChronoUnit unit) {
        return unit.between(d1, d2);
    }
}
