package threads.server;

import threads.INSTRUCTION_KIND;

/**
 * For instructions sent TO a ServerThread via its BlockingQueue
 */
public enum SERVER_INSTRUCTION_KIND implements INSTRUCTION_KIND
{
    LOGIN,
    CREATE_GAME,
    CLEANUP_GAME
}
