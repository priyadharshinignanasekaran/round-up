package tech.test.usecase

import groovy.json.JsonBuilder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import tech.test.Fixtures
import tech.test.domain.TransactionFeed

class RoundUpTest extends Fixtures {

    def 'calculate roundup only on OUT transaction feed entries'() {
        given:
        def feed1 = feedItem('OUT', 'SAVING', 'INTERNAL_TRANSFER')
        def feed2 = feedItem('OUT', 'PAYMENTS', 'FASTER_PAYMENTS_OUT')
        def feed3 = feedItem('OUT', 'PAYMENTS', 'FASTER_PAYMENTS_OUT')
        def feed4 = feedItem('IN', 'INCOME', 'FASTER_PAYMENTS_IN')

        TransactionFeed transactionFeedResponse = gson.fromJson("""{"feedItems": [ ${feed1}, ${feed2}, ${feed3}, ${feed4} ]}""", TransactionFeed)
        when:
        BigDecimal roundUpAmount = RoundUp.calculate(transactionFeedResponse.feedItems)

        then:
        roundUpAmount == BigDecimal.valueOf(154)
    }

    def 'return BigDecimal.ZERO when feed entry is empty'() {
        given:
        TransactionFeed transactionFeedResponse = gson.fromJson("""{"feedItems": []}""", TransactionFeed)

        when:
        BigDecimal roundUpAmount = RoundUp.calculate(transactionFeedResponse.feedItems)

        then:
        roundUpAmount == BigDecimal.ZERO
    }

    def 'return BigDecimal.ZERO when feed entry only has IN items'() {
        given:
        def feed = feedItem('IN', 'INCOME', 'FASTER_PAYMENTS_IN')
        TransactionFeed transactionFeedResponse = gson.fromJson("""{"feedItems": [ ${feed} ]}""", TransactionFeed)

        when:
        BigDecimal roundUpAmount = RoundUp.calculate(transactionFeedResponse.feedItems)

        then:
        roundUpAmount == BigDecimal.ZERO
    }

    def 'return BigDecimal.ZERO when feed entry only has OUT items but with spending category saving and source internal'() {
        given:
        def feed = feedItem('OUT', 'SAVING', 'INTERNAL_TRANSFER')
        TransactionFeed transactionFeedResponse = gson.fromJson("""{"feedItems": [ ${feed} ]}""", TransactionFeed)

        when:
        BigDecimal roundUpAmount = RoundUp.calculate(transactionFeedResponse.feedItems)

        then:
        roundUpAmount == BigDecimal.ZERO
    }

    def feedItem(def directionVal = 'OUT', def spendingCategoryVal = 'PAYMENTS', def sourceVal = 'FASTER_PAYMENTS_OUT') {

        def builder = new JsonBuilder()

        builder {
            feedItemUid randomUid()
            categoryUid randomUid()
            amount {
                currency 'GBP'
                minorUnits 123
            }
            sourceAmount {
                currency 'GBP'
                minorUnits 123
            }
            direction directionVal
            updatedAt aDateTime()
            transactionTime aDateTime()
            settlementTime aDateTime()
            source sourceVal
            status 'status'
            counterPartyType 'counterPartyType'
            counterPartyUid randomUid()
            counterPartyName 'counterPartyName'
            counterPartySubEntityUid randomUid()
            counterPartySubEntityName 'counterPartySubEntityName'
            counterPartySubEntityIdentifier 'counterPartySubEntityIdentifier'
            counterPartySubEntitySubIdentifier 'counterPartySubEntitySubIdentifier'
            reference 'reference'
            country 'country'
            spendingCategory spendingCategoryVal
            hasAttachments false
        }
        builder.toPrettyString()
    }

    void aDateTime(def date = ZonedDateTime.now()) {
        def FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSz"
        def formatter = DateTimeFormatter.ofPattern(FORMAT)
        date.format(formatter)
    }

    def randomUid() {
        UUID.randomUUID()
    }
}
