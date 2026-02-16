package com.iexceed.appzillonbanking.cagl.collection.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GenerateQR {

	private static final Logger logger = LogManager.getLogger(GenerateQR.class);

	public static String generateQRCode(String upiContent) {
		String base64QR = "";
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			// int width = Integer.parseInt(CommonUtils.getCommonProperties("qrWidth"));
			// int height = Integer.parseInt(CommonUtils.getCommonProperties("qrHeight"));
			int height = 300;
			int width = 300;
			BitMatrix bitMatrix = qrCodeWriter.encode(upiContent, BarcodeFormat.QR_CODE, width, height);
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(qrImage, "PNG", outputStream); // Write QR code as PNG to output stream
			byte[] qrBytes = outputStream.toByteArray();
			base64QR = Base64.getEncoder().encodeToString(qrBytes);
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		return base64QR;
	}
}
