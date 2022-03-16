import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class Constants {
    public static String TWITTER_DATA_DIRECTORY_NAME = "./twitterData/";
    public static MongoCollection tweetCollection;
    public static MongoClient mongoClient;
    public static String[] keywords = {"flu", "snow", "cold"};

    public static  void setMongoClient(MongoClient client) { mongoClient = client; };
    public static void setTweetCollection(MongoCollection collection) {
        tweetCollection = collection;
    }

}
