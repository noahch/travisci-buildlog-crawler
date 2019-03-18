package ch.uzh.seal.controller;

import ch.uzh.seal.client.TravisRestClient;
import ch.uzh.seal.model.Build;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CrawlController {

    private final TravisRestClient travisRestClient;

    public CrawlController() {
        travisRestClient = new TravisRestClient();
    }

    public void findFailPassPairs(String repositoryIdentifier) {
        List<Build> builds = travisRestClient.getBuilds(repositoryIdentifier).getBuilds();
        builds.stream().filter(build -> build.getState() == "failed").;
    }




}
