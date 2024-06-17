package main;

import threads.client.CLIENT_INSTRUCTION_KIND;
import threads.client.ClientInstruction;
import threads.client.ClientThread;
import threads.server.ServerThread;

public class DevRunner
{
    public static void main(String[] args)
    {
        ServerThread server = new ServerThread(null);
        ClientThread client1 = new ClientThread(null);
        ClientThread client2 = new ClientThread(null);
        server.start();
        client1.start();
        client2.start();
        ClientInstruction i1 = new ClientInstruction(null, CLIENT_INSTRUCTION_KIND.LOGIN, server);
        try
        {
            client1.queue.put(i1);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            client1.queue.put(i1);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            client2.queue.put(new ClientInstruction(null, CLIENT_INSTRUCTION_KIND.LOGIN, server));
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            Thread.sleep(3000);
            System.exit(0);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}