import com.mongodb.MongoClient;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TwitterWordCounterEngine {
    JSONParser parser = new JSONParser();
    private Map<String, Integer> frequencies = new HashMap<>();
    public void MapReduce() throws InterruptedException, IOException {
        ArrayList<String> content = new ArrayList<String>();
        File[] allFiles = new File(Constants.TWITTER_DATA_DIRECTORY_NAME).listFiles();
        for (File file:
                allFiles) {
            ArrayList<String> textArr = new ArrayList<String>();
            ArrayList<String> tweets = parser.getText(Files.readString(Path.of(file.getPath())));
            for (String tweet:
                    tweets) {
                Collections.addAll(textArr, tweet.split("\\s+"));
            }

            for(String word: textArr){
                for(String keyword: Constants.keywords){
                    word = word.toLowerCase();
                    if(word.equals(keyword) || word.contains(keyword)){
                        content.add(keyword);
                    }
                }
            }
        }

        int midIndex = content.size() / 2;
        List<String> content1 = content.subList(0, midIndex);
        List<String> content2 = content.subList(midIndex + 1, content.size());

        Mapper m1 = new Mapper(content1);
        Mapper m2 = new Mapper(content2);
        m1.start();
        m2.start();

        m1.join();
        m2.join();

        ArrayList<Pair<String, Integer>> rawMappedData1 = m1.getMappedData();
        ArrayList<Pair<String, Integer>> rawMappedData2 = m2.getMappedData();

        ArrayList<Pair<String, Integer>> rawMappedData = new ArrayList<Pair<String, Integer>>();
        rawMappedData.addAll(rawMappedData1);
        rawMappedData.addAll(rawMappedData2);

        Map<String, Integer> reducedData = reduce(rawMappedData);
        System.out.println();
        for (String keyword:
             Constants.keywords) {
            System.out.println("The frequency of the keyword \"" + keyword +"\": " + reducedData.get(keyword));
        }
    }

    public Map<String, Integer> reduce(ArrayList<Pair<String, Integer>> rawMappedData){
        Map<String, Integer> reducedData = new HashMap<String, Integer>();
        System.out.println("Reducing the Data...\n");
        for(int i = 0; i < rawMappedData.size(); i++){
            String key = rawMappedData.get(i).getKey();
            if(reducedData.containsKey(key)){
                reducedData.put(key, reducedData.get(key) + 1);
            } else {
                reducedData.put(key, 1);
            }
        }
        return reducedData;
    }
}
