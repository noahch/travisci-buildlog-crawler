package ch.uzh.seal.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Builder
public class FailPassPair {
    @Getter
    @Setter
    private Build failedBuild;

    @Getter
    @Setter
    private Build previousPassedBuild;
}
