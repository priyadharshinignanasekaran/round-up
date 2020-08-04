package tech.test

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.Rule
import spock.lang.Specification
import tech.test.deserialization.GsonFactory
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static com.google.common.io.ByteStreams.toByteArray
import static java.net.HttpURLConnection.HTTP_OK
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class Fixtures extends Specification {

    def gson = GsonFactory.create()
    def bearerToken = "token"


    @Rule
    WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort())


    StubMapping stubUnauthorizedResponse(String fileName) {
        stubFor(get(urlMatching(".*"))
                .withHeader("Authorization", matching("Bearer bearerToken1"))
                .withHeader("Accept", matching("application/json"))
                .withHeader("Content-Type", matching("application/json"))
                .willReturn(aResponse()
                        .withStatus(HTTP_UNAUTHORIZED)
                        .withBody(
                                toByteArray(this.getClass().getClassLoader().getResourceAsStream(fileName))
                        )
                ))
    }

    StubMapping stubSuccessfulAccountResponse(String fileName) {
        stubFor(get(urlEqualTo("/accounts"))
                .withHeader("Authorization", matching("Bearer " + bearerToken))
                .withHeader("Accept", matching("application/json"))
                .withHeader("Content-Type", matching("application/json"))
                .willReturn(aResponse()
                        .withStatus(HTTP_OK)
                        .withBody(
                                toByteArray(this.getClass().getClassLoader().getResourceAsStream(fileName))
                        )
                ))
    }

    StubMapping stubSuccessfulTransactionFeedResponse(
            final String accountUid,
            final String categoryUid,
            final String minTransactionTimestamp,
            final String maxTransactionTimestamp,
            final String fileName) {
        stubFor(get(urlEqualTo(
                String.format("/feed/account/%s/category/%s/transactions-between?minTransactionTimestamp=%s&maxTransactionTimestamp=%s",
                        accountUid,
                        categoryUid,
                        minTransactionTimestamp,
                        maxTransactionTimestamp)))
                .withHeader("Authorization", matching("Bearer " + bearerToken))
                .withHeader("Accept", matching("application/json"))
                .withHeader("Content-Type", matching("application/json"))
                .willReturn(aResponse()
                        .withStatus(HTTP_OK)
                        .withBody(
                                toByteArray(this.getClass().getClassLoader().getResourceAsStream(fileName))
                        )
                ))
    }

    StubMapping stubEmptyTransactionResponse(
            final String accountUid,
            final String categoryUid,
            final String minTransactionTimestamp,
            final String maxTransactionTimestamp) {
        stubFor(get(urlEqualTo(
                String.format("/feed/account/%s/category/%s/transactions-between?minTransactionTimestamp=%s&maxTransactionTimestamp=%s",
                        accountUid,
                        categoryUid,
                        minTransactionTimestamp,
                        maxTransactionTimestamp)))
                .withHeader("Authorization", matching("Bearer " + bearerToken))
                .withHeader("Accept", matching("application/json"))
                .withHeader("Content-Type", matching("application/json"))
                .willReturn(aResponse()
                        .withStatus(HTTP_OK)
                        .withBody("""{"feedItems": []}""")))
    }

    StubMapping stubSuccessfulSavingsGoalCreationResponse(
            final String request,
            final String accountUid,
            final String responseFileName) {
        stubFor(put(urlEqualTo(String.format("/account/%s/savings-goals", accountUid)))
                .withHeader("Authorization", matching("Bearer " + bearerToken))
                .withHeader("Accept", matching("application/json"))
                .withRequestBody(containing(request))
                .willReturn(aResponse()
                        .withStatus(HTTP_OK)
                        .withBody(
                                toByteArray(this.getClass().getClassLoader().getResourceAsStream(responseFileName))
                        )
                ))
    }

    StubMapping stubSuccessfulSavingsGoalDepositResponse(
            final String accountUid,
            final String savingGoalUid,
            final String transferUid,
            final String request,
            final String responseFileName) {
        stubFor(put(urlEqualTo(String.format("/account/%s/savings-goals/%s/add-money/%s", accountUid, savingGoalUid, transferUid)))
                .withHeader("Authorization", matching("Bearer " + bearerToken))
                .withHeader("Accept", matching("application/json"))
                .withRequestBody(containing(request))
                .willReturn(aResponse()
                        .withStatus(HTTP_OK)
                        .withBody(
                                toByteArray(this.getClass().getClassLoader().getResourceAsStream(responseFileName))
                        )
                ))
    }
}
