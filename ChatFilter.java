import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ChatFilter {
    ArrayList<String> badWords = new ArrayList<>();

    public ChatFilter(String badWordsFileName) {

        String line;
        File file = new File(badWordsFileName);
        try (
                FileReader fr = new FileReader(file);

                BufferedReader br = new BufferedReader(fr);
        ){
            while ((line = br.readLine()) != null){
                badWords.add(line);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public String filter(String msg) {
        String messageArray[] = msg.split(" ");
        String censor = "";
        String badWord;
        if (!badWords.isEmpty()) {
            for (int i = 0; i < badWords.size(); i++) {
                for (int j = 1; j < messageArray.length; j++) {
                    msg = msg.replaceAll(messageArray[j], messageArray[j].toLowerCase());
                    if (msg.contains(badWords.get(i))) {
                        badWord = badWords.get(i);
                        for (int z = 0; z < badWord.length(); z++) {
                            censor += "*";
                        }
                        msg = msg.replaceAll(badWord, censor);
                    }
                }
            }
        }
        return msg;
    }
}