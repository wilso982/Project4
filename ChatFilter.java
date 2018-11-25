import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ChatFilter {
    ArrayList badWords = new ArrayList();

    public ChatFilter(String badWordsFileName) {

        String line;
        File file = new File(badWordsFileName);
        try(
        FileReader fr = new FileReader(file);

        BufferedReader br = new BufferedReader(fr);
        ){
            while ((line = br.readLine()) != null){
                badWords.add(line);
            }
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    //TODO: Make filter nicer like the other one and make it not case sensitive

    public String filter(String msg) {
        String censor = "";
        String badWord;
        if (!badWords.isEmpty()) {
            for (int i = 0; i < badWords.size(); i++) {
                if (msg.toLowerCase().contains(badWords.get(i).toString().toLowerCase())){
                     badWord = badWords.get(i).toString();
                    for (int j = 0; j < badWord.length(); j++) {
                        censor += "x";
                    }
                   msg = msg.replaceAll(badWord,censor);
                }
            }
        }
        return msg;
    }
}
