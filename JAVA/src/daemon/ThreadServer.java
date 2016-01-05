/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daemon;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import dataTransfer.AttributesTransfer;
import dataTransfer.FileTransfer;
import dataTransfer.FilesDescTransfer;
import dataTransfer.IOStreams;
import dataTransfer.MessageTransfer;
import dataTransfer.StringTransfer;
import messages.ProcessMessage;
import tools.Constants;
import tools.Network;
import tools.Tools;
import types.DataFile;
import types.DataFileDesc;
import types.DataIntegers;
import types.DataListFilesDesc;
import types.DataMessage;
import types.DataString;


/**
 * ThreadServer in TCell. 
 * It receives commands from clients and executes the associated actions.  
 * 
 * @author Athanasia Katsouraki 
 */
public class ThreadServer extends Thread
{  
    Socket socket;
    String userGID;
    
    /**
     * Creates a ThreadServer instance
     * @param s the client socket
     * @param userGID the userGID of the TC
     */
    public ThreadServer( Socket s, String userGID )
    {
        this.socket = s;
        this.userGID = userGID;
    }

    @Override
    public void run() 
    {
        try 
        {
		    IOStreams stream = new IOStreams( socket );
		    
		    AttributesTransfer at = new AttributesTransfer( stream );
		    DataIntegers di = (DataIntegers)at.receive();
		    
		    if( di == null || di.nAttr < 1 )
			return;
		    
		    int cmd = di.attrs[0];
		    
		    switch ( cmd )
		    {
				case Constants.CMD_STORE_FILE:
				    receiveFile( stream );
				    receiveMessage( stream );
				    break;
				    
				case Constants.CMD_SHARE_FILE:
				    receiveFile ( stream );
				    break;
				    
				case Constants.CMD_SHARE_MSG:
				    receiveMessage( stream );
				    break;
				    
				case Constants.CMD_GET_FILES_DESC:
				    sendFilesDesc( stream );
				    break;
				    
				case Constants.CMD_READ_FILE:
				    sendFile( stream );
				    break;
				default:
				    break;
		    }
		    
	   
		    stream.close();
	        socket.close();
        }
        catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    /**
     * Receive File
     * @param stream an IOStreams object
     */
    public void receiveFile( IOStreams stream ) 
    {
		FileTransfer ft = new FileTransfer( stream );
		DataFile f = (DataFile)ft.receive();
		if ( f != null )
		    writeReceivedFile( f.filePath, f.file );
		else
		    System.err.println("Error : file not received"); 
    }
    

  /**
   * Writes the received file.
   * @param filePath the file's path
   * @param file the file itself
   */
    public void writeReceivedFile( String filePath, byte[] file )
    {
        FileOutputStream fos;
        try 
        {       	
            /* Creation of the received folder */
            if ( !Tools.createDir(Tools.getHome() + Constants.TCELL_FILES) )
                return;

            /* Extract the file name from the path */
            String fileID = Tools.getFileName( filePath );

            /* Write the file in the received folder */
            fos = new FileOutputStream( Tools.getHome() + Constants.TCELL_FILES + fileID );
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write( file );
            System.out.println("The file '" + Tools.getHome() + Constants.TCELL_FILES
                    + fileID +"' (" + file.length +" bytes) has been received successfully!");
            bos.close();
            fos.close();
            

        } catch (FileNotFoundException ex){
            System.err.println("ERROR : file " + filePath + " not found");
            return;
        } catch (IOException ex){ 
        	 System.err.println("ERROR : can not write the file " + filePath );
        	return;
        }
      }

  /**
   * Receives Message.
   * @param stream an IOStreams object
   */
   public void receiveMessage( IOStreams stream)
   {
		MessageTransfer mt = new MessageTransfer( stream );
		DataMessage msg = (DataMessage)mt.receive();
	
		if ( msg != null )
		    processMessage( msg, stream );
		else
		    System.err.println("Error : message not received"); 
   }

   /**
    * Processes the received message.
    * @param msg the received message
    * @param stream an IOStreams object
    */
   public void processMessage ( DataMessage msg, IOStreams stream )
   {
       try
       {
		    /* Process the message */
		    int status = ProcessMessage.process( msg.message, msg.iv, userGID );
		    
		    /* Send the SQL status to the client */
		    Network.sendStatus( status, stream);
	    
       }
       catch (Exception ex) {
    	   System.err.println("Error during the process message");
    	   return;
       }
   }
   /**
    * Sends a file to a client through a stream.
    * @param stream an IOStreams object
    * @throws IOException 
    */
    private void sendFile( IOStreams stream ) throws IOException 
    {
		StringTransfer st = new StringTransfer( stream );
		DataString dStr = (DataString)st.receive();
		
		String FileGID = dStr.str;
		DataFile df  = ReadFile.read ( FileGID );
	
		if ( df != null )
		{
		    /* Send the status to the client */
		    Network.sendStatus( Constants.OK, stream);
		    
		    /* Send the file */
		    FileTransfer ft = new FileTransfer( stream );
		    ft.send( df );
		    System.out.println("File sent");
		}
		else
			Network.sendStatus( Constants.KO, stream);
		    
    }
    /**
     * Sends files' description to a client through a stream.
     * @param stream an IOStreams object
     */
    public void sendFilesDesc( IOStreams stream )
    {
		ArrayList<DataFileDesc> listDesc = ProcessFile.getListFilesDesc( );
		
		if (listDesc == null )
		{
			System.err.println("None file description in the database");
		    Network.sendStatus( Constants.KO, stream);
		    return;
		}
	
		Network.sendStatus( Constants.OK, stream);
	    
		/* Send the list */
		DataListFilesDesc listFilesDesc = new DataListFilesDesc(listDesc.size(), listDesc);
		FilesDescTransfer fdt = new FilesDescTransfer( stream );
		fdt.send( listFilesDesc );
		
		System.out.println("Files descriptions sent");
	
    }


}
