package types;

/**
 *
 * @author tranvan
 */
public class DataIntegers extends DataType
{
    public int nAttr;
    public int[] attrs;
    
    public DataIntegers( int nAttr, int...attrs )
    {
    	this.nAttr = nAttr;
    	this.attrs = attrs;
    }
    
}
