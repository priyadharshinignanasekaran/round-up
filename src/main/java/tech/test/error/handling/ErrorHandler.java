package tech.test.error.handling;

import com.google.gson.Gson;
import spark.Response;
import tech.test.domain.ErrorDetail;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class ErrorHandler {
    private Gson gson;

    public ErrorHandler(final Gson gson) {
        this.gson = gson;
    }

    public String response(Response response, List<ErrorDetail> errors) {
        String jsonResponse = gson.toJson(new ErrorResponse(errors));
        response.type("application/json");
        response.status(HTTP_BAD_REQUEST);
        return jsonResponse;
    }
}

class ErrorResponse {

    private final List<ErrorDetail> errors;
    public ErrorResponse(final List<ErrorDetail> errors) {
        this.errors = errors;
    }
    public List<ErrorDetail> getErrors() {
        return errors;
    }

}