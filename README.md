# TRAVIS-CI BUILDLOG CRAWLER
The purpose of this tool is to find pairs of a failing and a succeeding passing build 
and write their logs into a output directory. These logs are later used to create a differencing
algorithm for a more efficient failure cause identification in build logs.

## Usage
First, the `src/main/resources/config.properties`  file needs to be edited:
* set `build_log_output_dir` to your desired output directory (the directory will be created if it does not exist yet). 
Use a pathseparator according to your file system. E.g. for windows `build_log_output_dir=C:\\Data\\BA\\output\\`
* set `repository_slug_list` to your input file. The file and directory need to exist. E.g. `repository_slug_list=C:\\Data\\BA\\input\\repo_slugs.txt`

Your input file should contain all the travis-ci repository slugs you want to crawl, separated by newline. E.g.
```
 noahch/travisci-buildlog-crawler
 exampleuser/example-repository
 ```
Now you can run the App within your IDE or from the commandline
```java -cp target/travisci-buildlog-crawler-1.0-SNAPSHOT-jar-with-dependencies.jar ch.uzh.seal.App```


