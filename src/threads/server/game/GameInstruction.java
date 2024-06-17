package threads.server.game;

import threads.Instruction;
import threads.SPARKThread;
import threads.server.ServerThread;

/**
 * Instructions sent TO a GameThread via its BlockingQueue
 */
public class GameInstruction extends Instruction<GAME_INSTRUCTION_KIND, Object>
{
    public GameInstruction(SPARKThread creator, GAME_INSTRUCTION_KIND kind, Object message)
    {
        super(creator, kind, message);
    }

    /**
     * Full constructor. Called by other constructors
     * @param id Same form as GlobalEnv.getID()
     * @param creator
     * @param kind
     * @param message Associated Object with the instruction.
     *                Make sure it's appropriately castable by GameThread.handleInstruction()
     */
    public GameInstruction(String id, ServerThread creator, GAME_INSTRUCTION_KIND kind, Object message)
    {
        super(id, creator, kind, message);
    }
}
