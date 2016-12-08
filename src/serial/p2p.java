package serial;

import java.awt.List;
import java.io.EOFException;
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
import java.util.Arrays;
import java.util.Collections;

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

					ListaIPs iplist = new ListaIPs();

					File carpeta = new File("./carpetaCompartida");
					Chunk test = new Chunk();
					File archivo = null;

					if (!carpeta.exists()) {

						carpeta.mkdirs();
						System.out.println("se creo la carpeta compartida");

					} else {
						System.out.println("la carpeta ya existe");
					}

					String[] ficheros = carpeta.list();
					String[] newFicheros;
					for (int i = 0; i < ficheros.length; i++) {
						System.out.println(ficheros[i]);
					}
					while (true) {

						newFicheros = carpeta.list();
						if (ficheros.length != newFicheros.length) {

							Socket soc = new Socket(iplist.getIplist().get(0), 60020);
							OutputStream o = soc.getOutputStream();
							ObjectOutput s = new ObjectOutputStream(o);

							System.out.println("Se detectó un cambio en la carpeta: sincronizando...");
							ArrayList<String> ficherosArr = new ArrayList<String>();
							ArrayList<String> newFicherosArr = new ArrayList<String>();
							Collections.addAll(ficherosArr, ficheros);
							Collections.addAll(newFicherosArr, newFicheros);
							newFicherosArr.removeAll(ficherosArr);
							for (String fichero : newFicherosArr) {

								System.out.println(fichero);

								archivo = new File("./carpetaCompartida/" + fichero);
								Path path = Paths.get("./carpetaCompartida/" + fichero);

								byte[] data = Files.readAllBytes(path);

								test.setInfo(data);
								test.setNombre(String.valueOf(fichero));
								test.setId(newFicherosArr.indexOf(fichero));

								s.writeObject(test);
								s.flush();

							}
							ficheros = carpeta.list();
							System.out.println("Sincronización exitosa.");
							s.close();
						}

						try {
							Thread.sleep(1000); // 1000 milliseconds
						} catch (InterruptedException ex) {
							Thread.currentThread().interrupt();
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

					File carpeta = new File("./carpetaRec");
					if (!carpeta.exists()) {
						carpeta.mkdirs();
						System.out.println("se creo la carpeta compartida Rec");
					} else {
						System.out.println("la carpeta Rec ya existe");
					}
					while (s.available() != 0) {
						d = (Chunk) s.readObject();
						FileOutputStream fos = new FileOutputStream("./carpetaRec/" + d.getNombre());
						fos.write(d.getInfo());
						fos.close();
						System.out.println(d.getNombre());
					}

					s.close();

				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Error during serialization");
					System.exit(1);

				}
			}
		}).start();
	}
}
