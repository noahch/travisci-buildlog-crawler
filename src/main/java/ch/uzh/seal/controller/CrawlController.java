package ch.uzh.seal.controller;

import ch.uzh.seal.model.FailPassPair;
import ch.uzh.seal.service.CrawlService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CrawlController {

    private final CrawlService crawlService;

    public CrawlController() {
        crawlService = new CrawlService();
    }

    public boolean isTravisRepo(String repositoryIdentifier) {
        return crawlService.isTravisRepo(repositoryIdentifier);
    }

    public boolean isMavenProject(String repositoryIdentifier) {
        return crawlService.isMavenProject(repositoryIdentifier);
    }

    public void processRepository(String repositoryIdentifier) {
       List<FailPassPair> pairs = crawlService.findFailPassPairs(repositoryIdentifier);
       log.info("PROCESSING: " + repositoryIdentifier);
       log.info(pairs.toString());
       if (!pairs.isEmpty()){
           crawlService.processFailPassPair(pairs.get(0));
       }
    }
}
