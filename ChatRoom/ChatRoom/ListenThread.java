package ChatRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import javafx.scene.control.*;

class ListenThread implements Runnable {
	private Socket cs;
	private TextArea text;
	private String inData, id;
	private DataInputStream in;
	private DataOutputStream out;

	private boolean isServer = false;

	static private LinkedList<ListenThread> threads;
	private Iterator<ListenThread> itr;

	public ListenThread(Socket clientSocket, TextArea text, String id, boolean isServer) {
		cs = clientSocket;
		this.text = text;
		this.isServer = isServer;
		this.id = id;
		try {
			in = new DataInputStream(cs.getInputStream());
			out = new DataOutputStream(cs.getOutputStream());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void run() {

		// send client login message
		if (!isServer) {
			try {
				out.writeUTF(id + " Login!");
			} catch (IOException ioe) {

			}
		}

		while (!cs.isClosed() && cs.isConnected()) {
			try {
				inData = in.readUTF();// read message
			} catch (IOException ioe) {
				break;
			}
			if (!inData.isEmpty()) {
				text.appendText(inData + "\n");

				if (isServer) {// server read client's message and send it to all client
					itr = threads.iterator();

					while (itr.hasNext()) {
						try {
							itr.next().out.writeUTF(inData);
						} catch (IOException ioe) {
							break;
						}
					}
				}
				inData = "";
			}
		}
		//remove this thread when client leave
		if (isServer) {
			threads.remove(this);
		}

	}

	public static void setThreads(LinkedList<ListenThread> threads) {
		ListenThread.threads = threads;
	}

	public DataOutputStream getDataOutputStream() {
		return out;
	}

	public void closeSocket() {
		try {
			cs.close();
		} catch (Exception ex) {
			text.appendText("ListenThread closeSocket error!\n");
			ex.printStackTrace();
		}
	}
}
