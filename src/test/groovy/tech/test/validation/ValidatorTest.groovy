package tech.test.validation

import java.time.ZonedDateTime
import spark.Request
import spock.lang.Specification
import tech.test.exception.InvalidArgumentException

class ValidatorTest extends Specification {

    def 'test should return true when there are no validation errors'() {
        given:
        def maxTransactionTimestamp =  ZonedDateTime.now().format(Validator.formatter)
        def minTransactionTimestamp = ZonedDateTime.now().minusDays(8).format(Validator.formatter)
        Request request = Mock(Request)
        request.queryParams('minTransactionTimestamp') >> minTransactionTimestamp
        request.queryParams('maxTransactionTimestamp') >> maxTransactionTimestamp
        request.body() >> null

        when:
        Validator.validate(request)

        then:
        true
    }

    def 'test DateTime should be ZonedDateTime with format yyyy-MM-dd\'T\'HH:mm:ss.SSSz'() {
        given:
        Request request = Mock(Request)
        request.queryParams('minTransactionTimestamp') >> '2010/10/12'
        request.queryParams('maxTransactionTimestamp') >> '2010/10/12'
        request.body() >> null

        when:
        Validator.validate(request)

        then:
        InvalidArgumentException ex = thrown()
        ex.message == 'supported format for timestamps is yyyy-MM-dd\'T\'HH:mm:ss.SSSz'
    }

    def 'test date range provided should be for a week'() {
        given:
        Request request = Mock(Request)
        request.queryParams('minTransactionTimestamp') >> ZonedDateTime.now().minusDays(10).format(Validator.formatter)
        request.queryParams('maxTransactionTimestamp') >> ZonedDateTime.now().format(Validator.formatter)
        request.body() >> null

        when:
        Validator.validate(request)

        then:
        InvalidArgumentException ex = thrown()
        ex.message == 'DateTime range is for more than a week. Please Retry with a week\'s range'
    }

    def 'test minTransactionTimestamp should be before maxTransactionTimestamp'() {
        given:
        def minTransactionTimestamp =  ZonedDateTime.now().format(Validator.formatter)
        def maxTransactionTimestamp = ZonedDateTime.now().minusDays(7).format(Validator.formatter)
        Request request = Mock(Request)
        request.queryParams('minTransactionTimestamp') >> minTransactionTimestamp
        request.queryParams('maxTransactionTimestamp') >> maxTransactionTimestamp
        request.body() >> null

        when:
        Validator.validate(request)

        then:
        InvalidArgumentException ex = thrown()
        ex.message == String.format('minTransactionTimestamp %s should be before %s', minTransactionTimestamp, maxTransactionTimestamp)

    }
}
