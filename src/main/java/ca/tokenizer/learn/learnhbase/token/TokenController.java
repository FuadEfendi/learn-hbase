package ca.tokenizer.learn.learnhbase.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
public class TokenController {
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenController(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/tokens")
    public TokenRequestResponse createtoken(@RequestBody TokenRequestResponse request) throws IOException {
        boolean error207 = false;
        for (TokenRequestResponse.Container c : request.containers) {
            for (TokenRequestResponse.Column col : c.columns) {
                for (TokenRequestResponse.Field f : col.fields) {
                    Token token = new Token(c.container, col.column, f.field, null, null);
                    Token savedToken = tokenRepository.save(token);
                    f.token = savedToken.getToken();
                    if (f.token == null) {
                        error207 = true;
                        f.error = savedToken.getError();
                    }
                }
            }
        }
        request.setResponseId(UUID.randomUUID().toString());
        TokenRequestResponse.ResponseStatus status = new TokenRequestResponse.ResponseStatus();
        if (error207 == true) {
            status.responseCode = "207";
            status.message = "some fields were not tokenized";
            status.details = "see per-field error details";
        } else {
            status.responseCode = "200";
            status.message = "success";
        }
        request.setStatus(status);
        return request;
    }
}
