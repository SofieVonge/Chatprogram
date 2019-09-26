package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Client main. Kører klientprogrammet. Får access til serveren via AccessHandler.
 * @author Sofie Vonge Jensen
 */
public class Client {

    private static final int PORT = 1237;

    public static void main(String[] args) throws IOException {


        Scanner console = new Scanner(System.in);
        User user = new User();
        Socket s = new Socket(user.getIP(), PORT);
        DataInputStream in = new DataInputStream(s.getInputStream());
        DataOutputStream out = new DataOutputStream(s.getOutputStream());

        AccessHandler access = new AccessHandler(s, in, out, user);

        boolean test = false;
        String name;
       do {
           System.out.println("For at forbinde skal du vælge et brugernavn på maks. 12 karakterer:");
           System.out.print("Skriv brugernavn: ");
           name = console.next();
           String enter = console.nextLine(); //tom linje
           test = access.validateName(name);
       } while(!test);


        System.out.println("Forsøger at logge ind som " + name);
        user = access.login(name);
        System.out.println("Logget ind som: " + user.getUsername());

        //nu er brugeren logget ind, så kan første heartbeat sættes
        user.setHeartbeat(LocalDateTime.now());

        //nu kan brugeren chatte:
        Thread writeThread = new Thread(access);
        writeThread.start();



        //starter en tråd, der konstant læser fra serveren
        ReadThread read = new ReadThread(s, in);
        Thread readThread = new Thread(read);
        readThread.setDaemon(true);
        readThread.start();

        //starter en service som sender en impuls hvert minut til serveren
        Thread heartbeatService = new Thread(new HeartbeatService(s, user, out));
        heartbeatService.setDaemon(true);
        heartbeatService.start();

    }


}
