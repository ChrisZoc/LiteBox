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
import java.util.Arrays;
import java.util.Collections;

public class p2p {
	
public static void main(String args[]){
	startServer();
    startClient();
}


private static void startClient() {
	(new Thread(){
	
		@Override
		public void run(){
    try {
      // Create a socket
      Socket soc = new Socket(InetAddress.getLocalHost(), 60020);
      OutputStream o = soc.getOutputStream();
      ObjectOutput s = new ObjectOutputStream(o);
      File carpeta = new File("./carpetaCompartida");
      Chunk test = new Chunk();
      File archivo=null;
	  
      if (!carpeta.exists()) { 
    	 
    	  carpeta.mkdirs();
    	  System.out.println("se creo la carpeta compartida");
      
      }else{
    	  System.out.println("la carpeta ya existe");
      }
      
      String[] ficheros=carpeta.list();
      String[] newFicheros;
      for (int i = 0; i < ficheros.length; i++) {
  		System.out.println(ficheros[i]);
  	}
      while(true){
    	  newFicheros=carpeta.list();
    	  if(ficheros.length!=newFicheros.length){
    		  break;
    	  }
      }
      System.out.println("salio del while");
      ArrayList ficherosArr = new ArrayList();
      ArrayList newFicherosArr = new ArrayList();
      Collections.addAll(ficherosArr, ficheros);
      Collections.addAll(newFicherosArr, newFicheros);
      newFicherosArr.removeAll(ficherosArr);
      for (int i = 0; i < newFicherosArr.size(); i++) {
		
    	System.out.println(newFicherosArr.get(i));
		archivo= new File("./carpetaCompartida/"+newFicherosArr.get(i));
		Path path = Paths.get("./carpetaCompartida/"+newFicherosArr.get(i));
		byte[] data = Files.readAllBytes(path);
		test.setInfo(data);
		test.setNombre(String.valueOf(newFicherosArr.get(i)));
		test.setId(i);
	    s.writeObject(test);
	}
      
      
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
				File carpeta= new File("./carpetaRec");
				if (!carpeta.exists()) { 
			    	  carpeta.mkdirs();
			    	  System.out.println("se creo la carpeta compartida Rec");
			      
			      }else{
			    	  System.out.println("la carpeta Rec ya existe");
			      }
				FileOutputStream fos = new FileOutputStream("./carpetaRec/"+d.getNombre());
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


