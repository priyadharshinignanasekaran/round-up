package tech.test.request;

import tech.test.domain.Amount;

public class SavingGoalDepositRequest {
    private Amount amount;

    public SavingGoalDepositRequest(final Amount amount) {
        this.amount = amount;
    }
}
