package tech.test.domain;

public class ErrorDetail {
    private String message;

    public ErrorDetail(String message) {
        this.message = message;
    }

    public String getMessages() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorDetail{" +
                "message='" + message + '\'' +
                '}';
    }
}
