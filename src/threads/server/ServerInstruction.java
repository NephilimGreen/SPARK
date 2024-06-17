package threads.server;

import threads.Instruction;
import threads.client.ClientThread;

/**
 * Instructions sent TO a ServerThread via its BlockingQueue
 */
public class ServerInstruction extends Instruction<SERVER_INSTRUCTION_KIND, Object>
{
    public final ClientThread creator;

    public ServerInstruction(ClientThread creator, SERVER_INSTRUCTION_KIND kind, Object message)
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
     *                Make sure it's appropriately castable by ServerThread.handleInstruction()
     */
    public ServerInstruction(String id, ClientThread creator, SERVER_INSTRUCTION_KIND kind, Object message)
    {
        super(id, creator, kind, message);
        this.creator = creator;
    }
}
