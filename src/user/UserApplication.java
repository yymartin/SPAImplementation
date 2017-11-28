package user;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import mobile.MobileClient;
import qrcode.QRCode;
import server.client.ServerClient;
import storage.client.StorageClient;

import org.eclipse.swt.widgets.List;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;

public class UserApplication {

	protected Shell shell;

	private ProtocolMode protocol;
	private ServerClient serverConnector;
	private StorageClient storageConnector;
	private static String address, username, password, website;
	private static PublicKey bvk, svk;
	private static PrivateKey bsk, ssk;
	private static BigInteger r;
	private static byte[] K;
	private Text textConsole;
	
	public static String output = "no output";

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		address = System.getProperty("user.dir");
		username = "Yoan";
		password = "Martin";
		website = "Bob";

		bvk = MyKeyGenerator.getPublicKeyFromFile(address, "blind");
		bsk = MyKeyGenerator.getPrivateKeyFromFile(address,"blind");
		r = MyKeyGenerator.getRFromFile(address, "blind");
		svk = MyKeyGenerator.getPublicKeyFromFile(address,"digital");
		ssk = MyKeyGenerator.getPrivateKeyFromFile(address,"digital");
		
		K = MyKeyGenerator.getHMacKeyFromFile(address, "mobile").getEncoded();
		QRCode.generateQRCodeFromData(BigInteger.valueOf(12345).toByteArray(), System.getProperty("user.home")+"/Desktop");

		try {
			UserApplication window = new UserApplication();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1000, 1000);
		shell.setText("SWT Application");
		
		Label qrcode = new Label(shell, SWT.NONE);
		qrcode.setBounds(491, 40, 400, 400);
		qrcode.setVisible(false);

		List list = new List(shell, SWT.BORDER);
		String[] protocols = new String[] {"Server optimal", "Storage optimal", "Privacy optimal", "Mobile"};
		list.setItems(protocols);
		list.setBounds(40, 22, 150, 75);

		list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String elementSelected = list.getSelection()[0];
				switch(elementSelected) {
				case "Server optimal" :
					protocol = ProtocolMode.SERVER_OPTIMAL;
					break;
				case "Storage optimal" :
					protocol = ProtocolMode.STORAGE_OPTIMAL;
					break;
				case "Privacy optimal" :
					protocol = ProtocolMode.PRIVACY_OPTIMAL;
					break;
				case "Mobile" :
					protocol = ProtocolMode.MOBILE;
					break;
				default :
					protocol = ProtocolMode.SERVER_OPTIMAL;
				}
			}
		});

		Button btnRegister = new Button(shell, SWT.NONE);
		btnRegister.setBounds(282, 26, 94, 28);
		btnRegister.setText("Register");
		btnRegister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				switch(protocol) {
				case SERVER_OPTIMAL:
					serverConnector = new ServerClient(username, svk);
					storageConnector = new StorageClient(username, password, website, bsk, bvk, ssk, r);
					serverConnector.registerToServer();
					storageConnector.storeValuesToStorage();
					break;
				case STORAGE_OPTIMAL:
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.STORAGE_OPTIMAL, password, bsk, svk, ssk, r);
					serverConnector.registerToServer();
					storageConnector.storeValuesToStorage();
					break;
				case PRIVACY_OPTIMAL:
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.PRIVACY_OPTIMAL, password, bsk, svk, ssk, r);		
					serverConnector.registerToServer();
					PublicKey obliviousTransferKey = storageConnector.storeValuesToStorage();
					storePublicKeyToFile(obliviousTransferKey);
					break;
				case MOBILE:
					serverConnector = new ServerClient(username, K);
					serverConnector.registerToServer();
					MobileClient.executeRegistration();
					break;
				default:
					break;
				}
			}
		});

		Button btnConnect = new Button(shell, SWT.NONE);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				BigInteger challenge, response, id;
				BigInteger[] result;
				PrivateKey keyFromStorage;
				switch(protocol) {
				case SERVER_OPTIMAL:
					serverConnector = new ServerClient(username, svk);
					storageConnector = new StorageClient(username, password, website, bsk, bvk, ssk, r);
					keyFromStorage = storageConnector.retrieveValuesFromStorage(null, null);
					challenge = serverConnector.askForChallengeToServer()[0];
					response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
					serverConnector.executeChallengeToServer(response);

					break;
				case STORAGE_OPTIMAL:
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.STORAGE_OPTIMAL, password, bsk, svk, ssk, r);
					result = serverConnector.askForChallengeToServer();
					id = result[0];
					challenge = result[1];
					keyFromStorage = storageConnector.retrieveValuesFromStorage(id, null);
					response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
					serverConnector.executeChallengeToServer(response);
					break;
				case PRIVACY_OPTIMAL:
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.PRIVACY_OPTIMAL, password, bsk, svk, ssk, r);		
					result = serverConnector.askForChallengeToServer();
					id = result[0];
					challenge = result[1];
					PublicKey obliviousTransferKey = getPublicKeyFromFile();
					keyFromStorage = storageConnector.retrieveValuesFromStorage(id, obliviousTransferKey);
					response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
					serverConnector.executeChallengeToServer(response);

					break;
				case MOBILE:
					challenge = serverConnector.askForChallengeToServer()[0];
					QRCode.generateQRCodeFromData(challenge.toByteArray(), System.getProperty("user.home")+"/Desktop");
					qrcode.setVisible(true);
					qrcode.setImage(new Image(Display.getDefault(), System.getProperty("user.home") + "/Desktop/qrcode.png"));
					break;
				default:
					break;
				}
			}
		});
		btnConnect.setBounds(282, 60, 94, 28);
		btnConnect.setText("Connect");
		
		textConsole = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		textConsole.setBounds(10, 155, 430, 113);
		textConsole.setText(output);
	}

	public void storePublicKeyToFile(PublicKey key) {
		String address = "/Users/yoanmartin/Desktop";
		Path path = Paths.get(address+"/Server-Key");
		try {
			Files.write(path, key.getEncoded());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static PublicKey getPublicKeyFromFile() {
		String address = "/Users/yoanmartin/Desktop";
		Path path = Paths.get(address+"/Server-Key");
		byte[] key = null;
		try {
			key = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return MyKeyGenerator.convertByteArrayIntoPublicKey(key);
	}
}
