package ca.tokenizer.learn.learnhbase.token;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class TokenRequestResponse {
    @NotEmpty String requestId;
    String responseId;
    @NotEmpty String callerId;
    @NotEmpty String serviceName;
    ResponseStatus status;
    @NotEmpty Container[] containers;

    @Data
    @NoArgsConstructor
    static class ResponseStatus {
        @NotNull String responseCode;
        @NotNull String message;
        String details;
    }

    @Data
    @NoArgsConstructor
    static class Container {
        @NotEmpty String container;
        @NotEmpty Column[] columns;
    }

    @Data
    @NoArgsConstructor
    static class Column {
        @NotEmpty String column;
        @NotEmpty Field[] fields;
    }

    @Data
    @NoArgsConstructor
    static class Field {
        @NotEmpty String field;
        String token;
        String error;
    }
}
