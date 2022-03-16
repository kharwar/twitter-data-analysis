import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TwitterExtractionEngine {
    static JSONParser parser = new JSONParser();
    public void fetchAndStoreTweets(String keyword, String bearerToken) throws IOException {

        final Path path = Paths.get(Constants.TWITTER_DATA_DIRECTORY_NAME);
        if(!Files.exists(path)){
            Files.createDirectory(path);
        }
        String nextToken = null;
        for (int i = 0; i < 8; i++) {
            String targetUrl = "https://api.twitter.com/2/tweets/search/recent";
            String tweetFields = "&tweet.fields=attachments,author_id,conversation_id,created_at,id,in_reply_to_user_id,lang,possibly_sensitive,reply_settings,source,text,withheld";
            targetUrl = targetUrl + "?query=" + keyword + "%20-is:retweet" + tweetFields + "&max_results=100";
            if (i != 0) {
                targetUrl = targetUrl + "&next_token=" + nextToken;
            }
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + bearerToken);
            BufferedReader jsonRes = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            String rawData = "";
            while((line = jsonRes.readLine()) != null){
                rawData += line;
            }

            String tweets = parser.getTweetData(rawData);
            String fileName = Constants.TWITTER_DATA_DIRECTORY_NAME + "twitter_data_" + keyword + "_" + i + ".json";
            try (FileWriter fw = new FileWriter(fileName, StandardCharsets.UTF_8)) {
                fw.write(tweets);
            } catch (Exception e){
                e.printStackTrace();
            }
            nextToken = parser.getNextToken(rawData);
        }

    }
}