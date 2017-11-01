package qrcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCode {
	public static void generateQRCodeFromData(byte[] data, String address) {
		BitMatrix matrix = generateMatrix(data);
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(new File(address+"/qrcode.png"));
			MatrixToImageWriter.writeToStream(matrix, "png", out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static BigInteger readQRCodeFromFile(String address) {
		File in = new File(address);
		BigInteger finalResult = null;
		try {
			BufferedImage qrcode = ImageIO.read(in);
			LuminanceSource source = new BufferedImageLuminanceSource(qrcode);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Result result = new MultiFormatReader().decode(bitmap);
			finalResult = new BigInteger(result.getText().getBytes("ISO-8859-1"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalResult;
	}
	
	private static BitMatrix generateMatrix(byte[] data) {
		String dataAsString = null;
		try {
			dataAsString = new String(data, "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BitMatrix bitMatrix = null;
		int size = 400;
		try {
			bitMatrix = new QRCodeWriter().encode(dataAsString, BarcodeFormat.QR_CODE, size, size);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitMatrix;
	}
}
