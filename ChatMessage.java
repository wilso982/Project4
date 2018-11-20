
import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    private String message;
    private int type;

    public ChatMessage (String string, int number) {
        this.message = string;
        this.type = number;
    }

    public int getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
}
