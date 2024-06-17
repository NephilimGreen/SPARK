package threads.client;

import threads.INSTRUCTION_KIND;

/**
 * For instructions sent TO a ClientThread via its BlockingQueue
 */
public enum CLIENT_INSTRUCTION_KIND implements INSTRUCTION_KIND
{
    UPDATE_VIEW,
    LOGIN,
    FAILED_LOGIN
}
