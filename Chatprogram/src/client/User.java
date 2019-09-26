package client;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

/***
 * User er en bruger af systemet. Et clientprogram har en bruger der får adgang til en server via socket.
 * En bruger har et username, IP og et heartbeat. Det første heartbeat laves når brugeren laves.
 * @author Sofie Vonge Jensen
 */
public class User {

    private String username;
    private static InetAddress IP;
    private LocalDateTime heartbeat;

    public User(String username)
    {
        this.username = username;
        heartbeat = LocalDateTime.now();

        try
        {
            IP = InetAddress.getLocalHost();
        }
        catch(UnknownHostException uhEx)
        {
            System.out.println("Host ID not found!");
            System.exit(1);
        }

    }

    public User() {

        try
        {
            IP = InetAddress.getLocalHost();
        }
        catch(UnknownHostException uhEx)
        {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static InetAddress getIP() {
        return IP;
    }

    public static void setIP(InetAddress IP) {
        User.IP = IP;
    }

    public LocalDateTime getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(LocalDateTime heartbeat) {
        this.heartbeat = heartbeat;
    }
}
