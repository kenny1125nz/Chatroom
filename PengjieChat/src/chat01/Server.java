
package chat01;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Server implements Runnable {

	ServerSocket serverSocket;
	private int port;
	private Observable uiComponent;
	private static ArrayList<ClientProcessor> clientList = new ArrayList<ClientProcessor>();// socket集合
//	ObservableList<Client> ipAddressList;
	private boolean isStart = false;

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

		private static Map<String, ClientProcessor> clientsMap = new HashMap<>();

		private Socket socket;
		private Observable uiComponent;
		private JDBC jdbc = new JDBC();
		private String senderName;
		private String receiverName;
		private String messageContent;
		String resultStr = null;
		String clientName ;

		private ClientProcessor(Socket socket, Observable uiComponent) {
			this.socket = socket;
			this.uiComponent = uiComponent;
			new Thread(this).start();
		}

		@Override
		public void run() {

			connentDBcheckUsernameAndPasswordEx(receiveUsernameAndPasswordFromLoginPage());
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

		public void connentDBcheckUsernameAndPasswordEx(String[] userInfo) {// Client client
			DataOutputStream dos = null;
			try {
				dos = new DataOutputStream(this.socket.getOutputStream());
				resultStr = "success";
				dos.writeUTF(resultStr);

				//add clients intoMap;
				clientName = userInfo[0];
				clientsMap.put(userInfo[0],this);

				broadCastClients(clientsMap.keySet());

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}


		private void broadCastClients(Set<String> clients) {
			StringBuffer clientsStr = new StringBuffer("C:");
			clients.forEach(c -> clientsStr.append(c + ","));
			String commaSeparatedClients = clientsStr.toString();

			clientsMap.values().forEach(c->c.sendMessageToClients(commaSeparatedClients));


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

					char msgType = strReceived.charAt(0);
					if (msgType == 'M') {
						int pos = strReceived.indexOf("_");
						String receiver = strReceived.substring(2, pos);
						String messageBody = strReceived.substring(pos + 1);
						String msg = "M:" + messageBody;
						if ("ALL".equals(receiver)) {
							broadcastMessage(msg);
						} else {
							clientsMap.get(receiver).sendMessageToClients(msg);
						}

						senderName = messageBody.split(" said: ")[0];
						receiverName = receiver;
						messageContent = messageBody.split(" said: ")[1];
						//connectDBToSaveData();
					}
					

					
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
//					Iterator<ClientProcessor> iterator = clientList.iterator();
//					while(iterator.hasNext()) {
//						ClientProcessor c = iterator.next();
//						c.sendMessageToClients("M:"+strReceived);
//					}
				}
			} catch (SocketException e) {
				System.out.println("One client is offline");
				uiComponent.append(clientName + " from :" + socket.getPort() + " is offline!");
				clientsMap.remove(clientName);

				broadCastClients(clientsMap.keySet());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		private void broadcastMessage(String strSend){
			clientsMap.values().forEach(c -> c.sendMessageToClients(strSend));
		}

		// 服务器端发送数据的方法 且是同时发送给客户 需要遍历ClientList
		public void sendMessageToClients(String strSend) {

			try {
				System.out.println("sending to all clients: " + strSend);
				//DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
				String utf8EncodedString = new String(strSend.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);											
				// 发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法

				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				PrintWriter pw = new PrintWriter(osw);
				pw.println(strSend);
				pw.flush();
				//	dos.writeChars(strSend);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
