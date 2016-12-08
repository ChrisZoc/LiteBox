package serial;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Chunk implements Serializable{
	
	private byte id;
	private byte[] nombre;
	private byte[] info;
	
	public int getId() {
		return (int) id;
	}
	public void setId(int id) {
		this.id = (byte) id;
	}
	public String getNombre() {
		return new String(nombre, StandardCharsets.UTF_8);
	}
	public void setNombre(String nombre) {
		this.nombre = nombre.getBytes(StandardCharsets.UTF_8);
	}
	public byte[] getInfo() {
		return info;
	}
	public void setInfo(byte[] info) {
		this.info = info;
	}
}