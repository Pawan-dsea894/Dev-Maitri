package com.iexceed.appzillonbanking.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

@Component
public class AppzillonAESUtils {

	private static final String SHA_512 = "SHA-512";
	private static final String AES = "AES";
	private static final int GCM_IV_LENGTH = 12;
	private static final int TAG_BIT_LENGTH = 128;
	private static final String SECURITY_KEY = "securitykey";

	private static final Logger LOG = LogManager.getLogger(AppzillonAESUtils.class);

	private AppzillonAESUtils() {

	}

	public static String encryptString(String pkey, String poriginalstring) {
		String encyptedstring = "";
		try {
			byte[] key = (pkey).getBytes(StandardCharsets.UTF_8);

			MessageDigest sha = MessageDigest.getInstance(SHA_512);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			LOG.trace("{} Key for encryption : {}", new String(key));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
			encyptedstring = encryptUsingAesGcm(secretKeySpec, poriginalstring);
			LOG.debug("{} length encrypted string : {}", encyptedstring.length());
		} catch (Exception e) {
			LOG.error("Exception while encryptString: ", e);
		}
		return encyptedstring;
	}
	
	public static String encrypt(String poriginalstring) {
		String encyptedstring = "";
		try {
			byte[] key = (CommonUtils.getCommonProperties(SECURITY_KEY)).getBytes(StandardCharsets.UTF_8);
			MessageDigest sha = MessageDigest.getInstance(SHA_512);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			LOG.trace("{} Key for encryption : {}", new String(key));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
			encyptedstring = encryptUsingAesGcm(secretKeySpec, poriginalstring);
			LOG.debug("{} length encrypted string : {}", encyptedstring.length());
		} catch (Exception e) {
			LOG.error("Exception while encryptString: ", e);
		}
		return encyptedstring;
	}

	public static String decryptString(String pkey, String pencrypted) {
		String originalString = "";
		try {
			byte[] key = (pkey).getBytes(StandardCharsets.UTF_8);

			MessageDigest sha = MessageDigest.getInstance(SHA_512);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			LOG.debug("Key for encrytion:" + new String(key));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);

			originalString = decryptUsingAesGcm(secretKeySpec, pencrypted);
			LOG.trace("{} originalString : {}", originalString);
		} catch (Exception e) {
			LOG.error("Exception while decryptString: ", e);
		}
		return originalString;
	}
	
	public static String decrypt(String pencrypted) {
		String originalString = "";
		try {
			byte[] key = (CommonUtils.getCommonProperties(SECURITY_KEY)).getBytes(StandardCharsets.UTF_8);

			MessageDigest sha = MessageDigest.getInstance(SHA_512);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			LOG.debug("Key for encrytion:" + new String(key));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);

			originalString = decryptUsingAesGcm(secretKeySpec, pencrypted);
			LOG.trace("{} originalString : {}", originalString);
		} catch (Exception e) {
			LOG.error("Exception while decryptString: ", e);
		}
		return originalString;
	}

	// decryption using AEC-GCM
	private static String decryptUsingAesGcm(SecretKeySpec secretKeySpec, String encryptedPayload)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		LOG.debug("Decrypt payload using AES-GCM");
		byte[] cipherText = Base64.decodeBase64(encryptedPayload.getBytes(StandardCharsets.UTF_8));

		final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		// use first 12 bytes for iv
		GCMParameterSpec gcmIv = new GCMParameterSpec(TAG_BIT_LENGTH, cipherText, 0, GCM_IV_LENGTH);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmIv);

		// use everything from 12 bytes on as ciphertext
		byte[] plainText = cipher.doFinal(cipherText, GCM_IV_LENGTH, cipherText.length - GCM_IV_LENGTH);
		return new String(plainText, StandardCharsets.UTF_8);
	}

	// encryption using AEC-GCM
	private static String encryptUsingAesGcm(SecretKeySpec secretKeySpec, String plaintext)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		LOG.debug("Encrypt payload using AES-GCM");
		byte[] iv = generateIv(GCM_IV_LENGTH);
		final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParamSpec);

		byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

		/*
		 * byte[] cipherTextWithIv = Base64 .encodeBase64(ByteBuffer.allocate(iv.length
		 * + cipherText.length).put(iv).put(cipherText).array());
		 */
		byte[] cipherTextWithIv = Base64.encodeBase64URLSafe(ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array());
		return new String(cipherTextWithIv, StandardCharsets.UTF_8);

	}

	private static byte[] generateIv(int length) {
		byte[] iv = new byte[length];
		// Commented to generate same encrypted value
		//new SecureRandom().nextBytes(iv);
		return iv;
	} 
}
