package env;

public interface SPARKObject
{
    String getID();  // Should return string ID of same form as GlobalEnv.getID()

    /*
     * .equals() should return return (other instanceof SPARKObject) && getID().equals(((SPARKObject)other).getID());
     */
}
