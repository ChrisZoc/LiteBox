package serial;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread implements Runnable {
	Thread runner;
	Socket soc;
	Chunk toSend;

	public ClientThread(String ip, int port, Chunk toSend) {
		runner = new Thread(this);
		try {
			soc = new Socket(ip, port);
		} catch (UnknownHostException e) {
			System.out.println("Host at " + ip + "is down.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.toSend = toSend;
		System.out.println("Initializing ClientThread to " + ip + "...");
		runner.run();
	}

	@Override
	public void run() {
		try {
			OutputStream o = soc.getOutputStream();
			ObjectOutput s = new ObjectOutputStream(o);
			s.writeObject(toSend);
			System.out.println(
					"Chunk with id '" + toSend.getId() + "' of file '" + toSend.getName() + "' has been sent!");
			s.flush();
			s.close();
			System.out.println("Terminating ClientThread...");
			runner.join();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error during serialization");
			System.exit(1);
		}
	}

}