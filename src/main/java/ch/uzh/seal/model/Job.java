package ch.uzh.seal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Job {
    @Getter
    @Setter
    public Long id;

    @Getter
    @Setter
    public String state;

    @Getter
    @Setter
    public Repository repository;

    @Getter
    @Setter
    public Build build;

}
