package threads.client;

import java.util.logging.Logger;

import auth.Credentials;
import env.Env;
import env.GlobalEnv;
import static env.GlobalEnv.*;
import threads.SPARKThread;
import threads.server.SERVER_INSTRUCTION_KIND;
import threads.server.ServerInstruction;
import threads.server.ServerThread;

public class ClientThread extends SPARKThread<ClientInstruction>
{
    // For WARN()
    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());

    // Client's available information about the server they're logged into
    private ClientView view;

    public ClientThread(Env env)
    {
        this(GlobalEnv.getID(), env);
    }

    public ClientThread(String id, Env env)
    {
        this(id, env, DEFAULT_SLEEP_DELAY, false);
    }

    public ClientThread(String id, Env env, int sleepDelay, boolean debugPrint)
    {
        this(id, env, sleepDelay, debugPrint, DEFAULT_MAXIMUM_MESSAGES);
    }

    /**
     * Full constructor. Called by other constructors
     * @param id Same form as GlobalEnv.getID()
     * @param env TODO
     * @param sleepDelay Milliseconds between checks to the BlockingQueue
     * @param debugPrint Whether to print all run-loop info
     * @param maximumMessages Capacity of the BlockingQueue
     */
    public ClientThread(String id, Env env, int sleepDelay, boolean debugPrint, int maximumMessages)
    {
        super(id, env, sleepDelay, debugPrint, maximumMessages);
        view = new ClientView();
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
            System.out.println("Thread " + getID() + " logged in:\t\t" + view.loggedIn);
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
    protected void handleInstruction(ClientInstruction instruction)
    {
        if(handledInstructions.contains(instruction)) { return; }
        handledInstructions.add(instruction);

        switch(instruction.kind)
        {
            case CLIENT_INSTRUCTION_KIND.UPDATE_VIEW -> {
                ClientView newView;
                try
                {
                    newView = (ClientView)instruction.message;
                }
                catch(ClassCastException e)
                {
                    WARN(logger, "Client UPDATE_VIEW instruction invalid view Object");
                    break;
                }
                view = newView;
                System.out.println("Login successful");  // TODO : Upgrade to popup in window
            }
            case CLIENT_INSTRUCTION_KIND.LOGIN -> {
                ServerThread server;
                try
                {
                    server = (ServerThread)instruction.message;
                }
                catch(ClassCastException e)
                {
                    WARN(logger, "Client LOGIN instruction invalid server Object");
                    break;
                }                                                                                                           // temp (TODO)
                server.queue.add(new ServerInstruction(this, SERVER_INSTRUCTION_KIND.LOGIN, new Credentials("nephilim_green", "")));
            }
            case CLIENT_INSTRUCTION_KIND.FAILED_LOGIN -> {
                String message;
                try
                {
                    message = (String)instruction.message;
                }
                catch(ClassCastException e)
                {
                    WARN(logger, "Client FAILED_LOGIN instruction invalid message Object");
                    break;
                }
                WARN(logger, "Login failed:\n" + message);  // TODO : Upgrade to popup in window
            }
        }
    }

    /**
     * @return Clone of this ClientThread's view member
     */
    public ClientView getCurrentView()
    {
        return view.clone();
    }
}
