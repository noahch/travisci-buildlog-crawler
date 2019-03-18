package ch.uzh.seal;

import ch.uzh.seal.client.TravisRestClient;
import ch.uzh.seal.controller.CrawlController;

public class App {
    public static void main(String[] args) {
//        String repoSlug = "noahch/travisci-buildlog-crawler";
//        TravisRestClient client = new TravisRestClient();
//        client.getRepository(repoSlug);
//        client.getRepository("23734264");
//        client.getBuild("507919295");
//        client.getBuilds(repoSlug);
//        client.getJob("507919296");
//        client.getLog("507919296");
        CrawlController crawlController = new CrawlController();
        crawlController.findFailPassPairs("23734264");

    }

}
