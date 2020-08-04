package tech.test.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.test.domain.Accounts;
import tech.test.domain.Accounts.Account;
import tech.test.domain.ClientErrorDetail;
import tech.test.domain.ErrorDetail;
import tech.test.domain.ErrorDetails;
import tech.test.domain.ServerStyleErrorDetail;
import tech.test.domain.TransactionFeed;
import tech.test.domain.TransactionFeed.Transaction;
import tech.test.exception.StarlingClientException;
import tech.test.request.SavingGoalDepositRequest;
import tech.test.request.SavingGoalRequest;
import tech.test.response.SavingGoalDepositResponse;
import tech.test.response.SavingGoalResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static okhttp3.RequestBody.create;

public class StarlingClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CONTENT_TYPE = "application/json";
    private static final String ACCEPT = "application/json";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");
    private final String baseUrl;
    private final String token;
    private final Gson gson;

    public StarlingClient(final String baseUrl, final String token, final Gson gson) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.gson = gson;
    }

    public List<Account> getAccounts() throws StarlingClientException {
        Request request = buildGetRequest("/accounts");
        Optional<Accounts> mayBeAccounts = getResponse(request, Accounts.class);
        if (mayBeAccounts.isPresent()) {
            return mayBeAccounts.get().getAccounts();
        }
        return emptyList();
    }

    public List<Transaction> getTransactions(final String accountUid,
                                             final String categoryUid,
                                             final String minTransactionTimestamp,
                                             final String maxTransactionTimestamp) throws StarlingClientException {
        Request request = buildGetRequest(
                buildFeedUrl(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp));

        Optional<TransactionFeed> mayBeTransactionFeed = getResponse(request, TransactionFeed.class);
        if (mayBeTransactionFeed.isPresent()) {
            return mayBeTransactionFeed.get().getFeedItems();
        }
        return emptyList();
    }

    public Optional<SavingGoalResponse> createASavingsGoal(
            final SavingGoalRequest creationRequest,
            final String accountUid) throws StarlingClientException {
        Request request = buildPutRequest(buildCreateSavingsGoalUrl(accountUid),
                create(JSON, gson.toJson(creationRequest).trim()));
        return getResponse(request, SavingGoalResponse.class);
    }

    public Optional<SavingGoalDepositResponse> depositSavingGoal(
            final String accountUid,
            final String savingGoalUid,
            final String transferUid,
            final SavingGoalDepositRequest depositRequest
    ) throws StarlingClientException {
        Request request = buildPutRequest(buildDepositSavingsGoalUrl(accountUid, savingGoalUid, transferUid),
                create(JSON, gson.toJson(depositRequest).trim()));
        return getResponse(request, SavingGoalDepositResponse.class);
    }

    private <T> Optional<T> getResponse(final Request request, final Class<T> type) throws StarlingClientException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return Optional.of(gson.fromJson(response.body().string(), type));
            } else if (response.code() >= 400 && response.code() < 500) {
                handleErrors(response.body().string(), response.code());
            } else {
                throw new StarlingClientException(new ClientErrorDetail(singletonList(new ErrorDetail("Server Error")), false));
            }
        } catch (IOException e) {
            LOGGER.error("Unable to query Starling's open Api", e);
        }
        return Optional.empty();
    }

    private void handleErrors(String responseBody, int code) {
        Map<String, Object> errorResponse = gson.fromJson(responseBody, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        ErrorDetails errorDetail;
        if (errorResponse.containsKey("errors")) {
            errorDetail = gson.fromJson(responseBody, new TypeToken<ClientErrorDetail>() {
            }.getType());
        } else {
            errorDetail = gson.fromJson(responseBody, new TypeToken<ServerStyleErrorDetail>() {
            }.getType());
        }
        LOGGER.error("Client error -->  response code: {},  response body: {}", code, errorDetail);
        throw new StarlingClientException(errorDetail);
    }

    private String buildFeedUrl(
            final String accountUid,
            final String categoryUid,
            final String minTransactionTimestamp,
            final String maxTransactionTimestamp) {
        return format(
                "/feed/account/%s/category/%s/transactions-between?minTransactionTimestamp=%s&" +
                        "maxTransactionTimestamp=%s",
                accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
    }

    private String buildCreateSavingsGoalUrl(final String accountUid) {
        return String.format("/account/%s/savings-goals", accountUid);
    }

    private String buildDepositSavingsGoalUrl(
            final String accountUid,
            final String savingGoalUid,
            final String transferUid) {
        return String.format("/account/%s/savings-goals/%s/add-money/%s", accountUid, savingGoalUid, transferUid);
    }

    private Request buildGetRequest(final String path) {
        return init(path).get().build();
    }

    private Request buildPutRequest(final String path, final RequestBody requestBody) {
        return init(path).put(requestBody).build();
    }

    private Request.Builder init(final String path) {
        return new Request.Builder()
                .url(baseUrl.concat(path))
                .addHeader("Accept", ACCEPT)
                .addHeader("Content-Type", CONTENT_TYPE)
                .addHeader("Authorization", "Bearer " + token);
    }
}
