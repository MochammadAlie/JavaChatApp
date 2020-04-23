package chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * @author Mochammad Alie
 */
public class ChatApp {

    private static final int PORT = 1813;
    private static HashSet<String> names = new HashSet<>();
    private static HashSet<PrintWriter> printWriters = new HashSet<>();
    
    public static void main(String[] args) throws IOException {
        System.out.println("Service running on port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        try {
            while (true) {                
                new Handler(serverSocket.accept()).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            serverSocket.close();
        }
    }

    private static class Handler extends Thread {
        
        private String name;
        private Socket socket;
        private BufferedReader bufferedReader;
        private PrintWriter printWriter;
        
        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                
                while (true) {                    
                    printWriter.println("Submit Name");
                    name = bufferedReader.readLine();
                    
                    if (name == null) {
                        return;
                    }
                    
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                
                printWriter.println("Name Accepted");
                printWriters.add(printWriter);
                
                while (true) {                    
                    String input = bufferedReader.readLine();
                    
                    if (input == null) {
                        return;
                    }
                    
                    printWriters.stream().forEach((pw) -> {
                        pw.println("Message " + name + " : " + input);
                    });
                }
                
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                }
                if (printWriter != null) {
                    printWriters.remove(printWriter);
                }
                
                try {
                    socket.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
    
}
