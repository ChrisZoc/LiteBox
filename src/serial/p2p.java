package serial;

import java.io.File;
import java.io.IOException;
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
import java.util.Collections;

public class p2p {

	public static void main(String args[]) throws IOException {
		startServer();
		startClient();
	}

	private static ArrayList<String> difference(String[] before, String[] after) {
		ArrayList<String> b = new ArrayList<String>();
		ArrayList<String> a = new ArrayList<String>();
		Collections.addAll(b, before);
		Collections.addAll(a, after);
		a.removeAll(b);
		return a;
	}

	private static void startClient() {
		(new Thread() {

			@Override
			public void run() {
				try {
					// Create a socket
					Socket soc = new Socket(InetAddress.getLocalHost(), 60020);
					OutputStream o = soc.getOutputStream();
					ObjectOutput s = new ObjectOutputStream(o);
					File carpeta = new File("./folderSend");
					Chunk test = null;
					if (!carpeta.exists()) {
						carpeta.mkdirs();
						System.out.println("No shared folder detected, creating.....Done!");

					} else {
						System.out.println("Shared folder /folderSend/ detected.");
					}

					String[] ficheros = carpeta.list();
					String[] newFicheros;
					System.out.println("Files in the shared folder:");
					for (int i = 0; i < ficheros.length; i++) {
						System.out.println("-" + ficheros[i]);
					}
					while (true) {
						newFicheros = carpeta.list();
						if (ficheros.length < newFicheros.length) { // new
																	// file
							System.out.println("Change detected in the folder......syncing");
							ArrayList<String> newFicherosArr = difference(ficheros, newFicheros);
							System.out.println(newFicherosArr.get(0));
							Path path = Paths.get("./folderSend/" + newFicherosArr.get(0));
							byte[] data = Files.readAllBytes(path);
							test = new Chunk();
							test.setInfo(data);
							test.setName(String.valueOf(newFicherosArr.get(0)));
							test.setId(0);
							s.writeObject(test);
							System.out.println("The file '" + newFicherosArr.get(0) + "' has been sent\n");

							s.flush();
							// s.close();
							ficheros = newFicheros;
						} else if (ficheros.length > newFicheros.length) { // delete
																			// file
							System.out.println("Change detected in the folder......syncing");
							ArrayList<String> newFicherosArr = difference(newFicheros, ficheros);
							System.out.println(newFicherosArr.get(0));
							test = new Chunk();
							test.setName(String.valueOf(newFicherosArr.get(0)));
							test.setId(-1);
							s.writeObject(test);
							System.out.println("Petition to delete '" + newFicherosArr.get(0) + "' has been sent\n");

							s.flush();
							// s.close();
							ficheros = newFicheros;
						}
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Error during serialization");
					System.exit(1);
				}
			}
		}).start();
	}

	private static void startServer() throws IOException {
		(new Thread() {

			@Override
			public void run() {
				ServerSocket ser = null;
				ServerThread rt;
				try {
					ser = new ServerSocket(60020);
				} catch (IOException e) {
					System.err.println("Could not listen on port: 60020.");
					System.exit(1);
				}

				Socket clientSocket = null;
				try {
					while (true) {
						clientSocket = ser.accept();
						rt = new ServerThread(clientSocket);
					}
				} catch (IOException e) {
					System.err.println("Accept failed.");
					System.exit(1);
				}

				try {
					ser.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
