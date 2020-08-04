package tech.test.response;

import tech.test.domain.Amount;

import java.util.UUID;

public class RoundUpResponse {
    private UUID transferUid;
    private UUID savingsGoalUid;
    private Amount transferAmount;

    public RoundUpResponse(final String transferUid, final String savingsGoalUid, final Amount transferAmount) {

        this.transferUid = UUID.fromString(transferUid);
        this.savingsGoalUid = UUID.fromString(savingsGoalUid);
        this.transferAmount = transferAmount;
    }

    @Override
    public String toString() {
        return "RoundUpResponse{" +
                "transferUid=" + transferUid +
                ", savingsGoalUid=" + savingsGoalUid +
                ", transferAmount=" + transferAmount +
                '}';
    }
}
