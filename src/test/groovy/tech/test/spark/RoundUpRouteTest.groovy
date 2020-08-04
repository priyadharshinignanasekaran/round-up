package tech.test.spark

import com.google.gson.stream.JsonReader
import spark.Request
import spark.Response
import spock.lang.Shared
import spock.lang.Specification
import tech.test.client.StarlingClient
import tech.test.deserialization.GsonFactory
import tech.test.domain.Accounts
import tech.test.domain.ErrorDetail
import tech.test.domain.TransactionFeed
import tech.test.error.handling.ErrorHandler
import tech.test.response.SavingGoalDepositResponse
import tech.test.response.SavingGoalResponse
import tech.test.usecase.RoundUp
import tech.test.validation.Validator
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_OK
import static java.util.Collections.emptyList

class RoundUpRouteTest extends Specification {
    @Shared
            INVALID_JSON = "{"
    def request = Mock(Request)
    def response = Mock(Response)
    def starlingClient = Mock(StarlingClient)
    def gson = GsonFactory.create()
    def errorHandler = new ErrorHandler(gson)
    def validator = new Validator(errorHandler)

    def roundUpRoute = new RoundUpRoute(starlingClient, gson, errorHandler, validator)

    def 'returns response with http BAD_REQUEST(400) status when request contains an invalid query paramters'() {
        given:
        request.body() >> INVALID_JSON
        request.queryParams("minTransactionTimestamp") >> "2020-08-01T00:00:000Z"
        request.queryParams("maxTransactionTimestamp") >> "2020-08-02T00:00:000Z"
        validator.validate(_) >> false
        when:
        roundUpRoute.handle(request, response)

        then:
        1 * response.status(HTTP_BAD_REQUEST)
    }

    def 'returns response with http BAD_REQUEST(400) status when request contains an invalid json'() {
        given:
        request.body() >> INVALID_JSON
        request.queryParams("minTransactionTimestamp") >> "2020-08-01T00:00:00.000Z"
        request.queryParams("maxTransactionTimestamp") >> "2020-08-08T00:00:00.000Z"

        when:
        roundUpRoute.handle(request, response)

        then:
        1 * response.status(HTTP_BAD_REQUEST)
    }

    def 'throws AccountUnavailableException with HTTP_BAD_REQUEST (400) when customer has no accounts'() {
        given:
        request.body() >> """{
                                  "savingsGoal": {
                                    "name": "Trip to Paris",
                                    "currency": "GBP",
                                    "target": {
                                      "currency": "GBP",
                                      "minorUnits": 123456
                                    },
                                    "base64EncodedPhoto": "string"
                                  }
                            }"""

        request.queryParams("minTransactionTimestamp") >> "2020-08-01T00:00:00.000Z"
        request.queryParams("maxTransactionTimestamp") >> "2020-08-08T00:00:00.000Z"
        starlingClient.accounts >> emptyList()

        when:
        roundUpRoute.handle(request, response)

        then:
        1 * response.status(HTTP_BAD_REQUEST)
    }

    def 'throws SavingGoalException with http BAD_REQUEST(400) status when saving goal creation is unsuccessful and return errors'() {
        given:


        request.body() >> """{
                                  "savingsGoal": {
                                  "name": "Trip to Paris",
                                  "currency": "GBP",
                                  "target": {
                                    "currency": "GBP",
                                    "minorUnits": 123456
                                  },
                                  "base64EncodedPhoto": "string"
                                }
                            }"""
        request.queryParams("minTransactionTimestamp") >> "2020-08-01T00:00:00.000Z"
        request.queryParams("maxTransactionTimestamp") >> "2020-08-08T00:00:00.000Z"

        def savingGoalResponse = Mock(SavingGoalResponse)
        savingGoalResponse.successFul >> false
        savingGoalResponse.errors >> [new ErrorDetail("went wrong")]

        def account = gson.fromJson(new JsonReader(new FileReader("src/test/resources/account-response.json")), Accounts)
        starlingClient.accounts >> account.getAccounts()
        starlingClient.createASavingsGoal(_, _) >> Optional.of(savingGoalResponse)
        when:
        roundUpRoute.handle(request, response)

        then:
        1 * response.status(HTTP_BAD_REQUEST)
    }

