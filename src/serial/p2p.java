package serial;

import java.awt.List;
import java.io.File;
import java.io.FileOutputStream;
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

public class p2p {

	public static void main(String args[]) {
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
					OutputStream o = soc.getOutputStream();
					ObjectOutput s = new ObjectOutputStream(o);
					listarCarpeta list = new listarCarpeta();
					list.setDir(new File("./carpetaCompartida"));

					if (list.ficheros == null) {
						System.out.println("No hay ficheros en el directorio especificado");
					} else {
						for (int x = 0; x < list.ficheros.length; x++)
							System.out.println(list.ficheros[x]);
					}

					Chunk test = new Chunk();
					Path path = Paths.get(filepath());
					byte[] data = Files.readAllBytes(path);
					test.setInfo(data);
					test.setNombre(fileName());
					test.setId(0);
					s.writeObject(test);
					s.flush();
					s.close();

				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Error during serialization");
					System.exit(1);
				}
			}
		}).start();
	}

	private static void startServer() {

		(new Thread() {
			@Override
			public void run() {

				ServerSocket ser = null;
				Socket soc = null;
				Chunk d = null;

				try {
					ser = new ServerSocket(60020);
					/*
					 * This will wait for a connection to be made to this
					 * socket.
					 */
					soc = ser.accept();
					InputStream o = soc.getInputStream();
					ObjectInput s = new ObjectInputStream(o);
					d = (Chunk) s.readObject();
					s.close();

					FileOutputStream fos = new FileOutputStream(d.getNombre());
					fos.write(d.getInfo());
					fos.close();

				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Error during serialization");
					System.exit(1);

				}
			}
		}).start();
	}
}
