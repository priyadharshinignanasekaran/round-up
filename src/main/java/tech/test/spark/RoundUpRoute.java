package tech.test.spark;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;
import spark.Route;
import tech.test.client.StarlingClient;
import tech.test.domain.Accounts.Account;
import tech.test.domain.Amount;
import tech.test.domain.ErrorDetail;
import tech.test.domain.TransactionFeed.Transaction;
import tech.test.error.handling.ErrorHandler;
import tech.test.exception.AccountUnavailableException;
import tech.test.exception.StarlingClientException;
import tech.test.exception.InvalidArgumentException;
import tech.test.exception.SavingGoalException;
import tech.test.request.RoundUpRequest;
import tech.test.request.SavingGoalDepositRequest;
import tech.test.request.SavingGoalRequest;
import tech.test.response.RoundUpResponse;
import tech.test.response.SavingGoalResponse;
import tech.test.validation.Validator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;
import static tech.test.usecase.RoundUp.calculate;

public class RoundUpRoute implements Route {
    public static final String PATH = "/roundup/transactions-between";

    private final StarlingClient starlingClient;
    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final Validator validator;

    public RoundUpRoute(
            final StarlingClient starlingClient,
            final Gson gson,
            final ErrorHandler errorHandler,
            final Validator validator) {
        this.starlingClient = starlingClient;
        this.gson = gson;
        this.errorHandler = errorHandler;
        this.validator = validator;
    }

    @Override
    public Object handle(Request request, Response response) {
        response.header("Content-type", "application/json");
        final String minTransactionTimestamp = request.queryParams("minTransactionTimestamp");
        final String maxTransactionTimestamp = request.queryParams("maxTransactionTimestamp");
        try {
            validator.validate(request);
            final SavingGoalRequest savingGoalRequest = gson.fromJson(request.body(), RoundUpRequest.class).getSavingsGoal();

            final Account account = getGBPAccount().orElseThrow(() -> new AccountUnavailableException("Customer has no GBP account"));

            createSavingGoal(account.getAccountUid(), savingGoalRequest).ifPresent(
                    savingGoalResponse -> {
                        if (!savingGoalResponse.isSuccessFul()) {
                            throw new SavingGoalException(String.format("Unable to create or update saving goal: %s",
                                    savingGoalResponse.getErrors().stream().map(ErrorDetail::getMessages).collect(Collectors.joining(","))));
                        } else {
                            BigDecimal roundUpValue = calculate(getAllTransactionsBetween(account, minTransactionTimestamp, maxTransactionTimestamp));
                            final String transferUid = createTransferUid();
                            final String savingsGoalUid = savingGoalResponse.getSavingsGoalUid();
                            starlingClient.depositSavingGoal(
                                    account.getAccountUid(), savingsGoalUid, transferUid,
                                    new SavingGoalDepositRequest(new Amount(Currency.getInstance(account.getCurrency()), roundUpValue))).ifPresent(savingGoalDepositResponse -> {
                                if (!savingGoalDepositResponse.isSuccessFul()) {
                                    throw new SavingGoalException(String.format("Unable to deposit to saving goal: %s",
                                            savingGoalDepositResponse.getErrors().stream().map(ErrorDetail::getMessages).collect(Collectors.joining(","))));
                                } else {
                                    response.body(gson.toJson(new RoundUpResponse(
                                            transferUid,
                                            savingsGoalUid,
                                            new Amount(Currency.getInstance(savingGoalRequest.getCurrency()), roundUpValue))));
                                }

                            });
                        }
                    });

            response.status(HTTP_OK);
        } catch (JsonSyntaxException | SavingGoalException | InvalidArgumentException | AccountUnavailableException | StarlingClientException e) {
            return errorHandler.response(response, Collections.singletonList(new ErrorDetail(e.getMessage())));
        }
        return response.body();
    }

    private Optional<SavingGoalResponse> createSavingGoal(final String accountUid, final SavingGoalRequest savingGoalRequest) throws StarlingClientException {
        return starlingClient.createASavingsGoal(savingGoalRequest, accountUid);
    }

    private Optional<Account> getGBPAccount() throws StarlingClientException {
        return starlingClient.getAccounts()
                .stream()
                .filter(acc -> acc.getCurrency().equals("GBP"))
                .findFirst();
    }

    private List<Transaction> getAllTransactionsBetween(
            final Account account,
            final String minTransactionTimestamp,
            final String maxTransactionTimestamp) throws StarlingClientException {
        return starlingClient.getTransactions(
                account.getAccountUid(), account.getDefaultCategory(), minTransactionTimestamp, maxTransactionTimestamp
        );
    }

    private String createTransferUid() {
        return UUID.randomUUID().toString();
    }


}