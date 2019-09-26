package server;



import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Denne tråd tager sig af at tjekke efter inaktive klienter. En klient er inaktiv når den ikke har sendt et hjerteslag
 * i løbet af de sidste to minutter.
 * @author Sofie Vonge Jensen
 */
public class KickoutService implements Runnable {


    /**
     * Konstruktør
     */
    public KickoutService(){}

    /**
     * Denne metode køres når tråden startes. Tjekker hvert halve minut om klienten stadig er i live.
     * Ikke testet.
     */
    public void run()
    {
        while (true)
        {
            String serverRespons = "";
            try {
                //tjekker hvert halve minut om klienterne stadig er i live.
                Thread.sleep(30000);

                for (ClientHandler ch : Server.list) {
                    LocalDateTime lastBeat = ch.getHeartbeat();
                    long minutes = ChronoUnit.MINUTES.between(lastBeat, LocalDateTime.now());

                    //hvis der er gået mere end to minutter, lukkes forbindelsen
                    if (minutes >= 2) {
                        serverRespons = ch.getName() + " er inaktiv. Farvel!";
                        ch.getOut().writeUTF(serverRespons);
                        ch.getOut().flush();
                        //ch.getS().close();
                        //fjern user fra listen
                        Server.list.remove(ch);
                    }

                }
                //og serveren skal generere en opdateret liste
                serverRespons = "LIST ";
                for(ClientHandler ch : Server.list)
                {
                    serverRespons += ch.getName() + " ";
                }

                // og sende listen ud til alle klienter
                for(ClientHandler ch : Server.list)
                {
                    ch.getOut().writeUTF(serverRespons);
                    ch.getOut().flush();
                }
            } catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
