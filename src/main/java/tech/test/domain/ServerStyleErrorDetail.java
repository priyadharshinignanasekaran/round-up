package tech.test.domain;

public class ServerStyleErrorDetail implements ErrorDetails{
    private String error;
    private String error_description;

    public ServerStyleErrorDetail(String error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return error_description;
    }

    @Override
    public String toString() {
        return "ServerStyleErrorDetail{" +
                "error='" + error + '\'' +
                ", error_description='" + error_description + '\'' +
                '}';
    }
}
