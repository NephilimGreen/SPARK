package threads;

import env.GlobalEnv;
import env.SPARKObject;

/**
 * Template for instructions sent TO a SPARKThread via its BlockingQueue
 */
public abstract class Instruction<K extends INSTRUCTION_KIND, M> implements SPARKObject
{
    protected final String id;
    public final SPARKThread creator;
    public final K kind;
    public final M message;

    public Instruction(SPARKThread creator, K kind, M message)
    {
        this(GlobalEnv.getID(), creator, kind, message);
    }

    /**
     * Full constructor. Called by other constructors
     * @param id Same form as GlobalEnv.getID()
     * @param creator
     * @param kind
     * @param message Associated Object with the instruction.
     *                Make sure it's appropriately castable by ServerThread.handleInstruction()
     */
    public Instruction(String id, SPARKThread creator, K kind, M message)
    {
        this.id = id;
        this.creator = creator;
        this.kind = kind;
        this.message = message;
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
