package ch.uzh.seal.controller;

import ch.uzh.seal.model.FailPassPair;
import ch.uzh.seal.service.CrawlService;
import ch.uzh.seal.utils.FileUtils;
import ch.uzh.seal.utils.PropertyManagement;
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
       String dir =  PropertyManagement.getProperty("build_log_output_dir");
        FileUtils.appendFile(dir, "processed_repos.txt", repositoryIdentifier);
       if (!pairs.isEmpty()){
           pairs.forEach(failPassPair -> {
               crawlService.processFailPassPair(failPassPair, repositoryIdentifier);
           });
       }
    }
}
