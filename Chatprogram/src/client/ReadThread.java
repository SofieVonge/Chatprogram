package client;



import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * Denne tråd læser alt hvad der kommer fra serveren. Kan være daemon.
 * @author Sofie Vonge Jensen
 */
public class ReadThread implements Runnable {

    private DataInputStream in;
    private Socket s;

    /**
     * Konstruktør
     * @param s
     * @param in
     */
    public ReadThread(Socket s, DataInputStream in)
    {
        this.s = s;
        this.in = in;
    }

    public ReadThread(){

        this.in = in;
    }


    /**
     * Metoden køres når tråden startes. Den læser alt hvad serveren skriver efter brugeren er logget ind.
     */
    public void run()
    {

        while(!s.isClosed())
        {
            try{
                String serverMessage = in.readUTF();
                System.out.println("\nBesked fra server: " + serverMessage);

            } catch (IOException e)
            {
                //e.printStackTrace();
                break;
            }
        }

    }
}
