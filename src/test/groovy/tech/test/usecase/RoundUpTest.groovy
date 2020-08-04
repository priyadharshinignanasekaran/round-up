package tech.test.usecase

import tech.test.Fixtures
import tech.test.domain.TransactionFeed

class RoundUpTest extends Fixtures {

    def 'calculate roundup only on OUT transaction feed entries'() {
        given:
        TransactionFeed transactionFeedResponse =
                gson.fromJson("""{
          "feedItems": [
            {
              "feedItemUid": "f12f8373-9aab-4cef-b7ef-3bc7dd624973",
              "categoryUid": "2623afb1-cb82-4042-8714-c2fcd8852500",
              "amount": {
                "currency": "GBP",
                "minorUnits": 553
              },
              "sourceAmount": {
                "currency": "GBP",
                "minorUnits": 553
              },
              "direction": "OUT",
              "updatedAt": "2020-07-31T21:03:14.892Z",
              "transactionTime": "2020-07-31T21:03:14.418Z",
              "settlementTime": "2020-07-31T21:03:14.839Z",
              "source": "FASTER_PAYMENTS_OUT",
              "status": "SETTLED",
              "counterPartyType": "PAYEE",
              "counterPartyUid": "829bdbe8-3c7e-4852-be8a-1fc3d6ebbc55",
              "counterPartyName": "Mickey Mouse",
              "counterPartySubEntityUid": "e6cafcc0-7ad0-42f1-bc7b-18517990956c",
              "counterPartySubEntityName": "UK account",
              "counterPartySubEntityIdentifier": "204514",
              "counterPartySubEntitySubIdentifier": "00000825",
              "reference": "External Payment",
              "country": "GB",
              "spendingCategory": "PAYMENTS",
              "hasAttachment": false
            },
            {
              "feedItemUid": "f12f8373-9aab-4cef-b7ef-3bc7dd624973",
              "categoryUid": "2623afb1-cb82-4042-8714-c2fcd8852500",
              "amount": {
                "currency": "GBP",
                "minorUnits": 154532
              },
              "sourceAmount": {
                "currency": "GBP",
                "minorUnits": 154532
              },
              "direction": "OUT",
              "updatedAt": "2020-07-31T21:03:14.892Z",
              "transactionTime": "2020-07-31T21:03:14.418Z",
              "settlementTime": "2020-07-31T21:03:14.839Z",
              "source": "FASTER_PAYMENTS_OUT",
              "status": "SETTLED",
              "counterPartyType": "PAYEE",
              "counterPartyUid": "829bdbe8-3c7e-4852-be8a-1fc3d6ebbc55",
              "counterPartyName": "Mickey Mouse",
              "counterPartySubEntityUid": "e6cafcc0-7ad0-42f1-bc7b-18517990956c",
              "counterPartySubEntityName": "UK account",
              "counterPartySubEntityIdentifier": "204514",
              "counterPartySubEntitySubIdentifier": "00000825",
              "reference": "External Payment",
              "country": "GB",
              "spendingCategory": "PAYMENTS",
              "hasAttachment": false
            },
            {
              "feedItemUid": "f12f03ad-a278-4fc8-8fb9-6768d913458f",
              "categoryUid": "2623afb1-cb82-4042-8714-c2fcd8852500",
              "amount": {
                "currency": "GBP",
                "minorUnits": 496
              },
              "sourceAmount": {
                "currency": "GBP",
                "minorUnits": 496
              },
              "direction": "IN",
              "updatedAt": "2020-07-31T21:03:14.835Z",
              "transactionTime": "2020-07-31T21:03:14.358Z",
              "settlementTime": "2020-07-31T21:03:14.770Z",
              "source": "FASTER_PAYMENTS_IN",
              "status": "SETTLED",
              "counterPartyType": "PAYEE",
              "counterPartyUid": "af20d877-7a74-4090-a3b0-5da6dd1336be",
              "counterPartyName": "Mickey Mouse",
              "counterPartySubEntityUid": "84a080a1-5ad5-4fbc-a08b-8ae8ed5ac395",
              "counterPartySubEntityName": "UK account",
              "counterPartySubEntityIdentifier": "204514",
              "counterPartySubEntitySubIdentifier": "00000825",
              "reference": "External Payment",
              "country": "GB",
              "spendingCategory": "PAYMENTS",
              "hasAttachment": false
            }]}""", TransactionFeed)
        when:
        BigDecimal roundUpAmount = RoundUp.calculate(transactionFeedResponse.feedItems)

        then:
        roundUpAmount == BigDecimal.valueOf(115)
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
        TransactionFeed transactionFeedResponse = gson.fromJson("""{
          "feedItems": [
            {
              "feedItemUid": "f12f8373-9aab-4cef-b7ef-3bc7dd624973",
              "categoryUid": "2623afb1-cb82-4042-8714-c2fcd8852500",
              "amount": {
                "currency": "GBP",
                "minorUnits": 553
              },
              "sourceAmount": {
                "currency": "GBP",
                "minorUnits": 553
              },
              "direction": "IN",
              "updatedAt": "2020-07-31T21:03:14.892Z",
              "transactionTime": "2020-07-31T21:03:14.418Z",
              "settlementTime": "2020-07-31T21:03:14.839Z",
              "source": "FASTER_PAYMENTS_IN",
              "status": "SETTLED",
              "counterPartyType": "PAYEE",
              "counterPartyUid": "829bdbe8-3c7e-4852-be8a-1fc3d6ebbc55",
              "counterPartyName": "Mickey Mouse",
              "counterPartySubEntityUid": "e6cafcc0-7ad0-42f1-bc7b-18517990956c",
              "counterPartySubEntityName": "UK account",
              "counterPartySubEntityIdentifier": "204514",
              "counterPartySubEntitySubIdentifier": "00000825",
              "reference": "External Payment",
              "country": "GB",
              "spendingCategory": "PAYMENTS",
              "hasAttachment": false
            }]}""", TransactionFeed)

        when:
        BigDecimal roundUpAmount = RoundUp.calculate(transactionFeedResponse.feedItems)

        then:
        roundUpAmount == BigDecimal.ZERO
    }
}
