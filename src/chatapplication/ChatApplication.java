package chatapplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatApplication {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    
    public ChatApplication(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException ex) {
            closeEverything (socket, bufferedReader, bufferedWriter);
        }
    }
    
    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
            Scanner scanner = new Scanner(System.in);
            
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": "+ messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException ex) {
            closeEverything (socket, bufferedReader, bufferedWriter);
        }
    }
    
    public void listenForMessage () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                
                while(socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException ex) {
                        closeEverything (socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    
    public void closeEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner (System.in);
        System.out.println("Informe o seu nome de usuário para o grupo de chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        ChatApplication client = new ChatApplication(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
    
}
