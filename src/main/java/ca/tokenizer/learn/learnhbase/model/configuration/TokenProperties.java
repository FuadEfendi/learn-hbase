package ca.tokenizer.learn.learnhbase.model.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "token")
@Validated
public class TokenProperties {
    private List<TokenMapping> mapping;

    public List<TokenMapping> getMapping() {
        return mapping;
    }

    public void setMapping(List<TokenMapping> mapping) {
        this.mapping = mapping;
    }

    @Data
    @NoArgsConstructor
    public static class TokenMapping {
        @NotNull
        private String container;
        @NotNull
        private String hbasetable;
    }
}
