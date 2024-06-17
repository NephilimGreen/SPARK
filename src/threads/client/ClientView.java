package threads.client;

/**
 * A ClientThread's collection of info it has about the server it's logged into.
 * Notably NOT a SPARKObject
 */
public class ClientView implements Cloneable
{
    public boolean loggedIn = false;

    @Override
    public ClientView clone()
    {
        ClientView ret = new ClientView();
        try { ret = (ClientView)super.clone(); }
            catch(CloneNotSupportedException ignored) {}
        ret.loggedIn = this.loggedIn;
        return ret;
    }

    @Override
    public boolean equals(Object other)
    {
        return (other instanceof ClientView) && (this.loggedIn == ((ClientView)other).loggedIn);
    }
}
