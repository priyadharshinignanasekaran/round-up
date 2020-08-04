package tech.test.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class TransactionFeed {
    private List<Transaction> feedItems;

    public List<Transaction> getFeedItems() {
        return feedItems;
    }

    public static class Transaction {
        private UUID feedItemUid;
        private UUID categoryUid;
        private Amount amount;
        private Amount sourceAmount;
        private Direction direction;
        private ZonedDateTime updatedAt;
        private ZonedDateTime transactionTime;
        private ZonedDateTime settlementTime;
        private String source;
        private String status;
        private String counterPartyType;
        private UUID counterPartyUid;
        private String counterPartyName;
        private UUID counterPartySubEntityUid;
        private String counterPartySubEntityName;
        private String counterPartySubEntityIdentifier;
        private String counterPartySubEntitySubIdentifier;
        private String reference;
        private String country;

        public String getSource() {
            return source;
        }

        public String getSpendingCategory() {
            return spendingCategory;
        }

        private String spendingCategory;
        private Boolean hasAttachment;

        public Amount getAmount() {
            return amount;
        }

        public String getDirection() {
            return direction.getValue();
        }

        public enum Direction {
            OUT("OUT"), IN("IN");

            private final String value;

            Direction(final String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }
    }
}
