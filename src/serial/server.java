package serial;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class server implements Runnable {
	Thread runner;
	Socket soc;

	public server(Socket ss) {
	runner= new Thread(this);
	soc = ss;
	runner.run();
	}

	@Override
	public void run() {
		try{
			Chunk d = null;
			InputStream o =null;
			ObjectInput s = null;
			File carpeta = new File("./folderRec");
			if (!carpeta.exists()) {
				carpeta.mkdirs();
				System.out.println("se creo la carpeta compartida Rec");

			} else {
				System.out.println("the shared folder it already exists ");
			}
			FileOutputStream fos=null;
			while (true) {
				o = soc.getInputStream();
				s = new ObjectInputStream(o);
				try{
				d = (Chunk) s.readObject();
				
					System.out.println("The file'" + d.getNombre() + "' has been Received ");
					fos = new FileOutputStream("./folderRec/" + d.getNombre());
					fos.write(d.getInfo());
					fos.close();

					
					
				}catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//s.close();		
	}
}
