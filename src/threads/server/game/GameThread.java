package threads.server.game;

import env.Env;
import threads.SPARKThread;

public class GameThread extends SPARKThread<GameInstruction>
{
    public GameThread(Env env)
    {
        super(env);
    }

    public GameThread(String id, Env env)
    {
        super(id, env);
    }

    public GameThread(String id, Env env, int sleepDelay, boolean debugPrint)
    {
        super(id, env, sleepDelay, debugPrint);
    }

    /**
     * Full constructor. Called by other constructors
     * @param id Same form as GlobalEnv.getID()
     * @param env TODO
     * @param sleepDelay Milliseconds between checks to the BlockingQueue
     * @param debugPrint Whether to print all run-loop info
     * @param maximumMessages Capacity of the BlockingQueue
     */
    public GameThread(String id, Env env, int sleepDelay, boolean debugPrint, int maximumMessages)
    {
        super(id, env, sleepDelay, debugPrint, maximumMessages);
    }
}
