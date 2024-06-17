package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import env.SPARKObject;
import env.GlobalEnv;
import static env.GlobalEnv.*;
import env.Env;

public abstract class SPARKThread<I extends Instruction> extends Thread implements SPARKObject
{
    private static final Logger logger = Logger.getLogger(SPARKThread.class.getName());

    public static final int DEFAULT_SLEEP_DELAY = 100;
    protected static final int DEFAULT_MAXIMUM_MESSAGES = 9999;  // TODO

    protected final String id;
    protected final Env env;
    protected final int sleepDelay;
    protected final boolean debugPrint;
    protected boolean stopped = false;

    public BlockingQueue<I> queue;
    protected List<Instruction> handledInstructions = new ArrayList<>();

    public SPARKThread(Env env)
    {
        this(GlobalEnv.getID(), env);
    }

    public SPARKThread(String id, Env env)
    {
        this(id, env, DEFAULT_SLEEP_DELAY, true);
    }

    public SPARKThread(String id, Env env, int sleepDelay, boolean debugPrint)
    {
        this(id, env, sleepDelay, debugPrint, DEFAULT_MAXIMUM_MESSAGES);
    }

    public SPARKThread(String id, Env env, int sleepDelay, boolean debugPrint, int maximum_messages)
    {
        this.id = id;
        this.env = env;
        this.sleepDelay = sleepDelay;
        this.debugPrint = debugPrint;
        queue = new ArrayBlockingQueue<>(maximum_messages);
    }

    public void run()
    {
        try
        {
            run(sleepDelay, debugPrint);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void run(int sleepDelay, boolean debugPrint) throws InterruptedException
    {
        if(debugPrint) { System.out.println("Thread " + getID() + " is running"); }
        while(!stopped)
        {
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

    protected void handleInstruction(I instruction)
    {
        throw new UnsupportedOperationException();
    }

    public void unStart()
    {
        stopped = true;
    }

    @Override
    public String getID()
    {
        return id;
    }

    @Override
    public boolean equals(Object other)
    {
        return (other instanceof SPARKObject) && getID().equals(((SPARKObject)other).getID());
    }
}
