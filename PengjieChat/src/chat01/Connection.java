package chat01;///**
// * 
// */
//package chat01;
//
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//public class Connection {
//
//	
//	Socket clientSocket;
//	boolean isConnect = false;
//	
//	public boolean connectToServer(String userName, String password, String ip, int port ) {
//		
//		boolean loggedInPasswordOK = false;
//		try {
//			clientSocket = new Socket(ip, port);
//			isConnect = true;		
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		if(isConnect) {
//			sendUsernameAndPasswordToServer(userName, password);
//			loggedInPasswordOK  = receiveCheckResultFromSever();
//		}
//		return loggedInPasswordOK;
//		
//	}
//	
//	
//	private void sendUsernameAndPasswordToServer(String userName, String password) {
//
//		if (isConnect) {
//
//					
//			String userInfo = userName + "," + password;
//			
//			try {
//				OutputStream osOutputStream = clientSocket.getOutputStream();
//				PrintWriter pw = new PrintWriter(osOutputStream);
//
//				pw.println(userInfo);
//				pw.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		} else {
//			System.out.println("not connect");
//		}
//
//	}
//	
//	
//	private boolean receiveCheckResultFromSever() {
//
//		boolean flag = false;
//		try {
//
//			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
//			String resultStr = dis.readUTF();
//			if (resultStr.equals("success")) {
//				flag = true;
//			} else {
//				//invalidFeedback.setText(resultStr);
//				flag = false;
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return flag;
//
//	}
//}
