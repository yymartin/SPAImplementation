package user;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import mobile.MobileClient;
import server.client.ServerClient;
import storage.client.StorageClient;

import org.eclipse.swt.widgets.List;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.SecretKey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

public class UserApplication {

	protected Shell shell;

	private ProtocolMode protocol;
	private ServerClient serverConnector;
	private StorageClient storageConnector;
	private static String address, username, password, website;
	private static PublicKey bvk, svk;
	private static PrivateKey bsk, ssk;
	private static BigInteger r;
	private static SecretKey K;

	private Text textConsole;	
	private Text textUsername;
	private Text textPassword;

	private String output = "";
	private final String requestServerRegistration = "\n Request registration to the website";
	private final String requestStorageRegistration = "\n Try to store values into the storage";
	private final String serverRegistrationOK = "\n Correctly registered to the server";
	private final String serverRegistrationBAD = "\n Something went wrong, not registered to the server";
	private final String storageRegistrationOK = "\n Correctly store values into the storage";
	private final String storageRegistrationBAD = "\n Something went wrong, unable to store values into the storage";

	private final String requestServerConnection = "\n Request connection to the website";
	private final String requestStorageConnection = "\n Try to retrieve values from the storage";
	private final String serverConnectionOK = "\n Correctly get the challenge! Response is copied!";
	private final String serverConnectionBAD = "\n Something went wrong, unable to connect to the website";
	private final String storageConnectionOK = "\n Correctly retrieve values from the storage";
	private final String storageConnectionBAD = "\n Something went wrong, unable to retrieve values from storage";

	/**
	 * Launch the application.
	 * @param args empty for this project
	 */
	public static void main(String[] args) {
		address = System.getProperty("user.dir");

		MyKeyGenerator.generateAsymmetricKeyToFile(address, "blind");
		MyKeyGenerator.generateAsymmetricKeyToFile(address, "digital");
		MyKeyGenerator.generateOneTimePaddingKeyToFile(address, "mobile", 1024);

		bvk = MyKeyGenerator.getPublicKeyFromFile(address, "blind");
		bsk = MyKeyGenerator.getPrivateKeyFromFile(address,"blind");
		r = MyKeyGenerator.getRFromFile(address, "blind");
		svk = MyKeyGenerator.getPublicKeyFromFile(address,"digital");
		ssk = MyKeyGenerator.getPrivateKeyFromFile(address,"digital");

		K = MyKeyGenerator.getHMacKeyFromFile(address, "mobile");

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
		shell.setSize(554, 364);
		shell.setText("SWT Application");

		textUsername = new Text(shell, SWT.BORDER);
		textUsername.setBounds(238, 21, 82, 22);
		textUsername.setMessage("Username");

		Combo comboWebsite = new Combo(shell, SWT.NONE);
		comboWebsite.setBounds(238, 90, 82, 40);
		comboWebsite.setText("Website");
		comboWebsite.add("Bob.com");

		textPassword = new Text(shell, SWT.BORDER);
		textPassword.setBounds(238, 56, 82, 22);
		textPassword.setMessage("Password");

		textConsole = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		textConsole.setBounds(31, 210, 490, 113);
		textConsole.setText(output);

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
		btnRegister.setBounds(35, 130, 94, 28);
		btnRegister.setText("Register");
		btnRegister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				username = textUsername.getText();
				password = textPassword.getText();
				website = comboWebsite.getText();
				website = "https://128.178.73.85:8443/spa/register";
				if(username.equals("")) {
					output = output + "\n Please enter correct username";
					textConsole.setText(output);
					return;
				}

				if(password.equals("")) {
					output = output + "\n Please enter correct password";
					textConsole.setText(output);
					return;
				}

				if(website.equals("")) {
					output = output + "\n Please enter correct website";
					textConsole.setText(output);
					return;
				}

				switch(protocol) {
				case SERVER_OPTIMAL:
					serverConnector = new ServerClient(username, svk);
					storageConnector = new StorageClient(username, password, website, bsk, bvk, ssk, r);
					output = output + requestServerRegistration;

					if(serverConnector.registerToServer(website)) {
						output = output + serverRegistrationOK;
					} else {
						output = output + serverRegistrationBAD;
					}

					output = output + requestStorageRegistration;

					if(storageConnector.storeValuesToStorage()) {
						output = output + storageRegistrationOK;
					} else {
						output = output + storageRegistrationBAD;
						textConsole.setText(output);
						return;
					}
					break;
				case STORAGE_OPTIMAL:
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.STORAGE_OPTIMAL, password, bsk, svk, ssk, r);

					output = output + requestServerRegistration;

					if(serverConnector.registerToServer(website)) {
						output = output + serverRegistrationOK;
					} else {
						output = output + serverRegistrationBAD;
						textConsole.setText(output);
						return;
					}

					output = output + requestStorageRegistration;

					if(storageConnector.storeValuesToStorage()) {
						output = output + storageRegistrationOK;
					} else {
						output = output + storageRegistrationBAD;
						textConsole.setText(output);
						return;
					}
					break;
				case PRIVACY_OPTIMAL:
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.PRIVACY_OPTIMAL, password, bsk, svk, ssk, r);

