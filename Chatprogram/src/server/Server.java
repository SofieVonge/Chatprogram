package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Serverprogrammet. Programmet er altid tændt, idet while-løkken altid er sand. Serveren accepterer en socket og putter
 * hver klient i sin egen klienttråd, håndteret af ClientHandler.
 * En vector tager sig af listen af klienter.
 * @author Sofie Vonge Jensen
 */
public class Server {

    //en vectorliste af klienter
    //vector er synkroniseret og derfor bruges en vektor fremfor en liste!
    static Vector<ClientHandler> list = new Vector<>();

    private static final int PORT = 1237;


    public static void main(String[] args) throws IOException {

        ServerSocket servSock = new ServerSocket(PORT);
        Socket s;
        System.out.println("Opening chat program ...");

        //begræns adgangen af klienter til 5 ad gangen vha FixedThreadpool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

            //da dette er et serverprogram skal den altid være tændt og parat til at modtage klienter!
            while (true) {

                s = servSock.accept();
                System.out.println("Socket: " + s + " er åben");

                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                ClientHandler clha = new ClientHandler(s, in, out);

                //starter en tråd
                executor.execute(clha);

                //tilføjer til listen af clients
                list.add(clha);


                //kickout servicen kører i sin egen tråd
                Thread kickoutThread = new Thread(new KickoutService());
                kickoutThread.start();

            }






    }








}
