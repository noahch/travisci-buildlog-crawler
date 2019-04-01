package ch.uzh.seal.service;

import ch.uzh.seal.client.GitRestClient;
import ch.uzh.seal.client.TravisRestClient;
import ch.uzh.seal.model.Build;
import ch.uzh.seal.model.FailPassPair;
import ch.uzh.seal.model.Repository;
import ch.uzh.seal.utils.FileUtils;
import ch.uzh.seal.utils.PropertyManagement;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CrawlService {

    private final TravisRestClient travisRestClient;
    private final GitRestClient gitRestClient;

    public CrawlService() {
        travisRestClient = new TravisRestClient();
        gitRestClient = new GitRestClient();
    }

    public boolean isTravisRepo(String repositoryIdentifier) {
        Repository repository = travisRestClient.getRepository(repositoryIdentifier);
        return repository.getId() != null;
    }

    public boolean isMavenProject(String repositoryIdentifier) {
        return gitRestClient.checkIfPomExists(repositoryIdentifier);
    }

    public List<FailPassPair> findFailPassPairs(String repositoryIdentifier) {
        List<FailPassPair> pairs = new ArrayList<>();
        List<Build> builds = travisRestClient.getBuilds(repositoryIdentifier).getBuilds();
        if (builds != null) {
            List<Build> failedBuilds = builds.stream().filter(
                    build -> build.getState() != null && build.getPrevious_state() != null && (build.getState().equals("failed") || build.getState().equals("errored"))
                            && build.getPrevious_state().equals("passed")
            ).collect(Collectors.toList());
            failedBuilds.forEach(build -> {
                    try{
                        boolean present = builds.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().isPresent();
                        if(present){
                            Build prevPassed = builds.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().get();
                            pairs.add(FailPassPair.builder().failedBuild(build).previousPassedBuild(prevPassed).build());
                        }
                    }catch (NullPointerException e) {
                        log.info("Found an element but element was null");
                    }
                    }
            );
        }

        return pairs;
    }

    public void processFailPassPair(FailPassPair failPassPair){
        String failedLog = travisRestClient.getLog(failPassPair.getFailedBuild().getJobs().get(0).getId().toString()).getContent();
        String passedLog = travisRestClient.getLog(failPassPair.getPreviousPassedBuild().getJobs().get(0).getId().toString()).getContent();
        String subDir =  failPassPair.getDirectoryString() + File.separator;

        String dir = PropertyManagement.getProperty("build_log_output_dir");
        if (dir != null) {
            FileUtils.writeFile(dir + subDir, "failed.txt", failedLog);
            FileUtils.writeFile(dir + subDir, "passed.txt", passedLog);
        }



    }



}
