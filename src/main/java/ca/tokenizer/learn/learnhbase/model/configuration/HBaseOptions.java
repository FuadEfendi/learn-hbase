package ca.tokenizer.learn.learnhbase.model.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "system.hbaseoptions")
@Data
@NoArgsConstructor
public class HBaseOptions {
    @NotNull String namespace;
}
