package serial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerThread implements Runnable {
	Thread runner;
	Socket soc;

	public ServerThread(Socket ss) {
		runner = new Thread(this);
		soc = ss;
		System.out.println("Initializing ServerThread...");
		runner.run();
	}

	@Override
	public void run() {
		try {
			Chunk d = null;
			InputStream o = null;
			ObjectInput s = null;
			FileOutputStream fos = null;
			o = soc.getInputStream();
			s = new ObjectInputStream(o);
			try {
				d = (Chunk) s.readObject();

				if (d.getId() == -1) {
					File toDelete = new File("./folderRec/" + d.getName());
					if (toDelete.delete()) {
						System.out.println(d.getName() + " has been deleted!");
					} else {
						System.out.println("Delete operation has failed.");
					}
				} else {
					System.out.println("The file '" + d.getName() + "' has been Received ");
					fos = new FileOutputStream("./folderRec/" + d.getName());
					fos.write(d.getInfo());
					fos.close();
				}
				System.out.println("Terminating ServerThread...");
				soc.close();
				runner.join();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// s.close();
	}
}
