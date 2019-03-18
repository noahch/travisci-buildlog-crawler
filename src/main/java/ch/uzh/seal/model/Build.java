package ch.uzh.seal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Build {
    @Getter
    @Setter
    public Long id;

    @Getter
    @Setter
    public String state;

    @Getter
    @Setter
    public String previous_state;

    @Getter
    @Setter
    public List<Job> jobs;
}
