package client;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * En servicetråd i klientprogrammet, der opdaterer heartbeat engang i minuttet. Tråden kan være daemon.
 * @author Sofie Vonge Jensen
 */
public class HeartbeatService implements Runnable {

    private User user;
    private DataOutputStream out;
    private Socket s;

    /**
     * Konstruktør.
     * @param s
     * @param user
     * @param out
     */
    public HeartbeatService(Socket s, User user, DataOutputStream out)
    {
        this.s = s;
        this.user = user;
        this.out = out;
    }

    public HeartbeatService(){}

    /**
     * Metoden køres når tråden startes. Hvert minut sender den en besked til serveren.
     */
    public void run()
    {

        while (!s.isClosed())
        {
            try
            {
                Thread.sleep(60000);
                out.writeUTF("IMAV");
                out.flush();
                user.setHeartbeat(LocalDateTime.now());


            } catch (InterruptedException | IOException e)
            {
                break;
               // e.printStackTrace();
            }
        }

    }
}
