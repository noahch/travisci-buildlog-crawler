package ch.uzh.seal.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class FileUtils {

    public static void writeFile(String path, String filename, String content) {
        try {
            checkIfDirectoryExistsOrCreate(path);
            FileWriter fileWriter = new FileWriter(path + filename);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(content);
            printWriter.close();
            log.info(String.format("File %s written.", filename));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(String.format("File has not been %s written.", filename));
        }

    }

    private static void checkIfDirectoryExistsOrCreate(String directoryPath){
        File directory = new File(directoryPath);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }

}
