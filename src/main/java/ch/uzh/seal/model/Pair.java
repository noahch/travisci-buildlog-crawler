package ch.uzh.seal.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Builder
public class Pair {
    @Getter
    @Setter
    private Build build;

    @Getter
    @Setter
    private Build previousBuild;

    public String getDirectoryString(){
        return build.getId().toString() + "_" + build.getId().toString();
    }
}
