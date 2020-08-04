package tech.test.usecase;

import tech.test.domain.TransactionFeed;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static tech.test.domain.TransactionFeed.Transaction.Direction.OUT;

public class RoundUp {

    private RoundUp() {
    }

    public static BigDecimal calculate(final List<TransactionFeed.Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> (transaction.getDirection().equals(OUT.getValue())))
                .filter(outTransactions -> !(outTransactions.getSource().equals("INTERNAL_TRANSFER") && outTransactions.getSpendingCategory().equals("SAVING")))
                .map(transaction -> convert(transaction.getAmount().getMinorUnits()))
                .reduce(ZERO, BigDecimal::add);
    }

    private static BigDecimal convert(final BigDecimal minorUnit) {
        return BigDecimal.valueOf(100).subtract(minorUnit.remainder(BigDecimal.valueOf(100)));
    }


}


