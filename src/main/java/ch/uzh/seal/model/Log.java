package ch.uzh.seal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Log {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String content;
}
