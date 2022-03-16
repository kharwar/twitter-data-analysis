import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mortbay.util.ajax.JSON;

import java.io.File;
import java.io.IOException;
import java.net.*;

public class TwitterAPI {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        Constants.setMongoClient(client);
        MongoDatabase db = client.getDatabase("MyMongoTweet");
        MongoCollection<Document> tweetsCollection = db.getCollection("tweets");
        Constants.setTweetCollection(tweetsCollection);

        String bearerToken = "< -- Insert your Bearer Token here -- >";
        if(bearerToken.equals("< -- Insert your Bearer Token here -- >")){
            System.out.println("Please provide a bearer token!");
            System.exit(400);
        }
        String[] queryKeywords = {"mask","cold","immune","vaccine","flu","snow"};
        TwitterExtractionEngine tee = new TwitterExtractionEngine();
        System.out.println("\n\n\n");
        for(String query : queryKeywords){
            System.out.println("Fetching Tweets and Creating JSONs for " + query);
            tee.fetchAndStoreTweets(query, bearerToken);
        }
        System.out.println("TOTAL TWEETS FETCHED: " + JSONParser.getTweetCountFromFiles(new File(Constants.TWITTER_DATA_DIRECTORY_NAME)));

        TwitterFiltrationEngine tfe = new TwitterFiltrationEngine();
        System.out.println("\nNow inserting all data in MongoDB");
        File[] allFiles = tfe.readFileNames();
        tfe.filterAndInsertTweets(allFiles);

        TwitterWordCounterEngine twce = new TwitterWordCounterEngine();
        twce.MapReduce();
    }
}