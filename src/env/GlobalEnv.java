package env;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalEnv
{
    public static char[] validIDChars = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /*
        Stored as array of indeces (in validIDChars). Stored this way for temporal efficiency;
        expect later features to involve extremely rapid & frequent ID generation, so String manipulation may not be
        worth the convenience.
     */
    private static int[] previousID = new int[] {0};

    /**
     * Generates a unique identifier containing only characters in validIDChars.
     * @return The generated identifier
     */
    public static synchronized String getID()
    {
        int index = previousID.length;
        boolean carry = true;
        while(carry)
        {
            index--;
            if(index < 0)
            {
                int[] newIDArr = new int[previousID.length + 1];
                newIDArr[0] = 0;
                System.arraycopy(previousID, 0, newIDArr, 1, previousID.length);
                previousID = newIDArr;
                index++;
            }
            int oldChar = previousID[index];
            previousID[index] = (previousID[index] + 1) % validIDChars.length;
            carry = previousID[index] < oldChar;
        }

        StringBuilder ID = new StringBuilder();
        for(int c : previousID)
        {
            ID.append(validIDChars[c]);
        }
        return ID.toString();
    }

    /*
        Logging
     */
    private static final Logger logger = Logger.getLogger(GlobalEnv.class.getName());

    /**
     * @param invoker The name of the method containing the line in question.
     * @param offset N - 1, where N is the number of method calls between this and invoker.
     * @return {tab}at className.invoker(className.java:lineNumber)
     */
    public static synchronized String GET_TRACE_LINE(Logger logger, String invoker, int offset)
    {
        String className = logger.getName(); //MethodHandles.lookup().lookupClass().getName();
        return "\tat " + className + "." + invoker + "(" + className + ".java:" +
            Thread.currentThread().getStackTrace()[2 + offset].getLineNumber() + ")";
    }

    /**
     * Prints a red warning message to the console, like an Exception but it doesn't cause terminal and can't be caught.
     * {tab}at className.invoker(className.java:lineNumber) message
     * @param message The message to follow the logistical info in the warning.
     */
    public static synchronized void WARN(Logger logger, String message)
    {
        logger.log(Level.WARNING,
                   GET_TRACE_LINE(logger, Thread.currentThread().getStackTrace()[2].getMethodName(), 1) +
                       " " + message);
    }
}
