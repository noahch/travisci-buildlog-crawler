package ch.uzh.seal.client;

import ch.uzh.seal.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * TravisRestClient handles requests to the travis-api/v3/
 */
@Slf4j
public class TravisRestClient extends AbstractUnirestClient {

    private final String travisApiBaseUrl = "https://api.travis-ci.org/v3/";

    /**
     * Constructor, handles setup of the RESTclient
     */
    public TravisRestClient() {
        setupUnirest();
    }

    /**
     * Get repository information form the travis api
     * @param repositoryIdentifier repositoryId oder repositorySlug
     * @return Repository
     */
    public Repository getRepository(String repositoryIdentifier){
        HttpResponse<Repository> response = Unirest.get(travisApiBaseUrl + "repo/" + encodeRepositoryIdentifier(repositoryIdentifier)).asObject(Repository.class);
        Repository repository = response.getBody();
        log.info("Repository retrieved: " + repository.toString());
        return repository;
    }

    /**
     * Get build information form the travis api
     * @param buildIdentifier buildId
     * @return Build
     */
    public Build getBuild(String buildIdentifier){
        HttpResponse<Build> response = Unirest.get(travisApiBaseUrl + "build/" + buildIdentifier).asObject(Build.class);
        Build build = response.getBody();
        log.info("Build retrieved: " + build.toString());
        return build;
    }

    /**
     * Get information of all builds in the repository form the travis api
     * @param repositoryIdentifier repositoryId oder repositorySlug
     * @return Build
     */
    public Builds getBuilds(String repositoryIdentifier){
        // TODO: handle paging if there are more than 20 builds
        HttpResponse<Builds> response = Unirest.get(travisApiBaseUrl + "repo/" + encodeRepositoryIdentifier(repositoryIdentifier)+ "/builds?include=build.jobs" ).asObject(Builds.class);
        Builds builds = response.getBody();
        if (builds != null) {
            log.info("Builds retrieved: " + builds.toString());
        }
        return builds;
    }

    /**
     * Get information of a job form the travis api
     * @param jobIdentifier jobId
     * @return Job
     */
    public Job getJob(String jobIdentifier){
        HttpResponse<Job> response = Unirest.get(travisApiBaseUrl + "job/" + jobIdentifier).asObject(Job.class);
        Job job = response.getBody();
        log.info("Job retrieved: " + job.toString());
        return job;
    }

    /**
     * Get the log of a job form the travis api
     * @param jobIdentifier jobId
     * @return Log
     */
    public Log getLog(String jobIdentifier){
        HttpResponse<Log> response = Unirest.get(travisApiBaseUrl + "job/" + jobIdentifier + "/log").asObject(Log.class);
        Log buildLog = response.getBody();
        log.info("Log retrieved: (buildLog is not printed)" + buildLog.getId());
        return buildLog;
    }

    /**
     * Returns an encoded repositoryIdentifier if it is a slug, else it return the repositoryId as is.
     * @param repositoryIdentifier repositoryId oder repositorySlug
     * @return repositoryIdentifier in a format that can be used for the travis api
     */
    private String encodeRepositoryIdentifier(String repositoryIdentifier) {
        if (!StringUtils.isNumeric(repositoryIdentifier)){
            try {
                return URLEncoder.encode(repositoryIdentifier, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return repositoryIdentifier;
        }
    }


}
