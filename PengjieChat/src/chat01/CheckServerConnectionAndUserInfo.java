/**
 * 
 */
package chat01;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CheckServerConnectionAndUserInfo {
	
	private String userName ;//= "Sarah"
	private String password ;//= "49"
	private int port ;//= 2323
	private String ip ;//= "localhost"
	private Client client;
	private Socket clientSocket;
	private boolean isConnect;
	private DataInputStream dis = null;
	private boolean flag = false;
	

	public CheckServerConnectionAndUserInfo() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	public void connectToServer() {
		

//		int port = 2323;//Integer.parseInt(portnumberInput.getText());
//		String ip = "localhost";//ipAddressInput.getText();

//		if(serverController.server.isStart() == true) {			
//			if (port == serverController.getPort()
//					
//					&& ((ip.equals("localhost")) || (ip.equals("127.0.0.1")))) {

//		clientSocket = client.getClientSocket();
//		clientSocket = this.businessLogic.getCurrentClient().getClientSocket();
		
		try {
			clientSocket = new Socket(ip, port);
			isConnect = true;		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//			new Thread(new Receive()).start();
//			Client client = new Client(port, ip, this);
//			new Thread(client).start();

//			} else {
//				messageDisplay.appendText("Port number or Ip address is invalid, input again!\n");
//			}		
//		}else {
//			messageDisplay.appendText("Server has not been started, please start firstly!\n");
//		}

	}

	public void sendUsernameAndPasswordToServer() {

		if (isConnect) {

			userName = this.client.getUserName();
			password = this.client.getPassword();

			//client = new Client(userName, password, clientSocket);
					
			String userInfo = userName + "," + password;
			
			try {
				OutputStream osOutputStream = clientSocket.getOutputStream();
				PrintWriter pw = new PrintWriter(osOutputStream);
//				oos = new ObjectOutputStream(clientSocket.getOutputStream());
//				oos.writeObject(client);
				pw.println(userInfo);
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			//invalidFeedback.setText("not connect server!");
			System.out.println("not connect server");
		}

	}

	public boolean receiveCheckResultFromSever() {

//		boolean flag = false;
		try {
//			ois = new ObjectInputStream(clientSocket.getInputStream());
//			while(true) {
//				Object client = ois.readObject();

			dis = new DataInputStream(clientSocket.getInputStream());
			String resultStr = dis.readUTF();
			if (resultStr.equals("success")) {
				flag = true;
			} else {
				System.out.println(resultStr);
				flag = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return flag;

	}
	
	
}
