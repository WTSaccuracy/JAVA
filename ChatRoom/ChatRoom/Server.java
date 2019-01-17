package ChatRoom;

import java.io.*;
import java.net.*;
import java.util.*;

import javafx.scene.input.*;
import javafx.stage.*;

public class Server extends Client {

	private ServerSocket ss;
	private final int backlog = 10;
	LinkedList<ListenThread> threads = new LinkedList<ListenThread>();// server connect with clients
	private Iterator<ListenThread> itr;
	boolean isOn = false;
	
	public Server(Stage stage) {
		super(stage);
		idTextField.setText("Server");
		// create server
		start.setOnAction(e -> {
			build_ServerSocket_and_list_ServerInfo();
			new Thread(new listenClient()).start();// keep listening join request from client
		});
		// shut down server
		exit.setOnAction(e -> {
			close_socket();
		});
		// input message
		inputTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				// read message
				if (isOn) {
					Message msg = new Message(id, inputTextField.getText());
					String msgStr = msg.getStr();
					sendToAll(msgStr);
					inputTextField.setText("");
				} else {
					text.appendText("server is offline!\n");
				}
			}
		});

		stage.setTitle("Server");

	}

	private void build_ServerSocket_and_list_ServerInfo() {
		try {
			ss = new ServerSocket(Integer.valueOf(portTextField.getText()), backlog,
					InetAddress.getByName(ipTextField.getText())); // build
			id = idTextField.getText();
			threads.clear();
			ListenThread.setThreads(threads);
			text.appendText("Server is online!\n");
			lock();
			isOn = true;
		} catch (Exception e) {
			text.appendText("Port has been used!\n");
		}
	}

	// keep listening join request from client
	class listenClient implements Runnable {
		Socket cs;

		public void run() {
			while (!ss.isClosed()) {
				try {
					cs = ss.accept();
					ListenThread lt = new ListenThread(cs, text,id, true);// listening message from client
					new Thread(lt).start();
					threads.add(lt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void close_socket() {
		sendToAll("Server Shutdown!");
		try {
			ss.close();
			itr = threads.iterator();
			while (itr.hasNext()) {
				itr.next().closeSocket();
			}
			release();
			isOn = false;
		} catch (IOException e) {
			e.printStackTrace();
			text.appendText("server close_socket error\n");
		}
	}

	private void sendToAll(String str) {
		// send message to all client
		itr = threads.iterator();
		while (itr.hasNext()) {
			try {
				itr.next().getDataOutputStream().writeUTF(str);
			} catch (Exception ex) {

			}
		}
		text.appendText(str + "\n");// input message in server's chatRoom
	}
}
