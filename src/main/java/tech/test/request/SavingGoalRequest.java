package tech.test.request;

import tech.test.domain.Amount;

import java.util.Currency;

public class SavingGoalRequest {
    private String name;
    private Currency currency;
    private Amount target;
    private String base64EncodedPhoto;

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency.getCurrencyCode();
    }

    public Amount getTarget() {
        return target;
    }

    public String getBase64EncodedPhoto() {
        return base64EncodedPhoto;
    }

    @Override
    public String toString() {
        return "SavingGoalCreationRequest{" +
                "name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                ", target=" + target +
                ", base64EncodedPhoto='" + base64EncodedPhoto + '\'' +
                '}';
    }
}
