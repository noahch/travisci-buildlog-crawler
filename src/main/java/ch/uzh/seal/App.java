package ch.uzh.seal;

import ch.uzh.seal.controller.CrawlController;
import ch.uzh.seal.utils.FileUtils;
import ch.uzh.seal.utils.PropertyManagement;

import java.util.List;

public class App {
    public static void main(String[] args) {
        List<String> repositorySlugList =  FileUtils.readFileAsList(PropertyManagement.getProperty("repository_slug_list"));
        if (repositorySlugList != null) {
            CrawlController crawlController = new CrawlController();
            repositorySlugList.forEach(crawlController::processRepository);
        }
    }

}
