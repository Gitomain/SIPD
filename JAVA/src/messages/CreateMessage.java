/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.*;

/**
 * CreateMessage provides the methods to create a Store message and a Share message.
 * A Store message consists of a STORE identifier, a fileID, and the secret key used to encrypt the associated file.
 * A share message consists of a SHARE identifier, a fileGID, a fileID, and the secret key used to encrypt the associated file. 
 * Both fileGID and fileID are useful in the case of a Share message. 
 * If A shares a file with B, and then B shares the same file with B, the fileID received by C will be the one of the owner (A) whereas the fileGID will contains the path of the file from B.
 * 
 * @author Athanasia Katsouraki
 * 
 */
public class CreateMessage 
{
  
    public CreateMessage(){}
 
    /**
     * Creates a message of type Store
     * @param fileID the fileID that must be stored
     * @param sKey the secret key used to encrypt the file
     * @return the store message
     * @throws IOException 
     */
    public String createStoreMessage(String fileID, String sKey) throws IOException{
        String textLine = "STORE:"+fileID+":"+sKey;
        System.out.println ("MESSAGE : " + textLine);
        return textLine;
    }
    
    /**
     * Creates a message of type Share
     * @param fileGID the global ID of the file
     * @param fileID the fileID that must be shared
     * @param sKey the secret key used to encrypt the file
     * @return the share message
     * @throws IOException 
     */
    public String createShareMessage(String fileGID, String fileID, String sKey) throws IOException{
        
        String textLine = "SHARE:"+fileGID+":"+fileID+":"+sKey;
        System.out.println ("MESSAGE : " + textLine);
        return textLine;
    }

}
