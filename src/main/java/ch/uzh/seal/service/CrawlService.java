package ch.uzh.seal.service;

import ch.uzh.seal.client.TravisRestClient;
import ch.uzh.seal.model.Build;
import ch.uzh.seal.model.FailPassPair;
import ch.uzh.seal.utils.FileUtils;
import ch.uzh.seal.utils.PropertyManagement;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CrawlService {

    private final TravisRestClient travisRestClient;

    public CrawlService() {
        travisRestClient = new TravisRestClient();
    }

    public List<FailPassPair> findFailPassPairs(String repositoryIdentifier) {
        List<FailPassPair> pairs = new ArrayList<>();
        List<Build> builds = travisRestClient.getBuilds(repositoryIdentifier).getBuilds();
        List<Build> failedBuilds = builds.stream().filter(
                build -> (build.getState().equals("failed") || build.getState().equals("errored"))
                        && build.getPrevious_state().equals("passed")
        ).collect(Collectors.toList());
        failedBuilds.forEach(build -> {
            Build prevPassed = builds.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().get();
            pairs.add(FailPassPair.builder().failedBuild(build).previousPassedBuild(prevPassed).build());
        }
        );
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
