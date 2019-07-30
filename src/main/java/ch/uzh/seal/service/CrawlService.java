package ch.uzh.seal.service;

import ch.uzh.seal.client.GitRestClient;
import ch.uzh.seal.client.TravisRestClient;
import ch.uzh.seal.model.*;
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
        String dir = PropertyManagement.getProperty("build_log_output_dir") + repositoryIdentifier.split("/")[1];
        if(FileUtils.checkIfDirectoryExists(dir)){
            return true;
        }else {
            Repository repository = travisRestClient.getRepository(repositoryIdentifier);
            if(repository.getId() ==  null) {
                String noTravisNoMavenFile = PropertyManagement.getProperty("no_travis_no_maven");
                String noTravisNoMavenFilePath = noTravisNoMavenFile.substring(0,noTravisNoMavenFile.lastIndexOf(File.separator)+1);
                String noTravisNoMavenFileName = noTravisNoMavenFile.substring(noTravisNoMavenFile.lastIndexOf(File.separator)+1);
                FileUtils.appendFile(noTravisNoMavenFilePath, noTravisNoMavenFileName, repositoryIdentifier);
            }
            return repository.getId() != null;
        }
    }

    public boolean isMavenProject(String repositoryIdentifier) {
        String dir = PropertyManagement.getProperty("build_log_output_dir") + repositoryIdentifier.split("/")[1];
        if(FileUtils.checkIfDirectoryExists(dir)){
            return true;
        }else {
            if (!gitRestClient.checkIfPomExists(repositoryIdentifier)) {
                String noTravisNoMavenFile = PropertyManagement.getProperty("no_travis_no_maven");
                String noTravisNoMavenFilePath = noTravisNoMavenFile.substring(0,noTravisNoMavenFile.lastIndexOf(File.separator)+1);
                String noTravisNoMavenFileName = noTravisNoMavenFile.substring(noTravisNoMavenFile.lastIndexOf(File.separator)+1);
                FileUtils.appendFile(noTravisNoMavenFilePath, noTravisNoMavenFileName, repositoryIdentifier);
                return false;
            }
            return true;
        }
    }

    public List<FailPassPair> findFailPassPairs(String repositoryIdentifier) {
        List<FailPassPair> pairs = new ArrayList<>();
        Builds builds = travisRestClient.getBuilds(repositoryIdentifier);
        if(builds != null) {
            List<Build> buildList = builds.getBuilds();
            if (buildList != null) {
                List<Build> failedBuilds = buildList.stream().filter(
                        build -> build.getState() != null && build.getPrevious_state() != null && (build.getState().equals("failed") || build.getState().equals("errored"))
                                && build.getPrevious_state().equals("passed")
                ).collect(Collectors.toList());
                failedBuilds.forEach(build -> {
                        try{
                            boolean present = buildList.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().isPresent();
                            if(present){
                                Build prevPassed = buildList.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().get();
                                FailPassPair failPassPair = FailPassPair.builder().failedBuild(build).previousPassedBuild(prevPassed).build();
                                if (!checkIfAlreadyProcessed(failPassPair, repositoryIdentifier)){
                                    pairs.add(failPassPair);
                                }
                            }
                        }catch (NullPointerException e) {
                            log.info("Found an element but element was null");
                        } catch (Exception e){
                            log.info("oops");
                        }
                });
            }
        }
        return pairs;
    }

    public List<Pair> findPassPassPairs(String repositoryIdentifier) {
        List<Pair> pairs = new ArrayList<>();
        Builds builds = travisRestClient.getBuilds(repositoryIdentifier);
        if(builds != null) {
            List<Build> buildList = builds.getBuilds();
            if (buildList != null) {
                List<Build> failedBuilds = buildList.stream().filter(
                        build -> build.getState() != null && build.getPrevious_state() != null && (build.getState().equals("passed") && build.getPrevious_state().equals("passed"))
                ).collect(Collectors.toList());
                failedBuilds.forEach(build -> {
                    try{
                        boolean present = buildList.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().isPresent();
                        if(present){
                            Build prevPassed = buildList.stream().filter(build1 -> build.getBranch().getName().equals(build1.getBranch().getName()) && Integer.parseInt(build1.getNumber()) < Integer.parseInt(build.getNumber()) && build1.getState().equals("passed")).findFirst().get();
                            Pair passPassPair = Pair.builder().build(build).previousBuild(prevPassed).build();
                            // temp
                            if (!checkIfAlreadyProcessedP(passPassPair, repositoryIdentifier) && pairs.size() < 1){
                                pairs.add(passPassPair);
                                // temp

                            }
                        }
                    }catch (NullPointerException e) {
                        log.info("Found an element but element was null");
                    }
                });
            }
        }
        return pairs;
    }

    public List<Long> findSeries(String repositoryIdentifier) {
        List<Long> series = new ArrayList<>();
        Builds builds = travisRestClient.getBuilds(repositoryIdentifier);
        if(builds != null) {
            List<Build> buildList = builds.getBuilds();
            if (buildList != null) {
                long last = 0;
                for(int i = 0; i < buildList.size(); i++){
                    if(buildList.get(i).getState().equals("passed")){
                        long curr = buildList.get(i).getJobs().get(0).getId();
                        if((last - curr) < 9999999 || last == 0){
                            series.add(curr);
                            last = curr;
                        }
                        if(series.size() == 6){
                            break;
                        }
                    }
                }
            }
        }
        return series;
    }


    public void processFailPassPair(FailPassPair failPassPair, String repositoryIdentifier){
        String failedLog = travisRestClient.getLog(failPassPair.getFailedBuild().getJobs().get(0).getId().toString()).getContent();
        String passedLog = travisRestClient.getLog(failPassPair.getPreviousPassedBuild().getJobs().get(0).getId().toString()).getContent();
        String subDir =  failPassPair.getDirectoryString() + File.separator;

        String dir = PropertyManagement.getProperty("build_log_output_dir") + repositoryIdentifier.split("/")[1] + File.separator;
        if (dir != null) {
            FileUtils.writeFile(dir + subDir, "failed.txt", failedLog);
            FileUtils.writeFile(dir + subDir, "passed.txt", passedLog);
        }
    }

    private boolean checkIfAlreadyProcessed(FailPassPair pair, String repositoryIdentifier) {
        String dir = PropertyManagement.getProperty("build_log_output_dir") + repositoryIdentifier.split("/")[1] + File.separator;
        String subDir =  pair.getDirectoryString() + File.separator;
        return FileUtils.checkIfDirectoryExists(dir + subDir);
    }

    private boolean checkIfAlreadyProcessedP(Pair pair, String repositoryIdentifier) {
        String dir = PropertyManagement.getProperty("build_log_output_dir") + repositoryIdentifier.split("/")[1] + File.separator;
        String subDir =  pair.getDirectoryString() + File.separator;
        return FileUtils.checkIfDirectoryExists(dir + subDir);
    }
}
