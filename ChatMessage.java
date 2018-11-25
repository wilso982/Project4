
import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    private String message;
    private int type;
    private String recipient;

    public ChatMessage (String string, int number) {
        this.message = string;
        this.type = number;
    }
    public ChatMessage (String string, int number, String recipient){
        this.message = string;
        this.type = number;
        this.recipient = recipient;
    }

    public int getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public String getRecipient(){
        return this.recipient;
    }
    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
}
