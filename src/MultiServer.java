//Name: AkshayMaheshWaikar
//ID: 1001373973
//Mahrsee, Rishabh. “Multi-Threaded Chat Application.” GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-1/.
//Mahrsee, Rishabh. "Multi-Threaded Chat Application." GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-2/.
//https://stackoverflow.com/questions/15247752/gui-client-server-in-java
//http://www.jmarshall.com/easy/http/ HTTP Made Really Easy. 
import java.awt.ScrollPane;
import java.io.DataInputStream;			// All the header files 
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Server class
public class MultiServer  extends JFrame 
{
	static ArrayList<ClientHandler> clientlist = new ArrayList<ClientHandler>();

	
	static int i = 0;							// counter for clients
	static long previous_st = 0;
	static JTextArea lblWord;	
	public MultiServer(){						// Initialised at the time of main method call
		this.setTitle("Server");			
		this.setSize(1366, 768);												// --  Set all the GUI properties
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);					
		getContentPane().setLayout(null);									
		
		
		lblWord = new JTextArea();										// creating text field to show client response on server
		lblWord.setAutoscrolls(true);
		lblWord.setBounds(350, 15, 750, 725);
		lblWord.setEditable(false);
		add(lblWord);

		this.setVisible(true);												// making GUI visible
	}



	public static void main(String[] args) throws IOException 
	{		
		new MultiServer();										//Calling Constructor
		// server is listening on port 1234
		ServerSocket ss = new ServerSocket(1234);					//Declaring the port number for communication
		Socket s;													//Creating socket for transferring messages

		// running infinite loop for getting messages
		while (true) 
		{
			s = ss.accept(); 										// Accept the incoming request
			// obtain input and output streams
			DataInputStream dis = new DataInputStream(s.getInputStream());	
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			//Declaring variables for GET request
			String get="GET HTTP/1.1 Host:localhost";	
			String agent="HTTPTool/1.0 \\n";
			Date d2= new Date();			
			String date2=d2.toString();		//gets the dynamic date and time
			//displays message on the server's text area
			lblWord.append(get+"\n"+"From: Client"+i+"\n"+"User-Agent:"+agent+"\n"+"Date:"+date2+"\n"+"Creating a new client: User "+i+"\n");
			
			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s,"\n" + "client" + i, dis, dos,i, previous_st);

			// Create a new Thread with this object.
			Thread t = new Thread(mtch);

			// add this client to active clients list
			clientlist.add(mtch);

			// start the thread.
			t.start();

			// increment i for new client.
			// i is used for naming only, and can be replaced
			// by any naming scheme
			i++;

		}
	}
}

// ClientHandler class
class ClientHandler implements Runnable 
{
	Scanner scn = new Scanner(System.in);
	
	private String name;						//declaring variables to store client name
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	int i;
	long last_ts;
	boolean isloggedin;

	// constructor
	public ClientHandler(Socket s, String name,
			DataInputStream dis, DataOutputStream dos,int i, long last_ts) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
		this.i =i;
		this.last_ts = last_ts;
		this.isloggedin=true;
	}

	@Override
	public void run() {

		String received;
		long timestamp_fl = 0;
		try
		{
		while (true) 
		{
				// receives the string from the client
				received = dis.readUTF();
				Date d= new Date();
				String date=d.toString();			
				String post="POST HTTP/1.1";
				String type="application/x-www-form-urlencoded";
				int length=received.length();
				
				MultiServer.lblWord.append("\n"+post+"\n"+				//displays the message from client on to the server with http post method
						"From: Client"+i+"\n"+
						"Date:"+date+"\n"+ 
						"Content-Type:"+type+"\n"+ 
						"Content-Length:"+length+"\n"+received+"\n"); 
				if(received.equals("logout")){										//shows the logout functionality 
					MultiServer.lblWord.append("Client"+i+ "has logged out");		// where if client types logout it discontinues that client thread
					for(ClientHandler it:MultiServer.clientlist) {					//and broadcasts the message to other active clients
						it.dos.writeUTF(this.name+" has logged out");
					}
					this.isloggedin=false;
					this.s.close();
					break;
				}
				
			
				  
				// break the string into message and recipient part
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				for (ClientHandler it : MultiServer.clientlist) {
			        	long current_st=System.currentTimeMillis();
			        	if(MultiServer.previous_st == 0)				//checks if previous time is zero
			        	{
			        		MultiServer.previous_st = current_st;		// assigns it to current time
			        	}
			        	
			        	timestamp_fl = current_st - MultiServer.previous_st;  	//saves the difference between second message and first message
			        	
			        	if(this.name.equals(it.name))					//if another clients joins the network it displays the constant time throughtout all clients
				        {
			        		MultiServer.previous_st = current_st;
			        	}
			        
			        	int seconds = (int) (timestamp_fl/1000);			//calculates seconds and stores as an integer
			        	int min = seconds/60;								//calculates minutes and stores as an integer
			        	seconds = seconds - (min * 60);						//if seconds go above 60 increments minute and recalculates seconds
			        
			        String interval = String.format("%02d", min);			//string format used to match the requirement of project
			        interval = interval + ":";
			        String sec = String.format("%02d", seconds);
			        interval = interval + sec;
		            it.dos.writeUTF(this.name+								//sends message to all active clients with the correct timer
						" : "+MsgToSend+"\t"+ interval);
				}
				String res="Client:"+i+" "+MsgToSend+"\n";
				//Creating file to store communication among all the active clients
				File yourFile = new File("C:\\Users\\aksha\\eclipse-workspace\\DS_Project_1\\data.txt");
				yourFile.createNewFile();
				Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DS_Project_1\\data.txt"), res.getBytes(), StandardOpenOption.APPEND);
				
		}
		
		}catch (IOException e) {		//catches all the exceptions for the try block

			e.printStackTrace();
		}

		try
		{
			// closing resources
			this.dis.close();
			this.dos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}