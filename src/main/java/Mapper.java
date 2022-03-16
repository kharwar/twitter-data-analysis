import java.util.*;

public class Mapper extends Thread{
    List<String> contents;
    ArrayList<Pair<String, Integer>> mappedData = new ArrayList<Pair<String, Integer>>();

    Mapper(List<String> content) { contents = content; }

    public void run(){
            map(contents);
    }

    public void map(List<String> content){
        for(String word: content){
            mappedData.add(new Pair(word, 1));
        }
    }
    public ArrayList<Pair<String, Integer>> getMappedData() {
        return mappedData;
    }
}
