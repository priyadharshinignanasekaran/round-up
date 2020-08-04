package tech.test.domain;

import java.util.List;

public class ClientErrorDetail implements ErrorDetails{
    private List<ErrorDetail> errors;
    private boolean success;

    public ClientErrorDetail(List<ErrorDetail> errors, boolean success) {
        this.errors = errors;
        this.success = success;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "ClientErrorDetail{" +
                "errors=" + errors +
                ", success=" + success +
                '}';
    }
}