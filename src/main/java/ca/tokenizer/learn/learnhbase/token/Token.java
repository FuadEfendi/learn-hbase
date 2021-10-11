package ca.tokenizer.learn.learnhbase.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @NotBlank(message = "\"container\" cannot be null")
    private String container;
    @NotBlank(message = "\"column\" cannot be null")
    private String column;
    @NotBlank(message = "\"field\" cannot be null")
    private String field;
    private String token;
    private String error;
}
