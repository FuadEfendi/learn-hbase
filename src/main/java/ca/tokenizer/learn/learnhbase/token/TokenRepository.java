package ca.tokenizer.learn.learnhbase.token;

import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public interface TokenRepository {
    Token save(Token entity) throws IOException;
}
