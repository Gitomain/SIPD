package cryptoTools;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import tools.Constants;
import tools.Tools;

public class CheckSignature {

	public CheckSignature(){}
	Cipher ce;
	Cipher cd;

	/**
	 * Decrypt a byte array block by block.
	 * 
	 * @param encBytes the byte array to decrypt
	 * @param prKey the private key
	 * @return the decrypted byte array
	 * @throws Exception 
	 */
	public byte[] decryptBlockByBlock(byte[] encBytes, PublicKey pubKey) throws Exception
	{
		byte[] decMessage;
		try
		{
			int blockSize = Constants.RSA_KEY_LENGTH / 8;
			int numOfBlocks = (int) Math.ceil(((double)encBytes.length) / blockSize);
	
			byte[] tmpInBytes = new byte[blockSize];
			byte[] decryptedStoreMessage = new byte[blockSize * numOfBlocks];
			byte[] tmpDecBytes = null;
			int c =0; 
	
			for(int i=0; i<numOfBlocks; i++)
			{
				tmpInBytes = new byte[ blockSize ];
	
				for(int j=0; j<blockSize; j++)
				{
					if( i*blockSize + j >= encBytes.length )
					{
						System.err.println("ERROR : the decryption has failed. Please check if the public and private keys are correct");
						return null;
					}
					tmpInBytes[j] = encBytes[ i*blockSize + j ];
				}
					
	
				tmpDecBytes = decrypt(tmpInBytes, pubKey);
	
				for(int j=0; j<tmpDecBytes.length; j++)
					decryptedStoreMessage[c++] = tmpDecBytes[j];
	
			}
			decMessage = Tools.copyBytesStore(decryptedStoreMessage, c);
		}
		catch(BadPaddingException ex)
		{
			System.err.println("ERROR : the decryption has failed. Please check if the public and private keys are correct");
			return null;
		}

		return decMessage;
	}

	
	/**
	 * Decrypts an RSA block
	 * @param bytes the byte array to decrypt
	 * @param prKey the private key
	 * @return the decrypted byte array
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] bytes, PublicKey pubKey) throws Exception 
	{

		byte[] cipherText = null;
		try{

			cd = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cd.init(Cipher.DECRYPT_MODE, pubKey);
			cipherText = cd.doFinal( bytes );

		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return cipherText;
	}
}
