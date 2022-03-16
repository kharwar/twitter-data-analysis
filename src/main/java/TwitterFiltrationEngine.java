import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TwitterFiltrationEngine {
    final String urlFilter = "/(^\\w+:|^)\\/\\//";
    final String emoticonFilter = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
    MongoCollection collection = Constants.tweetCollection;
    JSONParser parser = new JSONParser();

    public File[] readFileNames() {
        final File twitterDataDirectory = new File(Constants.TWITTER_DATA_DIRECTORY_NAME);
        return twitterDataDirectory.listFiles();
    }

    public void filterAndInsertTweets(final File @NotNull [] allTweetFiles) {

        for (File tweetFile : allTweetFiles) {
            try {
                String tweetFileData = Files.readString(Paths.get(tweetFile.getPath())).trim();
                List<Document> tweetList = new ArrayList<Document>();

                String filteredTweetFileData = tweetFileData.replaceAll(urlFilter, "");
                filteredTweetFileData = filteredTweetFileData.replaceAll(emoticonFilter, "");
                ArrayList<String> tweets = parser.getObjectsFromData(filteredTweetFileData);
                for (int i = 0; i < tweets.size(); i++) {
                    try{
                        String tweet = "{" + tweets.get(i) + "}";
                        Document tweetDocument = Document.parse(tweet);
                        tweetList.add(tweetDocument);
                    }catch(Exception e){
                        System.out.print("");
                    }
                }
                try{
                    collection.insertMany(tweetList);
                } catch (Exception e){
                    System.out.print("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
