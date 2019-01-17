package ChatRoom;

import javafx.stage.*;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class Client {
	Scene scene;
	GridPane Gpane;

	private Socket cs;
	//private DataInputStream in;
	private DataOutputStream out;

	//TextArea text = new TextArea();//
	TextArea text = new TextArea();//
	String id;//

	Label idLable = new Label("ID :");
	TextField idTextField = new TextField("Client");

	Label ipLable = new Label("IP :");
	TextField ipTextField = new TextField("127.0.0.1");

	Label portLable = new Label("Port :");
	TextField portTextField = new TextField("57968");

	Button start, exit;
	TextField inputTextField = new TextField();// input field

	boolean isLock=false;
	public Client(Stage stage) {
		// connect to allocate server
		start = new Button("Start");
		start.setOnAction(e -> {
			build_client_socket();
		});
		// disconnect to server
		exit = new Button("Exit");
		exit.setOnAction(e -> {
			close_socket();
		});
		exit.setDisable(true);
		text.setEditable(false);
		// input message
		inputTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				try {
					Message msg = new Message(id, inputTextField.getText());
					String msgStr = msg.getStr();
					inputTextField.setText("");
					out.writeUTF(msgStr);
				} catch (Exception ex) {
					text.appendText("message input error\n");
				}
				inputTextField.setText("");
			}
		});
		Gpane = new GridPane();
		Gpane.add(ipLable, 0, 0);
		Gpane.add(ipTextField, 1, 0);
		Gpane.add(portLable, 0, 1);
		Gpane.add(portTextField, 1, 1);
		Gpane.add(idLable, 0, 2);
		Gpane.add(idTextField, 1, 2);
		Gpane.add(start, 0, 3);
		Gpane.add(exit, 1, 3);
		text.setPrefSize(300, 700);
		Gpane.add(text, 0, 4, 2, 1);
		Gpane.add(inputTextField, 0, 5, 2, 1);

		scene = new Scene(Gpane, 300, 800);
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			close_socket();
			System.exit(0);
		});

		stage.setTitle("Client");
	}

	private void build_client_socket() {
		try {
			cs = new Socket(InetAddress.getByName(ipTextField.getText()), Integer.valueOf(portTextField.getText()));
			id = idTextField.getText();

			//in = new DataInputStream(cs.getInputStream());
			out = new DataOutputStream(cs.getOutputStream());

			new Thread(new ListenThread(cs, text,id, false)).start();
			//text.appendText("Login! success\n");
			lock();
		} catch (Exception e) {
			text.appendText("build_client_socket error\n");
		}
	}

	private void close_socket() {

		try {
			release();
			text.appendText("Logout!\n");
			out.writeUTF(id + " Logout!");
			// server斷線後client輸入文字導致再連線會socket write error
			// release()前置後暫時解決
			cs.close();

		} catch (Exception e) {
			
		}
	}

	void lock() {
		start.setDisable(true);
		exit.setDisable(false);
		idTextField.setEditable(true);
		ipTextField.setEditable(true);
		portTextField.setEditable(true);
		isLock=true;
	}

	void release() {
		start.setDisable(false);
		exit.setDisable(true);
		idTextField.setEditable(false);
		ipTextField.setEditable(false);
		portTextField.setEditable(false);
		isLock=false;
	}
}
