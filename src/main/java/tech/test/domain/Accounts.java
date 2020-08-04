package tech.test.domain;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

public class Accounts {

    private List<Account> accounts;

    public List<Account> getAccounts() {
        return this.accounts;
    }

    public static class Account {
        private UUID accountUid;
        private UUID defaultCategory;
        private Currency currency;
        private ZonedDateTime createdAt;

        public String getAccountUid() {
            return accountUid.toString();
        }

        public String getDefaultCategory() {
            return defaultCategory.toString();
        }

        public String getCurrency() {
            return currency.getCurrencyCode();
        }

        public String getCreatedAt() {
            return createdAt.toString();
        }

        @Override
        public String toString() {
            return "Account{" +
                    "accountUid='" + accountUid + '\'' +
                    ", defaultCategory='" + defaultCategory + '\'' +
                    ", currency=" + currency +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}
