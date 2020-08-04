package tech.test.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SavingGoals {
    private List<SavingGoal> savingsGoalList;

    public List<SavingGoal> getSavingsGoalList() {
        return savingsGoalList;
    }

    public static class SavingGoal {
        private UUID savingsGoalUid;
        private String name;
        private Amount target;
        private Amount totalSaved;
        private BigDecimal savedPercentage;

        public String getSavingsGoalUid() {
            return savingsGoalUid.toString();
        }

        public String getName() {
            return name;
        }

        public Amount getTarget() {
            return target;
        }

        public Amount getTotalSaved() {
            return totalSaved;
        }

        public BigDecimal getSavedPercentage() {
            return savedPercentage;
        }

    }
}
