
package chat01;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Server implements Runnable {

	ServerSocket serverSocket;
	private int port;
	private Observable uiComponent;
	private static ArrayList<ClientProcessor> clientList = new ArrayList<ClientProcessor>();// socket集合
//	ObservableList<Client> ipAddressList;
	private boolean isStart = false;

	private Map<String, ClientProcessor> clients = new HashMap<>();

	public Server(int port, Observable uiComponent) {
		super();
		this.port = port;
		this.uiComponent = uiComponent;
	}

	@Override
	public void run() {
		try {
			// new ServerSocket 传port进去 意味服务器已经启动
			serverSocket = new ServerSocket(port);
			isStart = true;
			System.out.println(port + " is ready, waiting for clients to connect");

			while (isStart) {
				// 阻塞性方法 只连上了一个客户端 应放到永真循环里 就可以链接多个客户端
				Socket socket = serverSocket.accept();
				ClientProcessor clientProcessor = new ClientProcessor(socket, uiComponent);
				clientList.add(clientProcessor);			
				// new Thread可以放在这里 也可以放在ClientProcessor的构造器里
				// new Thread(clientProcessor).start();
			}
		} catch (SocketException e) {
			System.out.println("Server has broken off!");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// 内部类 内部类是解决起线程的问题 observable是解决数据更新后刷新界面问题
	private static class ClientProcessor implements Runnable {

		private Socket socket;
		private Observable uiComponent;
		private JDBC jdbc = new JDBC();
		private String senderName;
		private String receiverName;
		private String messageContent;
		String resultStr = null;

		private ClientProcessor(Socket socket, Observable uiComponent) {
			this.socket = socket;
			this.uiComponent = uiComponent;
			new Thread(this).start();
		}

		@Override
		public void run() {

			connentDBcheckUsernameAndPassword(receiveUsernameAndPasswordFromLoginPage());		
			uiComponent.append("one client connected, ip address is: " + socket.getInetAddress() + "=》 Port is: "
					+ socket.getPort());
			receiveMessageFromClient();
			
		}

		public String[] receiveUsernameAndPasswordFromLoginPage() {// Client

			String userInfo = null;
			try {
				InputStream inputStream = socket.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				userInfo = bufferedReader.readLine();
				//System.out.println("before split " + userInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return userInfo.split(",");

			// try {
			// ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			// client = (Client)ois.readObject();
			// DataInputStream dis = new DataInputStream(socket.getInputStream());
			// return dis.readUTF().split(",");
			//

			// } catch (IOException e) {
			// e.printStackTrace();
			// } catch (ClassNotFoundException e) {
			// e.printStackTrace();
			// }
			// return client;

		}

		public void connentDBcheckUsernameAndPassword(String[] userInfo) {// Client client

			PreparedStatement ptmt;
			DataOutputStream dos;

			try {
				String sql = "SELECT * FROM `usertable` WHERE username=? AND password=?";
				ptmt = jdbc.getConnection().prepareStatement(sql);
//				ptmt.setString(1, client.getUserName());
//				ptmt.setString(2, client.getPassword());										
				ptmt.setString(1, userInfo[0]);
				ptmt.setString(2, userInfo[1]);

				ResultSet rSet = ptmt.executeQuery();
				if (rSet.next()) {				
					dos = new DataOutputStream(this.socket.getOutputStream());
					resultStr = "success";
					dos.writeUTF("success");

				} else {
					dos = new DataOutputStream(this.socket.getOutputStream());
					resultStr = "failed";
					dos.writeUTF("failed");

				}
				rSet.close();
				ptmt.close();
				jdbc.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

		public void connectDBToSaveData() {
			
			PreparedStatement ptmt;		
			String sql = "INSERT INTO `messagetable`(`sendername`, `receivername`, `content`) VALUES (?,?,?)";
			try {
				ptmt = jdbc.getConnection().prepareStatement(sql);
				ptmt.setString(1, senderName);
				ptmt.setString(2, receiverName);
				ptmt.setString(3, messageContent);
//				System.out.println("sendername: " + senderName);
//				System.out.println(receiverName);
//				System.out.println("message: " + messageContent);
				
				ptmt.executeUpdate();
				
				ptmt.close();
				jdbc.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		}

		// 服务器端接收数据的方法 且是同时接客户信息
		public void receiveMessageFromClient() {

			try {
				//DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				while (true) {
										
					InputStream inputStream = socket.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String strReceived = bufferedReader.readLine();
					uiComponent.append(strReceived);
					strReceived = strReceived.substring(2);
					
					senderName = strReceived.split(" said: ")[0];
					receiverName = "not finish";
					messageContent = strReceived.split(" said: ")[1];					
					connectDBToSaveData();
					
					// 服务器读到的信息 也需要写进循环
//					String strReceived = dis.readUTF();				
//					System.out.println("server received utf: " + strReceived);
					// 写到console和serverLabel里
					//System.out.println(socket.getPort() + " said: " + strReceived);
//					uiComponent.append(socket.getPort() + " said: " + strReceived);
//					String serverSentToClientStr = socket.getPort() + " said: " + strReceived;
					// 遍历ClientList 调用下面这个方法 且：在客户端接收信息也需要多线程的接
//					for (ClientProcessor c : clientList) {
//						c.sendMessageToClients(serverSentToClientStr);
//					}
					Iterator<ClientProcessor> iterator = clientList.iterator();
					while(iterator.hasNext()) {
						ClientProcessor c = iterator.next();
						c.sendMessageToClients(strReceived);

					} 
				}
			} catch (SocketException e) {
				System.out.println("One client is offline");
				uiComponent.append(socket.getPort() + " is offline!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 服务器端发送数据的方法 且是同时发送给客户 需要遍历ClientList
		public void sendMessageToClients(String strSend) {

			try {
				//System.out.println("sara1: " + strSend);
				DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());						
				String utf8EncodedString = new String(strSend.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);											
				// 发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法
				dos.writeUTF(strSend+"\r\n");
				//	dos.writeChars(strSend);	
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
