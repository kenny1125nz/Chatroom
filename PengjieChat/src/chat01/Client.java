
package chat01;

import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;



public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;

	private String port;
	private Socket clientSocket;
	private HashMap<String, Client> clientsMap;
	
	
	

	public Client() {
		super();
	}
	public Client(String userName, String password, Socket clientSocket) {
		super();
		this.userName = userName;
		this.password = password;
		this.clientSocket = clientSocket;
	}
	public Client(String userName, String password, String port, Socket clientSocket,
			HashMap<String, Client> clientsMap) {
		super();
		this.userName = userName;
		this.password = password;
		this.port = port;
		this.clientSocket = clientSocket;
		this.clientsMap = clientsMap;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the clientSocket
	 */
	public Socket getClientSocket() {
		return clientSocket;
	}
	/**
	 * @param clientSocket the clientSocket to set
	 */
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	/**
	 * @return the clientsMap
	 */
	public HashMap<String, Client> getClientsMap() {
		return clientsMap;
	}
	/**
	 * @param clientsMap the clientsMap to set
	 */
	public void setClientsMap(HashMap<String, Client> clientsMap) {
		this.clientsMap = clientsMap;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
	
	
	
//	public Client(String userName, String password) {
//		super();
//		this.userName = userName;
//		this.password = password;
//	}
//
//	public Client(String userName, String password, String port) {
//		super();
//		this.userName = userName;
//		this.password = password;
//		this.port = port;
//	}
//
//	public String getUserName() {
//		return userName;
//	}
//	
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//	
//	public String getPassword() {
//		return password;
//	}
//	
//	public void setPassword(String password) {
//		this.password = password;
//	}
//	
//	public String getPortNumber() {
//		return port;
//	}
//	
//	public void setPortNumber(String port) {
//		this.port = port;
//	}
//
//	@Override
//	public String toString() {
//		return "Client [userName=" + userName + ", password=" + password + ", portNumber=" + portNumber + "]";
//	}
//
//	
//}

//	private final SimpleStringProperty name;
//	private final SimpleStringProperty ipAddress;
//	
//	public Client(String clientName, String ip) {
//		this.name = new SimpleStringProperty(clientName);
//		this.ipAddress = new SimpleStringProperty(ip);
//	}
	
//	public String getName() {
//		return name.get();
//	}
//	
//	public void setName(String clientName) {
//		name.set(clientName);
//	}
//	
//	public String getIpAddress() {
//		return ipAddress.get();
//	}
//	
//	public void setIpAddress(String ip) {
//		ipAddress.set(ip);
//	}
//	
//	@Override
//	public String toString() {
//		return "Client name=" + name + ", ipAddress=" + ipAddress;
//	}