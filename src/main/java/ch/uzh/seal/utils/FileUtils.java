package ch.uzh.seal.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

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
            log.error(String.format("File %s has not been written.", filename));
        }

    }

    public static ArrayList<String> readFileAsList(String file) {
        try {
            Scanner s = new Scanner(new File(file));
            ArrayList<String> list = new ArrayList<>();
            while (s.hasNextLine()){
                list.add(s.nextLine());
            }
            s.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(String.format("File %s could not been read.", file));
        }
        return null;

    }

    private static void checkIfDirectoryExistsOrCreate(String directoryPath){
        File directory = new File(directoryPath);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }

}
