import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser {
    public String getTweetData(String rawData) {
        Pattern pattern = Pattern.compile("\"data\":\\[(.*?)\\],\"meta\"");
        Matcher m = pattern.matcher(rawData);
        if(m.find()){
            return "[" + m.group(1) + "]";
        } else {
            return "";
        }
    }

    public ArrayList<String> getText(String rawData){
        ArrayList<String> tweetTexts = new ArrayList<String>();
        String regex = "\"text\":\"(.*?)\",";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(rawData);
        while(m.find()){
            tweetTexts.add(m.group(1));
//            tweetTexts.add("\"" + m.group(1) + "" + "\"");
        }
        return tweetTexts;
    }

    public String getNextToken(String rawData){
        String regex = "\"next_token\":\"(.*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(rawData);
        if(m.find()){
            return m.group(1);
        } else {
            return null;
        }
    }

    public ArrayList<String> getObjectsFromData(String data){
        ArrayList<String> objects = new ArrayList<String>();
        String regex = "\\{(.*?)},\\{";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(data);
        while(m.find()){
            objects.add(m.group(1));
        }
        return objects;
    }

    public static int getTweetCountFromFiles(File filePath) throws IOException {
        File[] allFiles = filePath.listFiles();
        int tweetCount = 0;
        for (File file:
             allFiles) {
            String tweetFileData = Files.readString(Paths.get(file.getPath())).trim();
            tweetCount += tweetFileData.split("},\\{").length;
        }
        return tweetCount;
    }
}