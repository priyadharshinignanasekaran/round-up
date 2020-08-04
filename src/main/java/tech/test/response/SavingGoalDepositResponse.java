package tech.test.response;

import tech.test.domain.ErrorDetail;

import java.util.List;
import java.util.UUID;

public class SavingGoalDepositResponse {
    private UUID transferUid;
    private boolean success;
    private List<ErrorDetail> errors;

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public String getTransferUid() {
        return transferUid.toString();
    }

    public boolean isSuccessFul() {
        return success;
    }

    @Override
    public String toString() {
        return "SavingGoalDepositResponse{" +
                "transferUid=" + transferUid +
                ", success=" + success +
                ", errors=" + errors +
                '}';
    }
}
