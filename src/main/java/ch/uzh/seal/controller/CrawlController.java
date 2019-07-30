package ch.uzh.seal.controller;

import ch.uzh.seal.model.FailPassPair;
import ch.uzh.seal.model.Pair;
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
    public void processRepositoryPP(String repositoryIdentifier) {
        List<Pair> pairs = crawlService.findPassPassPairs(repositoryIdentifier);
        StringBuilder sb = new StringBuilder();
        try{
            if (!pairs.isEmpty()){

                long id1 = pairs.get(0).getPreviousBuild().getJobs().get(0).getId();
                long id2 = pairs.get(0).getBuild().getJobs().get(0).getId();
                if((id2-id1) < 9999999){
                    sb.append("{\""+pairs.get(0).getBuild().getJobs().get(0).getId()+"\",\""+pairs.get(0).getPreviousBuild().getJobs().get(0).getId()+"\"},");
                    log.info("Job1: "+pairs.get(0).getBuild().getJobs().get(0).getId());
                    log.info("Job2: "+pairs.get(0).getPreviousBuild().getJobs().get(0).getId());
                    FileUtils.appendFile("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "ssp.txt", sb.toString());
                }
            }
        }catch (Exception e){
           log.info("oops");
        }

    }

    public void processRepositoryFP(String repositoryIdentifier) {
        List<FailPassPair> pairs = crawlService.findFailPassPairs(repositoryIdentifier);
        StringBuilder sb = new StringBuilder();
        try{
            if (!pairs.isEmpty()){

                long id1 = pairs.get(0).getPreviousPassedBuild().getJobs().get(0).getId();
                long id2 = pairs.get(0).getFailedBuild().getJobs().get(0).getId();
                if((id2-id1) < 9999999){
                    sb.append("{\""+pairs.get(0).getPreviousPassedBuild().getJobs().get(0).getId()+"\",\""+pairs.get(0).getFailedBuild().getJobs().get(0).getId()+"\"},");
                    log.info("Job1: "+pairs.get(0).getFailedBuild().getJobs().get(0).getId());
                    log.info("Job2: "+pairs.get(0).getPreviousPassedBuild().getJobs().get(0).getId());
                    FileUtils.appendFile("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "sfp.txt", sb.toString());
                }
            }
        }catch (Exception e){
            log.info("oops");
        }

    }

    public void processRepositorySeries(String repositoryIdentifier) {
        List<Long> series = crawlService.findSeries(repositoryIdentifier);
        StringBuilder sb = new StringBuilder();
        try{
            if (series.size() == 6){
                    sb.append("{\""+series.get(0)+"\",\""+series.get(1)+"\",\""+series.get(2)+"\",\""+series.get(3)+"\",\""+series.get(4)+"\",\""+series.get(5)+"\"},");
                    FileUtils.appendFile("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "sser.txt", sb.toString());
                }
        }catch (Exception e){
            log.info("oops");
        }


    }
}
