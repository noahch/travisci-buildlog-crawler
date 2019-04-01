package ch.uzh.seal.client;

import ch.uzh.seal.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * TravisRestClient handles requests to the travis-api/v3/
 */
@Slf4j
public class GitRestClient extends AbstractUnirestClient {

    private final String githubBaseUrl = "https://github.com/";

    /**
     * Constructor, handles setup of the RESTclient
     */
    public GitRestClient() {
        setupUnirest();
    }

    /**
     * Checks if the project has a pom.xml file in the base directory.
     * @param repositorySlug path of the github repository
     * @return true if a pom.xml exists, false otherwise
     */
    public boolean checkIfPomExists(String repositorySlug) {
        HttpResponse<Object> response = Unirest.get(githubBaseUrl + repositorySlug + "/blob/master/pom.xml").asObject(Object.class);
        if (response.getStatus() == HttpStatus.SC_OK) {
            return true;
        } else {
            return false;
        }
    }

}