    def 'throws SavingGoalException with http BAD_REQUEST(400) status when deposit to saving goal is unsuccessful and return errors'() {
        given:
        request.body() >> """{
                                  "savingsGoal": {
                                  "name": "Trip to Paris",
                                  "currency": "GBP",
                                  "target": {
                                    "currency": "GBP",
                                    "minorUnits": 123456
                                  },
                                  "base64EncodedPhoto": "string"
                                }
                            }"""
        request.queryParams("minTransactionTimestamp") >> "2020-08-01T00:00:00.000Z"
        request.queryParams("maxTransactionTimestamp") >> "2020-08-08T00:00:00.000Z"

        def savingGoalResponse = Mock(SavingGoalResponse)
        savingGoalResponse.savingsGoalUid >> UUID.randomUUID()
        savingGoalResponse.successFul >> true

        def savingGoalDepositResponse = Mock(SavingGoalDepositResponse)
        savingGoalDepositResponse.successFul >> false
        savingGoalDepositResponse.errors >> [new ErrorDetail("went wrong")]

        def account = gson.fromJson(new JsonReader(new FileReader("src/test/resources/account-response.json")), Accounts)
        def transactions = gson.fromJson(new JsonReader(new FileReader("src/test/resources/transaction_feed-response.json")), TransactionFeed).feedItems

        starlingClient.accounts >> account.getAccounts()
        starlingClient.createASavingsGoal(_, _) >> Optional.of(savingGoalResponse)
        starlingClient.getTransactions(_, _, _, _) >> transactions
        RoundUp.calculate(_) >> BigDecimal.valueOf(154)
        starlingClient.depositSavingGoal(_, _, _, _) >> Optional.of(savingGoalDepositResponse)

        when:
        roundUpRoute.handle(request, response)

        then:
        1 * response.status(HTTP_BAD_REQUEST)
    }

    def 'can successfully deposit to saving goal and return HTTP 200'() {
        given:
        request.body() >> """{
                                  "savingsGoal": {
                                  "name": "Trip to Paris",
                                  "currency": "GBP",
                                  "target": {
                                    "currency": "GBP",
                                    "minorUnits": 123456
                                  },
                                  "base64EncodedPhoto": "string"
                                }
                            }"""
        request.queryParams("minTransactionTimestamp") >> "2020-08-01T00:00:00.000Z"
        request.queryParams("maxTransactionTimestamp") >> "2020-08-08T00:00:00.000Z"

        def savingGoalResponse = Mock(SavingGoalResponse)
        savingGoalResponse.savingsGoalUid >> UUID.randomUUID()
        savingGoalResponse.successFul >> true

        def savingGoalDepositResponse = Mock(SavingGoalDepositResponse)
        savingGoalDepositResponse.successFul >> true

        def account = gson.fromJson(new JsonReader(new FileReader("src/test/resources/account-response.json")), Accounts)
        def transactions = gson.fromJson(new JsonReader(new FileReader("src/test/resources/transaction_feed-response.json")), TransactionFeed).feedItems

        starlingClient.accounts >> account.getAccounts()
        starlingClient.createASavingsGoal(_, _) >> Optional.of(savingGoalResponse)
        starlingClient.getTransactions(_, _, _, _) >> transactions
        RoundUp.calculate(_) >> BigDecimal.valueOf(154)
        starlingClient.depositSavingGoal(_, _, _, _) >> Optional.of(savingGoalDepositResponse)

        when:
        roundUpRoute.handle(request, response)

        then:
        1 * response.status(HTTP_OK)
    }

}
