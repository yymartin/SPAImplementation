package qrcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @author yoanmartin
 *	Class which implements a method to generate a QR Code
 */
public class QRCode {
	/**
	 * Function which generate a QR Code into in image
	 * @param data The data to store into the QR Code
	 * @param address The address to store the image
	 */
	public static void generateQRCodeFromData(byte[] data, String address) {
		BitMatrix matrix = generateMatrix(data);
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(new File(address+"/qrcode.png"));
			MatrixToImageWriter.writeToStream(matrix, "png", fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static BitMatrix generateMatrix(byte[] data) {
		BigInteger dataAsString = new BigInteger(data);
		BitMatrix bitMatrix = null;
		int size = 400;
		try {
			bitMatrix = new QRCodeWriter().encode(new String(dataAsString.toByteArray()), BarcodeFormat.QR_CODE, size, size);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitMatrix;
	}
}
