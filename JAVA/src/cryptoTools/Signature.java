package cryptoTools;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import tools.Constants;

public class Signature {
	
		public Signature(){}
		
		Cipher ce;
		Cipher cd;

		/**
		 * Encryption by separating the input into blocks.
		 * 
		 * @param inbytes the bytes to encrypt
		 * @param pubKey the public key
		 * @return the encrypted bytes
		 * @throws Exception 
		 */
		public byte[] encryptBlockByBlock( byte[] inbytes, PrivateKey prKey )
		{
			int encryptedBlockSize = Constants.RSA_KEY_LENGTH / 8;
			int numOfBlocks = (int) Math.ceil(((double)inbytes.length)/ Constants.RSA_PLAIN_BLOCK_SIZE );
			byte[] tmpInBytes = new byte[ Constants.RSA_PLAIN_BLOCK_SIZE ];
			byte[] encryptedStoreMessage = new byte[ encryptedBlockSize*numOfBlocks] ;

			byte[] tmpEncBytes = null;
			int upperBound =0;

			for(int i=0; i<numOfBlocks; i++)
			{
				if( (i+1)*Constants.RSA_PLAIN_BLOCK_SIZE < inbytes.length )
				{
					upperBound = Constants.RSA_PLAIN_BLOCK_SIZE;
					tmpInBytes = new byte[ Constants.RSA_PLAIN_BLOCK_SIZE ];
				}
				else
				{
					upperBound = inbytes.length - i* Constants.RSA_PLAIN_BLOCK_SIZE;
					tmpInBytes = new byte[ inbytes.length - i* Constants.RSA_PLAIN_BLOCK_SIZE ];
				}

				for(int j=0; j<upperBound; j++)
					tmpInBytes[j] = inbytes[ i*numOfBlocks + j ];

				tmpEncBytes = encrypt(tmpInBytes, prKey);
				if( tmpEncBytes == null)
					return null;

				for(int j=0; j<encryptedBlockSize; j++)
					encryptedStoreMessage[ i*encryptedBlockSize + j ] = tmpEncBytes[j];
			}
			return encryptedStoreMessage;
		}
		
		/**
		 * Encrypts an RSA block
		 * @param bytes the byte array to encrypt
		 * @param pubKey the public key
		 * @return the encrypted byte array
		 * @throws Exception
		 */
		private byte[] encrypt(byte[] bytes, PrivateKey prKey)
		{
			byte[] cipherText = null;
			try
			{
				ce = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				ce.init(Cipher.ENCRYPT_MODE, prKey);

				cipherText = ce.doFinal(bytes);
			}
			catch (InvalidKeyException ex)
			{
				System.err.println("Encryption failed");
				return null;
			} catch (IllegalStateException e) {
				System.err.println("Encryption failed");
				return null;
			} catch (IllegalBlockSizeException e) {
				System.err.println("Encryption failed");
				return null;
			} catch (BadPaddingException e) {
				System.err.println("Encryption failed");
				return null;
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Encryption failed");
				return null;
			} catch (NoSuchPaddingException e) {
				System.err.println("Encryption failed");
				return null;
			}
			return cipherText;
		}

}
