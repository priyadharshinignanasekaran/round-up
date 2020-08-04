package tech.test.response;

import tech.test.domain.ErrorDetail;

import java.util.List;
import java.util.UUID;

public class SavingGoalResponse {
    private UUID savingsGoalUid;
    private boolean success;
    private List<ErrorDetail> errors;

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public String getSavingsGoalUid() {
        return savingsGoalUid.toString();
    }

    public boolean isSuccessFul() {
        return success;
    }

    @Override
    public String toString() {
        return "SavingGoalResponse{" +
                "savingsGoalUid=" + savingsGoalUid +
                ", success=" + success +
                ", errors=" + errors +
                '}';
    }
}
