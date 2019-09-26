package client;

import java.io.*;
import java.net.Socket;

/**
 * Denne klasse håndterer klientens adgang til severen. Den kan logge en klient ind på serveren og chatte.
 * @author Sofie Vonge Jensen
 */
public class AccessHandler implements Runnable{

    private static final int PORT = 1237;
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private User user;

    public AccessHandler(Socket s, DataInputStream in, DataOutputStream out, User user)
    {
        this.s = s;
        this.in = in;
        this.out = out;
        this.user =user;
    }

    public AccessHandler(){}

    public void run()
    {
      while(true)
      {
          chat(user);
      }



    }

    public boolean validateName(String name)
    {
        if(name.length() <=12)
        {
            return true;
        }

        else
        {
            System.out.println("Brugernavn for langt!");
            return false;
        }

    }

    /**
     * Logger brugeren ind ved at validere brugernavnet.
     * @param name
     * @return en user med det korrekte brugernavn
     */
    public User login(String name)
    {

        try
        {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


            String serverinput = "";

            do {
                //Brugeren forsøger at logge ind som name
                String brugerinput = "JOIN " + name + ", " + user.getIP() + ":" + PORT;
                out.writeUTF(brugerinput);
                out.flush();
                serverinput = in.readUTF();
                System.out.println("\nServeren svarer på login: " + serverinput);
                //hvis navnet ikke godkendes af serveren:
                if(!serverinput.equals("J_OK"))
                {
                    boolean test = false;
                    do{ //hvis brugernavnet ikke kan valideres til 12 karakterer
                        System.out.println("Vælg et nyt brugernavn på maks. 12 karakterer:");
                        name = br.readLine();
                        test = validateName(name);
                    } while(!test);
                }
            } while(!serverinput.equals("J_OK"));

        }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        //setter navnet på brugeren
        user.setUsername(name);
        return user;
    }

    /**
     * En metode for at en bruger kan chatte. Protokollen for at sende data laves automatisk for brugeren.
     * @param user
     */
    public void chat(User user)
    {

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String brugerinput = "";


            //kører så længe brugeren ikke skriver quit
            while (!brugerinput.equals("QUIT") && !s.isClosed())
            {
                System.out.print("(QUIT for logout) Skriv for at chatte: ");
                brugerinput = br.readLine();
                //hvis brugeren ikke skriver quit, skal chatprotokollen i spil
                if(!brugerinput.equals("QUIT") && !brugerinput.equals("JOIN"))
                {
                    brugerinput = "DATA " + user.getUsername() + ": " + brugerinput;
                    System.out.println();
                    out.writeUTF(brugerinput);
                    out.flush();
                }

                if(brugerinput.equals("QUIT"))
                {
                    System.out.println();
                    out.writeUTF(brugerinput);
                    out.flush();
                    out.close();
                    in.close();
                    System.out.println(
                            "\n* Closing connection... *");
                    s.close();

                }


            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
