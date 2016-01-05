package daemon;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.Constants;
import tools.Tools;
import deviceInfo.DeviceInfo;



/**
 * Daemon in TrustedCell. 
 * 
 * @author Athanasia Katsouraki
 */
public class DaemonMain 
{
	public static void main(String[] args) throws IOException 
	{  	    	
		/* Get the infos from device.ini file */
		DeviceInfo deviceInfos = Tools.readDeviceInfo();
		if ( deviceInfos == null )
			return;
		String userGID = deviceInfos.getUserID();

		try
		{
			@SuppressWarnings("resource")
			/* Creation of the server socket */
			ServerSocket server = new ServerSocket( Constants.TCELL_PORT );

			/* The server listens for new connections and accepts it */
			System.out.println("TCell Daemon started...");
			while(true)
			{
				System.out.println("\nWaiting for a connection from an APP or from other TCells");
				Socket socket = server.accept();
				System.out.println("Accepted connection : " + socket );

				/* For each socket, a new thread is created */
				ThreadServer st = new ThreadServer( socket, userGID );
				st.start();   
			}

		}
		catch(IOException ex){
			Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}