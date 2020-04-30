package analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileHandler {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hh_mm_ss_a");
    Calendar now = Calendar.getInstance();
    private File logFile = new File(getUserDataDir() + "distrib.log");

    public static String getUserDataDir() {
        return System.getProperty("user.home") + File.separator + ".dtbPlugin" + File.separator;
    }

    public byte[] extractBytes (String filePath) throws IOException {
        File file = new File(filePath);
        if(file.isFile() && file.canRead()) {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return fileContent;
        }
        return null;
    }

    public void checkPluginStorage() {
        Path pluginPath = Paths.get(getUserDataDir());

        if( !Files.exists(pluginPath)) {
            new File(pluginPath.toString()).mkdir();
        }
    }

    public String getScreenshotPath(String subdirectory) {
        checkPluginStorage();
        Path pluginStoragePath = Paths.get(getUserDataDir() + File.separator + subdirectory);

        if( !Files.exists(pluginStoragePath)) {
            new File(pluginStoragePath.toString()).mkdir();
        }

        return getUserDataDir() + File.separator + subdirectory + File.separator + formatter.format(now.getTime()) + ".png";
    }

    public boolean isLogFileCreated() {
        return this.logFile.exists();
    }

    public void writeToLogFile(String logLine) throws IOException {
        checkPluginStorage();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        if(!isLogFileCreated()) {
            this.logFile.createNewFile();
        }

        FileWriter writer = new FileWriter(getUserDataDir() + "distrib.log", true);
        writer.write(formatter.format(date) + " " + logLine);
        writer.write(System.getProperty("line.separator"));
        writer.close();
    }
}
