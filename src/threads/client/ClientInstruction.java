package threads.client;

import threads.Instruction;
import threads.server.ServerThread;

/**
 * Instructions sent TO a ClientThread via its BlockingQueue
 */
public class ClientInstruction extends Instruction<CLIENT_INSTRUCTION_KIND, Object>
{
    public final ServerThread creator;

    public ClientInstruction(ServerThread creator, CLIENT_INSTRUCTION_KIND kind, Object message)
    {
        super(creator, kind, message);
        this.creator = creator;
    }

    /**
     * Full constructor. Called by other constructors
     * @param id Same form as GlobalEnv.getID()
     * @param creator
     * @param kind
     * @param message Associated Object with the instruction.
     *                Make sure it's appropriately castable by ClientThread.handleInstruction()
     */
    public ClientInstruction(String id, ServerThread creator, CLIENT_INSTRUCTION_KIND kind, Object message)
    {
        super(id, creator, kind, message);
        this.creator = creator;
    }
}
