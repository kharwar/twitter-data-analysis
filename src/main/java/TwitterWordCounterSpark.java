import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class TwitterWordCounterSpark {

    private static ArrayList<String> extractKeyword(String keyword, ArrayList<String> array){
        ArrayList<String> fetchedKeywords = new ArrayList<>();
        for(String word: array){
            if(word.toLowerCase().contains(keyword)){
                fetchedKeywords.add(keyword);
            }
        }
        return fetchedKeywords;
    }

    private static void wordCount() throws IOException {
        JSONParser parser = new JSONParser();
        String masterURL = "local";
        SparkConf sparkConf = new SparkConf().setMaster(masterURL).setAppName("Twitter Word Counter");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        File[] allFiles = new File(Constants.TWITTER_DATA_DIRECTORY_NAME).listFiles();
        for(String keyword: Constants.keywords){
            String allData = "";
            for(File file : allFiles){
                ArrayList<String> tweets = parser.getText(Files.readString(Path.of(file.getPath())));
                String tweetString = String.join(" ", extractKeyword(keyword, tweets));
                allData = allData + " " + tweetString;
                JavaRDD<String> inputFile = sparkContext.textFile(tweetString.trim());
                JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split(" ")));
                JavaPairRDD countData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);
                countData.saveAsTextFile(Constants.TWITTER_DATA_DIRECTORY_NAME + "frequency_" + keyword);
            }
        }
    }

    public static void main(String[] args) {
        try {
            wordCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
