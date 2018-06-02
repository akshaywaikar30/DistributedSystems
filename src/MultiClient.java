//Name: AkshayMaheshWaikar
//ID: 1001373973
//Mahrsee, Rishabh. “Multi-Threaded Chat Application.” GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-1/.
//Mahrsee, Rishabh. "Multi-Threaded Chat Application." GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-2/.
//https://stackoverflow.com/questions/15247752/gui-client-server-in-java
//http://www.jmarshall.com/easy/http/ HTTP Made Really Easy. 
import java.awt.event.ActionEvent; 			//All the header files are here
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class MultiClient extends JFrame implements ActionListener
{
	final static int ServerPort = 1234;	
	static Socket s;					
	static JTextArea txtFromClient;
	static JTextArea txtdisp;
	JButton sendText;
	public MultiClient(){										//it runs the constructor and enables all gui properties														
		this.setTitle("Client");																								
		this.setSize(1366, 768);										//Creating a Frame for client														
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);																			
		getContentPane().setLayout(null);																						

		txtFromClient = new JTextArea();							// creates a TextArea for client to type messages
		txtFromClient.setBounds(350, 600, 700, 100);				//This determines size of the TextArea
		add(txtFromClient);											//adds the TextArea to the container
		
		txtdisp = new JTextArea();								// creates TextArea to display messages broadcasted by server
		txtdisp.setBounds(350, 15, 700, 550);					//This determines size of the TextArea
		txtdisp.setEditable(false);							//Makes the TextArea non editable
		add(txtdisp);											//adds the TextArea to the container

		sendText = new JButton("Send");						// creates button for client to send the text which is txtFromClient
		sendText.setBounds(1100, 600, 130, 25);				//This determines size of the TextArea
		sendText.addActionListener(this);					//passes button object to send data after clicking it
		add(sendText);										//adds button to the container
		this.setVisible(true);								// makes GUI visible

	}

	@Override
	public void actionPerformed(ActionEvent ae) {					//method gets called on click of send buttons
		if (ae.getSource().equals(sendText)) {						//It checks which button to be called
			try {
				sendMsg();											//calls the sendMsg() function
			} catch (Exception e) {									//Handles exception if any occurred
				e.printStackTrace();
			}
		}
	}

	public void sendMsg()  throws UnknownHostException, IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());		//sends data across the network and InputStream can accept it on other end
		String word = txtFromClient.getText().trim();							//gets the data from the text area and assigns to word variable
		dos.writeUTF(word);														// read word from the client and write to server
	        txtFromClient.setText("");										
	}

	public static void main(String args[]) throws UnknownHostException, IOException 
	{
		new MultiClient();												//Object is called on 

		InetAddress ip = InetAddress.getByName("localhost");
		s = new Socket(ip, ServerPort);					
		while(true) {
			DataInputStream dis = new DataInputStream(s.getInputStream());		//accepts the data from the OutputStream
			String msg = dis.readUTF();											//reads the message from InputStream
			txtdisp.append(msg);												//appends the message to the TextArea 
			
		}
	}
}
