package dataTransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IOStreams is used to manipulate a DataOutputStream object and a DataInputStream object on a socket.
 * @author tranvan
 */
public class IOStreams 
{
	private DataOutputStream out;
	private DataInputStream in;

	/**Creation of the input and output streams on the socket
	 * @param socket the socket used to create the streams
	 */
	public IOStreams(Socket socket)
	{
		OutputStream os;
		InputStream is;

		try 
		{
			os = socket.getOutputStream();
			out = new DataOutputStream( os );

			is = socket.getInputStream();
			in = new DataInputStream( is );

		} catch (IOException ex) {
			System.err.println("Impossible to create the stream");
		}
	}

	/**
	 * @return the DataOutputStream object
	 */
	public DataOutputStream getOutputStream()
	{
		return out;
	}

	/**
	 * @return the DataInputStream object
	 */
	public DataInputStream getInputStream()
	{
		return in;
	}

	/**
	 * Flush the output stream
	 * @throws IOException
	 */
	public void flushOutStream() throws IOException
	{
		out.flush();
	}

	/**
	 * Close the streams
	 */
	public void close()
	{
		try 
		{
			out.close();
			in.close();

		} catch (IOException ex) {
			Logger.getLogger(IOStreams.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
