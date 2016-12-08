package serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ListaIPs implements Serializable {

	private ArrayList<String> iplist;

	public ListaIPs() {
		super();
		iplist = new ArrayList<String>();
		BufferedReader input = null;
		String ip;
		try {
			File inputFile = new File("iplist.txt");
			input = new BufferedReader(new FileReader(inputFile));
			while (input.ready()) {
				ip = input.readLine();
				iplist.add(ip);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public ArrayList<String> getIplist() {
		return iplist;
	}

}
