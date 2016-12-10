package serial;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class p2p {

	private static int port = 60020;

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
				ListaIPs iplist = new ListaIPs();

				File sharedFolder = new File("./folderSend");

				Chunk toSend = null;

				if (!sharedFolder.exists()) {
					sharedFolder.mkdirs();
					System.out.println("No shared folder detected, creating.....Done!");

				} else {
					System.out.println("Shared folder /folderSend/ detected.");
				}

				String[] actualFileList = sharedFolder.list();
				String[] newFileList;
				System.out.println("Files in the shared folder:");
				for (int i = 0; i < actualFileList.length; i++) {
					System.out.println("> " + actualFileList[i]);
				}

				while (true) {
					newFileList = sharedFolder.list();
					if (actualFileList.length < newFileList.length) { // new file
						System.out.println("Change detected in the folder......syncing");
						ArrayList<String> unsyncedFiles = difference(actualFileList, newFileList);
						System.out.println(unsyncedFiles.get(0));
						Path path = Paths.get("./folderSend/" + unsyncedFiles.get(0));
						byte[] data;
						try {
							data = Files.readAllBytes(path);
							toSend = new Chunk();
							toSend.setInfo(data);
							toSend.setName(String.valueOf(unsyncedFiles.get(0)));
							toSend.setId(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						for (String ip : iplist.getIplist()) {
							new ClientThread(ip, port, toSend);
						}

						actualFileList = newFileList;
					} else if (actualFileList.length > newFileList.length) { // delete
																		// file
						System.out.println("Change detected in the folder......syncing");
						ArrayList<String> unsyncedFiles = difference(newFileList, actualFileList);
						System.out.println(unsyncedFiles.get(0));
						toSend = new Chunk();
						toSend.setName(String.valueOf(unsyncedFiles.get(0)));
						toSend.setId(-1);

						for (String ip : iplist.getIplist()) {
							new ClientThread(ip, port, toSend);
						}

						actualFileList = newFileList;
					}
				}
			}
		}).start();
	}

	private static void startServer() throws IOException {
		(new Thread() {

			@Override
			public void run() {
				ServerSocket ser = null;
				File sharedFolder = new File("./folderRec");
				if (!sharedFolder.exists()) {
					sharedFolder.mkdirs();
					System.out.println("No shared folder detected, creating.....Done!");

				} else {
					System.out.println("Shared folder /folderRec/ detected.");
				}
				try {
					ser = new ServerSocket(port);
				} catch (IOException e) {
					System.err.println("Could not listen on port: " + port + ".");
					System.exit(1);
				}

				Socket clientSocket = null;
				try {
					while (true) {
						clientSocket = ser.accept();
						new ServerThread(clientSocket);
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
