package serial;

import java.awt.List;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class p2p {

	public static void main(String args[]) throws IOException {
		startServer();
		startClient();
	}

	private static void startClient() {
		(new Thread() {

			@Override
			public void run() {
				try {
					// Create a socket
					Socket soc = new Socket(InetAddress.getLocalHost(), 60020);
					File carpeta = new File("./folderSend");
					Chunk test = new Chunk();
					File archivo = null;

					if (!carpeta.exists()) {

						carpeta.mkdirs();
						System.out.println("se creo la carpeta compartida");

					} else {
						System.out.println("the folder folderSend it already exists");
					}

					String[] ficheros = carpeta.list();
					String[] newFicheros;
					System.out.println("Files in the shared folder:");
					for (int i = 0; i < ficheros.length; i++) {
						System.out.println("-" + ficheros[i]);
					}
					while (true) {
						soc=new Socket(InetAddress.getLocalHost(), 60020);
						OutputStream o = soc.getOutputStream();
						ObjectOutput s = new ObjectOutputStream(o);
						while (true) {
							newFicheros = carpeta.list();
							if (ficheros.length != newFicheros.length) {
								break;
							}
						}
						System.out.println("Change detected in the folder......syncing");
						ArrayList ficherosArr = new ArrayList();
						ArrayList newFicherosArr = new ArrayList();
						Collections.addAll(ficherosArr, ficheros);
						Collections.addAll(newFicherosArr, newFicheros);
						newFicherosArr.removeAll(ficherosArr);
						System.out.println(newFicherosArr.get(0));
						archivo = new File("./folderSend/" + newFicherosArr.get(0));
						Path path = Paths.get("./folderSend/" + newFicherosArr.get(0));
						byte[] data = Files.readAllBytes(path);
						test.setInfo(data);
						test.setNombre(String.valueOf(newFicherosArr.get(0)));
						test.setId(0);
						s.writeObject(test);
						System.out.println("the file '" + newFicherosArr.get(0) + "' has been sent\n");
						//soc.close();
						s.flush();
						// s.close();
						ficheros = newFicheros;
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Error during serialization");
					System.exit(1);
				}
			}
		}).start();
	}

	private static void startServer() throws IOException{
		(new Thread() {

			@Override
			public void run() {
		 
		try { 
	        //final ExecutorService service = Executors.newCachedThreadPool();
			ServerSocket ser = new ServerSocket(60020);
			while(true){
	        	System.out.println("estoy escuchando");
        	Socket clientSocket = ser.accept();
        	
            //service.execute(new server(clientSocket));
        	server servidor= new server(clientSocket);
        	Thread t = new Thread(servidor);
        	t.start();
        	System.out.println(clientSocket.isClosed());
        	}}
        catch (IOException e) 
        {
            System.err.println("Could not listen on port: 60021.");
            System.exit(1);
        }
	        }
			}).start();
		}
}

