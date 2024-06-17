package threads.server;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import env.Env;
import env.GlobalEnv;
import static env.GlobalEnv.*;
import auth.Credentials;
import threads.SPARKThread;
import threads.client.CLIENT_INSTRUCTION_KIND;
import threads.client.ClientInstruction;
import threads.client.ClientThread;
import threads.client.ClientView;

public class ServerThread extends SPARKThread<ServerInstruction>
{
    // For WARN()
    private static final Logger logger = Logger.getLogger(ServerThread.class.getName());

    private static final int DEFAULT_MAXIMUM_CLIENTS = 99;

    // Logged-in ClientThreads and their usernames
    private Map<ClientThread, String> threads = new HashMap<>();

    // Maximum number of ClientThreads this will allow to log in at once
    private final int maximum_clients;

    public ServerThread(Env env)
    {
        this(GlobalEnv.getID(), env);
    }

    public ServerThread(String id, Env env)
    {
        this(id, env, DEFAULT_SLEEP_DELAY, false);
    }

    public ServerThread(String id, Env env, int sleepDelay, boolean debugPrint)
    {
        this(id, env, sleepDelay, debugPrint, DEFAULT_MAXIMUM_MESSAGES);
    }

    public ServerThread(String id, Env env, int sleepDelay, boolean debugPrint, int maximumMessages)
    {
        this(id, env, sleepDelay, debugPrint, maximumMessages, DEFAULT_MAXIMUM_CLIENTS);
    }

    /**
     * Full constructor. Called by other constructors
     * @param id Same form as GlobalEnv.getID()
     * @param env TODO
     * @param sleepDelay Milliseconds between checks to the BlockingQueue
     * @param debugPrint Whether to print all run-loop info
     * @param maximumMessages Capacity of the BlockingQueue
     * @param maximumClients Maximum number of ClientThreads this will allow to be logged in at any given time
     */
    public ServerThread(String id, Env env, int sleepDelay, boolean debugPrint, int maximumMessages, int maximumClients)
    {
        super(id, env, sleepDelay, debugPrint, maximumMessages);
        this.maximum_clients = maximumClients;
    }

    /**
     * TEMP
     * @param sleepDelay
     * @param debugPrint
     * @throws InterruptedException
     */
    @Override
    public void run(int sleepDelay, boolean debugPrint) throws InterruptedException
    {
        if(debugPrint) { System.out.println("Thread " + getID() + " is running"); }
        while(!stopped)
        {
            System.out.println("Thread " + getID() + " size:\t\t" + threads.size());
            try
            {
                if(queue.peek() != null)
                {
                    if(debugPrint) { System.out.println("Thread " + getID() + " handling instruction"); }
                    handleInstruction(queue.poll());
                }
                else
                {
                    if(debugPrint) { System.out.println("Thread " + getID() + " instruction queue empty"); }
                    Thread.sleep(sleepDelay);
                }
            }
            catch(InterruptedException e)
            {
                if(debugPrint) { WARN(logger, "Thread " + getID() + " EXCEPTION:\n" + e.getMessage()); }
                else { throw e; }
            }
        }
    }

    /**
     * Called by run() loop
     * @param instruction
     */
    @Override
    protected void handleInstruction(ServerInstruction instruction)
    {
        if(handledInstructions.contains(instruction)) { return; }
        handledInstructions.add(instruction);

        switch(instruction.kind)
        {
            case SERVER_INSTRUCTION_KIND.LOGIN -> {
                Credentials credentials;
                try
                {
                    credentials = (Credentials)instruction.message;
                }
                catch(ClassCastException e)
                {
                    WARN(logger, "Server LOGIN instruction invalid credentials Object");
                    break;
                }
                ClientThread invoker = instruction.creator;
                int loginResult = login(invoker, credentials.username, credentials.password);
                switch(loginResult)
                {
                    case 0 -> {
                        ClientView invokerCurrentView = invoker.getCurrentView();
                        invokerCurrentView.loggedIn = true;
                        invoker.queue.add(new ClientInstruction(this, CLIENT_INSTRUCTION_KIND.UPDATE_VIEW, invokerCurrentView));
                    }
                    case 1 -> {
                        invoker.queue.add(new ClientInstruction(this, CLIENT_INSTRUCTION_KIND.FAILED_LOGIN, "Invalid username"));
                    }
                    case 2 -> {
                        invoker.queue.add(new ClientInstruction(this, CLIENT_INSTRUCTION_KIND.FAILED_LOGIN, "Invalid password"));
                    }
                    case 3 -> {
                        invoker.queue.add(new ClientInstruction(this, CLIENT_INSTRUCTION_KIND.FAILED_LOGIN, "Client already logged in"));
                    }
                    case 4 -> {
                        invoker.queue.add(new ClientInstruction(this, CLIENT_INSTRUCTION_KIND.FAILED_LOGIN, "Server full"));
                    }
                }
            }
        }
    }

    /**
     * Called by handleInstruction(). Login fails if username is already logged-in or the given ClientThread is already
     *  associated with an active session
     * @param username Username to be associated with the calling ClientThread
     * @param password Password associated with the username. Currently unused (TODO)
     * @return 0 if login is successful, 1 if invalid username, 2 if invalid password (TODO),
     *         3 if ClientThread is already registered, 4 if no available capacity in server
     */
    public int login(ClientThread invoker, String username, String password)
    {
        for(String user : threads.values())
        {
            if(user.equals(username))
            {
                return 1;
            }
            // TODO : Sanitize username and check database
        }
        for(ClientThread client : threads.keySet())
        {
            if(client.equals(invoker))
            {
                return 3;
            }
        }
        if(threads.size() >= maximum_clients)
        {
            return 4;
        }

        threads.put(invoker, username);
        return 0;
    }
}
