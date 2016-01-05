package types;

/**
 *
 * @author tranvan
 */
public class DataMessage extends DataType
{
    public int messageLength;
    public byte[] message; 
    public byte[] iv;
    
    public DataMessage( int messageLength, byte[] message, byte[] iv )
    {
    	this.messageLength = messageLength;
    	this.message = message;
    	this.iv = iv;
    }
}
