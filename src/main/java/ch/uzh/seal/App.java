package ch.uzh.seal;

import ch.uzh.seal.client.GitRestClient;
import ch.uzh.seal.controller.CrawlController;
import ch.uzh.seal.utils.FileUtils;
import ch.uzh.seal.utils.PropertyManagement;

import java.util.List;

public class App {
    public static void main(String[] args) {
        List<String> repositorySlugList =  FileUtils.readFileAsList(PropertyManagement.getProperty("repository_slug_list"));
        if (repositorySlugList != null) {
            CrawlController crawlController = new CrawlController();
            List<String> no_travis_no_maven_projects = FileUtils.readFileAsList(PropertyManagement.getProperty("no_travis_no_maven"));
//            repositorySlugList.stream().filter(s -> !no_travis_no_maven_projects.contains(s) && crawlController.isTravisRepo(s) && crawlController.isMavenProject(s)).forEach(crawlController::processRepositoryFP);
//            repositorySlugList.stream().filter(s -> !no_travis_no_maven_projects.contains(s) && crawlController.isTravisRepo(s) && crawlController.isMavenProject(s)).forEach(crawlController::processRepositoryPP);
            repositorySlugList.stream().filter(s -> !no_travis_no_maven_projects.contains(s) && crawlController.isTravisRepo(s) && crawlController.isMavenProject(s)).forEach(crawlController::processRepositorySeries);
        }
    }

}