					output = output + requestServerRegistration;

					if(serverConnector.registerToServer(website)) {
						output = output + serverRegistrationOK;
					} else {
						output = output + serverRegistrationBAD;
						textConsole.setText(output);
						return;
					}

					output = output + requestStorageRegistration;

					if(storageConnector.storeValuesToStorage()) {
						output = output + storageRegistrationOK;
					} else {
						output = output + storageRegistrationBAD;
						textConsole.setText(output);
						return;
					}
					break;
				case MOBILE:
					serverConnector = new ServerClient(username, K);

					output = output + requestServerRegistration;

					if(serverConnector.registerToServer(website)) {
						output = output + serverRegistrationOK;
					} else {
						output = output + serverRegistrationBAD;
						textConsole.setText(output);
						return;
					}

					try {
						output = output + "\n Please connect to: " + InetAddress.getLocalHost().getHostAddress();
					} catch (UnknownHostException e) {
						output = output + "\n Impossible to execute mobile registration";
						textConsole.setText(output);
						e.printStackTrace();
						return;
					}

					textConsole.setText(output);
					MobileClient.executeRegistration(K, password);
					break;
				default:
					break;
				}
				textConsole.setText(output);
			}
		});

		Button btnConnect = new Button(shell, SWT.NONE);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				username = textUsername.getText();

				if(username.equals("")) {
					output = output + "\n Please enter correct username";
					textConsole.setText(output);
					return;
				}

				if(website.equals("")) {
					output = output + "\n Please enter correct website";
					textConsole.setText(output);
					return;
				}

				BigInteger challenge = null;
				BigInteger response, id;
				BigInteger[] result = null;
				PrivateKey keyFromStorage = null;

				switch(protocol) {
				case SERVER_OPTIMAL:
					password = textPassword.getText();

					if(password.equals("")) {
						output = output + "\n Please enter correct password";
						textConsole.setText(output);
						return;
					}
					serverConnector = new ServerClient(username, svk);
					storageConnector = new StorageClient(username, password, website, bsk, bvk, ssk, r);

					output = output + requestStorageConnection;
					keyFromStorage = storageConnector.retrieveValuesFromStorage(null, null);

					if(keyFromStorage != null) {
						output = output + storageConnectionOK;
					} else {
						output = output + storageConnectionBAD;
						textConsole.setText(output);
						return;
					}

					output = output + requestServerConnection;
					challenge = serverConnector.askForChallengeToServer(website)[0];
					if(challenge == null) {
						output = output + serverConnectionBAD;
						textConsole.setText(output);
						return;
					}

					response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
					output = output + serverConnectionOK;
					copyTextToClipboard(response.toString());
					break;
				case STORAGE_OPTIMAL:
					password = textPassword.getText();

					if(password.equals("")) {
						output = output + "\n Please enter correct password";
						textConsole.setText(output);
						return;
					}
					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.STORAGE_OPTIMAL, password, bsk, svk, ssk, r);

					output = output + requestServerConnection;
					result = serverConnector.askForChallengeToServer(website);
					if(result == null) {
						output = output + serverConnectionBAD;
						textConsole.setText(output);
						return;
					}

					id = result[0];
					challenge = result[1];

					output = output + requestStorageConnection;
					keyFromStorage = storageConnector.retrieveValuesFromStorage(id, null);
					if(keyFromStorage != null) {
						output = output + storageConnectionOK;
					} else {
						output = output + storageConnectionBAD;
						textConsole.setText(output);
						return;
					}

					response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
					output = output + "\n" + serverConnectionOK;
					copyTextToClipboard(response.toString());
					break;
				case PRIVACY_OPTIMAL:
					password = textPassword.getText();
					if(password.equals("")) {
						output = output + "\n Please enter correct password";
						textConsole.setText(output);
						return;
					}

					serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
					storageConnector = new StorageClient(SSLUtility.ProtocolMode.PRIVACY_OPTIMAL, password, bsk, svk, ssk, r);	

					output = output + requestServerConnection;
					result = serverConnector.askForChallengeToServer(website);
					if(result == null) {
						output = output + serverConnectionBAD;
						textConsole.setText(output);
						return;
					}
					id = result[0];
					challenge = result[1];
					PublicKey obliviousTransferKey = MyKeyGenerator.getPublicKeyFromFile(address, "OT");
					output = output + requestStorageConnection;
					keyFromStorage = storageConnector.retrieveValuesFromStorage(id, obliviousTransferKey);
					if(keyFromStorage != null) {
						output = output + storageConnectionOK;
					} else {
						output = output + storageConnectionBAD;
						textConsole.setText(output);
						return;
					}

					response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
					output = output + "\n" + serverConnectionOK;
					copyTextToClipboard(response.toString());
					break;
				case MOBILE:
					break;
				default:
					break;
				}
				textConsole.setText(output);
			}
		});
		btnConnect.setBounds(127, 130, 94, 28);
		btnConnect.setText("Connect");

	}

	private void copyTextToClipboard(String message) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(message);
		clipboard.setContents(strSel, null);	
	}
}
