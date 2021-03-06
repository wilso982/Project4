import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private static String badwords;
    ChatFilter filter = new ChatFilter(badwords);



    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        System.out.println(sdf.format(date) + " Server waiting for clients on port " + port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    synchronized private boolean directMessage(String message, String username) {
        date = new Date();
        ChatFilter filter = new ChatFilter(badwords);

        message = filter.filter(message);
        message = sdf.format(date) + " " + message + "\n";

        for (int i = 0; i < clients.size() ; i++) {
            if (clients.get(i).username.equals(username)){
                clients.get(i).writeMessage(message);
                return true;
            }
        }
        return false;
    }

    synchronized private void broadcast(String message) {
        date = new Date();
        message = filter.filter(message);
        message = sdf.format(date) + " " + message + "\n";

        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).writeMessage(message);
        }
        System.out.print(message);
    }

    synchronized private void remove(int idToRemove) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id == idToRemove) {
                clients.remove(i);
            }
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        int port = 1500;
        badwords = "badwords.txt";

        if (args.length == 1){
            port = Integer.parseInt(args[0]);
        }
        else if (args.length == 2){
            port = Integer.parseInt(args[0]);
            badwords = args[1];
        }

        ChatServer server = new ChatServer(port);
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;

        private boolean writeMessage(String msg) {
            if (socket.isConnected()) {
                try {
                    sOutput.writeObject(msg);
                } catch (IOException e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        private void close() {
            try {
                socket.close();
                sInput.close();
                sOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            date = new Date();
            if (clients.size() > 1) {
                for (int i = 0; i < clients.size() - 1; i++) {
                    if (clients.get(i).username.equals(username)) {
                        System.out.println(sdf.format(date) + " " + username + ": tried connecting," +
                                " but someone with the username already exists.");
                        remove(id);
                        close();
                        return;
                    }
                }
            }
            System.out.println(sdf.format(date) + " " + username + ": just connected.");
            System.out.println(sdf.format(date) + " Server waiting for clients on port " + port);

            // Send message back to the client
            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(sdf.format(date) + " " + username + " disconnected without a LOGOUT message.");
                    remove(id);
                    close();
                    return;
                }

                if (cm.getType() == 0) {
                    broadcast(username + ": " + cm.getMessage());
                } else if (cm.getType() == 1) {
                    if (cm.getMessage().isEmpty()) {
                        System.out.println(sdf.format(date) + " " + username + " disconnected without a LOGOUT message.");
                    } else {
                        System.out.println(sdf.format(date) + " " + username + " disconnected with a LOGOUT message.");
                    }
                    remove(id);
                    close();
                    return;
                } else if (cm.getType() == 2) {
                    if (clients.size() > 1) {
                        for (int i = 0; i < clients.size(); i++) {
                            if (id != clients.get(i).id) {
                                directMessage(clients.get(i).username, username);
                            }
                        }
                    }
                    else {
                        directMessage("There are no other users online.", username);
                    }

                } else if (cm.getType() == 3) {
                    if (!directMessage(username + " -> " + cm.getRecipient() +": " + cm.getMessage(), cm.getRecipient())){
                        System.out.println(sdf.format(date) + " The user " + cm.getRecipient() + " is not online.");
                        directMessage("The user " + cm.getRecipient() + " is not online.", username);
                    } else {
                        String message = filter.filter(cm.getMessage()) + "\n";
                        System.out.println(sdf.format(date) + " " +
                                username + " -> " + cm.getRecipient() + ": " + message);
                        directMessage(username + " -> " + cm.getRecipient() +": " + cm.getMessage(), username);
                    }
                }
            }
        }
    }
}