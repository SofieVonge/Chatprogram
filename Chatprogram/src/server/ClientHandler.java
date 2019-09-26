package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

/**
 * ClientHandler er klassen, der tager sig af hver klient i en ny tråd. Handleren kan læse input og skrive output
 * baseret på en firecifret kode.
 * @author Sofie Vonge Jensen
 */
public class ClientHandler implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket s;
    private String name;
    private LocalDateTime heartbeat;

    /**
     * Konstruktøren. Navnet sættes til unknown da klienten ikke har fået et navn da konstruktøren kaldes.
     * @param s
     * @param in
     * @param out
     */
    public ClientHandler(Socket s, DataInputStream in, DataOutputStream out)
    {
        this.s = s;
        this.in = in;
        this.out = out;
        this.name = "unknown";
    }

    /**
     * Default konstruktør.
     */
    public ClientHandler(){}

    /**
     * Denne metode kører når tråden startes og indtil klienten skriver QUIT.
     * Serveren skal reagere forskelligt alt efter klientens input.
     */
    public void run() {

        String clientRequest = "";
        String serverResponse = "";

        while (!clientRequest.equals("QUIT") && !this.s.isClosed()) {
            try {
                //læser hele teksten fra client
                clientRequest = in.readUTF();

                //deler tekststrengen i 4-ciffer protokolkode
                String kode = clientRequest.substring(0,4);
                String tekst = "";
                //og i resten af strengen, (hvis den er længere)! som starter på indeks 5
                if(clientRequest.length() > 4) {
                    tekst = clientRequest.substring(5);
                }

                System.out.println("Echo: Client says: " + clientRequest);

                switch (kode){
                    case "DATA" :
                       //Send data til alle klienter
                        serverResponse = tekst;
                        for(ClientHandler ch : Server.list)
                        {
                            //pånær hvis klienten har skrevet beskeden
                            if(!ch.equals(this)){
                            ch.out.writeUTF(serverResponse);
                            ch.out.flush();}
                        }

                        break;

                    case "JOIN" :
                        //opdeler strengen
                        StringTokenizer st = new StringTokenizer(tekst, ",");
                        String username = st.nextToken();
                        String ipPort = st.nextToken();

                        //tjek om brugernavnet er brugt allerede
                        for(ClientHandler ch : Server.list)
                        {
                           if(ch.name.equals(username))
                           { //hvis navnet er i brug, kommer fejlmeddelelsen:
                               serverResponse = "J_ER 200: Brugetnavn optaget";
                               this.out.writeUTF(serverResponse);
                               out.flush();
                               break;
                           }

                        }
                        //er navnet ikke brugt skal defaultnavnet opdateres
                        this.setName(username);
                        //sammen med det første heartbeat
                        this.setHeartbeat(LocalDateTime.now());


                        //klienten skal vide at navnet er godkendt:
                        serverResponse = "J_OK";
                        this.out.writeUTF(serverResponse);
                        this.out.flush();

                        //og serveren skal generere en opdateret liste
                        serverResponse = "LIST ";
                        for(ClientHandler ch : Server.list)
                        {
                            serverResponse += ch.getName() + " ";
                        }

                        // og sende listen ud til alle klienter
                        for(ClientHandler ch : Server.list)
                        {
                            ch.out.writeUTF(serverResponse);
                            ch.out.flush();
                        }

                        break;

                    case "IMAV" :
                        //opdatere heartbeat for brugeren i listen
                        this.setHeartbeat(LocalDateTime.now());
                        break;

                    case "QUIT" :
                        String farvel = "Farvel " + this.getName();

                        //fjern user fra listen
                        Server.list.remove(this); //er useren fjernet når this.s er lukket??


                        //og serveren skal generere en opdateret liste
                        serverResponse = "LIST ";
                        for(ClientHandler ch : Server.list)
                        {
                            ch.out.writeUTF(farvel);
                            ch.out.flush();
                            serverResponse += ch.getName() + " ";
                        }

                        // og sende listen ud til alle klienter
                        for(ClientHandler ch : Server.list)
                        {
                            ch.out.writeUTF(serverResponse);
                            ch.out.flush();
                        }

                        this.s.close();
                        break;

                        default:
                            out.writeUTF("J_ER 400: Invalid protokol");
                            break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            //lukker streams
            this.in.close();
            this.out.close();


        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(LocalDateTime heartbeat) {
        this.heartbeat = heartbeat;
    }

    public DataOutputStream getOut() {
        return out;
    }
}
