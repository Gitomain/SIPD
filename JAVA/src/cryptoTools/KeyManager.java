package cryptoTools;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import tools.Tools;

/**
 * KeyManager is able to get Public/ Private keys and create PrivateKey and PublicKey objects.
 * It also provides methods of conversion.
 * 
 * @author Athanasia Katsouraki
 */
public class KeyManager 
{

	public KeyManager () throws NoSuchAlgorithmException { }

	/**
	 * Loads Public Key from a file.
	 * @param path path of the public key
	 * @param algorithm the algorithm used - RSA here
	 * @return a PublicKey object
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException 
	 */
	public PublicKey LoadPublicKey(String path, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException 
	{
		// Read Public Key.
		File filePublicKey = new File(path );
		FileInputStream fis = new FileInputStream(path );
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		// Generate PublicKey.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		return publicKey;
	}
	/**
	 * Converts Public Key to String.
	 * @param pubKey the PublicKey object
	 * @return a String value
	 * @throws IOException  
	 */
	public String PublicKeyToString(PublicKey pubKey) throws IOException 
	{

		byte[] encodedPublicKey = new byte[(int) pubKey.getEncoded().length];
		encodedPublicKey = pubKey.getEncoded();
		encodedPublicKey = Tools.encodeBytes(encodedPublicKey);
		String encodedPubKey = new String(encodedPublicKey, "UTF-8"); 

		return encodedPubKey;
	}
	/**
	 * Converts a String into a PublicKey
	 * @param stringPubKey the String to convert
	 * @param algorithm the algorithm used - RSA here
	 * @return a PublicKey object
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException 
	 */
	public PublicKey StringToPublicKey(String stringPubKey, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException 
	{
		// Read Public Key.
		byte[] decodedPublicKey = new byte[(int) stringPubKey.length()];
		decodedPublicKey = stringPubKey.getBytes();
		decodedPublicKey = Tools.decodeBytes(decodedPublicKey );
		// Generate PublicKey.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		return publicKey;
	}
	/**
	 * Loads PrivateKey from a file.
	 * @param path path of the private key
	 * @param algorithm the algorithm used - RSA here
	 * @return a PrivateKey object
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException 
	 */
	public PrivateKey LoadPrivateKey(String path, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException 
	{

		// Read Private Key.
		File filePrivateKey = new File(path );
		FileInputStream fis = new FileInputStream(path );
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		// Generate PrivateKey.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return privateKey;
	}

}
