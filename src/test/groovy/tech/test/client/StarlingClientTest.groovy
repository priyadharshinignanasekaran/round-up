package tech.test.client

import com.google.gson.stream.JsonReader
import java.time.ZoneId
import tech.test.Fixtures
import tech.test.domain.Accounts
import tech.test.domain.TransactionFeed
import tech.test.exception.StarlingClientException
import tech.test.request.SavingGoalDepositRequest
import tech.test.request.SavingGoalRequest
import tech.test.response.SavingGoalDepositResponse
import tech.test.response.SavingGoalResponse
import static java.time.ZonedDateTime.now
import static java.util.Collections.emptyList
import static tech.test.domain.TransactionFeed.Transaction.Direction.OUT

class StarlingClientTest extends Fixtures {

    def 'can successfully retrieve existing account details'() {
        given:
        StarlingClient client = new StarlingClient("http://localhost:${wireMockRule.port()}", bearerToken, gson)
        stubSuccessfulAccountResponse("account-response.json")

        when:
        List<Accounts.Account> accounts = client.getAccounts()

        then:
        accounts.size() > 0
        with(accounts.get(0)) {
            accountUid == "5aaecddb-e83c-48f3-9853-85e7f5666da0"
            defaultCategory == "2623afb1-cb82-4042-8714-c2fcd8852500"
            currency == "GBP"
            createdAt == "2020-07-31T20:34:31.719Z"
        }
    }

    def 'can handle empty transactions for given time range'() {
        given:
        StarlingClient client = new StarlingClient("http://localhost:${wireMockRule.port()}", bearerToken, gson)
        String accountUid = "5aaecddb-e83c-48f3-9853-85e7f5666da0"
        String categoryUid = "2623afb1-cb82-4042-8714-c2fcd8852500"
        String minTransactionTimestamp = now(ZoneId.of("UTC")).minusDays(1)
        String maxTransactionTimestamp = now(ZoneId.of("UTC"))
        stubEmptyTransactionResponse(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp)

        when:
        List<TransactionFeed.Transaction> transactions =
                client.getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp)

        then:
        transactions.size() == 0
    }

    def 'can successfully retrieve existing transaction details'() {
        given:
        StarlingClient client = new StarlingClient("http://localhost:${wireMockRule.port()}", bearerToken, gson)

        String accountUid = "5aaecddb-e83c-48f3-9853-85e7f5666da0"
        String categoryUid = "2623afb1-cb82-4042-8714-c2fcd8852500"
        String minTransactionTimestamp = now().minusDays(1).toString()
        String maxTransactionTimestamp = now().toString()
        stubSuccessfulTransactionFeedResponse(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp, "transaction_feed-response.json")

        when:
        List<TransactionFeed.Transaction> transactions =
                client.getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp)

        then:
        transactions.size() > 0
        with(transactions.get(0)) {
            categoryUid == "2623afb1-cb82-4042-8714-c2fcd8852500"
            direction == OUT.value
        }
    }

    def 'can successfully create new saving goal'() {
        given:
        StarlingClient client = new StarlingClient("http://localhost:${wireMockRule.port()}", bearerToken, gson)
        String accountUid = "5aaecddb-e83c-48f3-9853-85e7f5666da0"
        SavingGoalRequest request =
                gson.fromJson(
                        new JsonReader(new FileReader("src/test/resources/savings_goals-request.json")),
                        SavingGoalRequest.class)
        stubSuccessfulSavingsGoalCreationResponse(
                gson.toJson(request).trim(),
                accountUid,
                "savings_goals-response.json")

        when:
        Optional<SavingGoalResponse> savingsGoal = client.createASavingsGoal(request, accountUid)

        then:
        savingsGoal.isPresent()
        with(savingsGoal.get()) {
            savingsGoalUid == "77887788-7788-7788-7788-778877887788"
            errors == emptyList()
        }
    }

    def 'can successfully deposit to an existing saving goal'() {
        given:
        StarlingClient client = new StarlingClient("http://localhost:${wireMockRule.port()}", bearerToken, gson)

        String accountUid = "5aaecddb-e83c-48f3-9853-85e7f5666da0"
        String savingsGoalUid = "77887788-7788-7788-7788-778877887788"
        String transferUid = "88998899-8899-8899-8899-889988998899"

        SavingGoalDepositRequest request =
                gson.fromJson(
                        new JsonReader(new FileReader("src/test/resources/savings_goals-deposit-request.json")),
                        SavingGoalDepositRequest.class)
        stubSuccessfulSavingsGoalDepositResponse(
                accountUid,
                savingsGoalUid,
                transferUid,
                gson.toJson(request).trim(),
                "savings_goals-deposit-response.json")

        when:
        Optional<SavingGoalDepositResponse> mayBeResponse =
                client.depositSavingGoal(accountUid, savingsGoalUid, transferUid, request)

        then:
        mayBeResponse != Optional.empty()
        with(mayBeResponse.get()) {
            getTransferUid() == "88998899-8899-8899-8899-889988998899"
            success
            getErrors() == emptyList()

        }
    }

    def 'when request is unauthorized StarlingClientException is thrown'() {
        given:
        StarlingClient client = new StarlingClient("http://localhost:${wireMockRule.port()}", "bearerToken1", gson)
        stubUnauthorizedResponse("unauthorized_response.json")
        when:
        client.getAccounts()

        then:
        StarlingClientException ex = thrown()
        // Alternative syntax: def ex = thrown(InvalidDeviceException)
        ex.message == """{"errors":[{"message":"You are not authorised to access the requested data"}],"success":false}"""
    }

}
