package tech.test.exception;

import tech.test.deserialization.GsonFactory;
import tech.test.domain.ErrorDetails;

public class StarlingClientException extends RuntimeException {
    public StarlingClientException(ErrorDetails errorDetail) {
        super(GsonFactory.create().toJson(errorDetail));
    }

}
