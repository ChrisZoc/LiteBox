package serial;

import java.io.File;

public class listarCarpeta {
	
	File dir = new File("CarpetaAListar/");
	String[] ficheros = dir.list();
	long tam = dir.getTotalSpace();
	public File getDir() {
		return dir;
	}


	public void setDir(File dir) {
		this.dir = dir;
	}


	public String[] getFicheros() {
		return ficheros;
	}


	public void setFicheros(String[] ficheros) {
		this.ficheros = ficheros;
	}
}
