package store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Servers {
	private int intialNumberOfServers = 10;
	private List<String> IpAddress ;
	private List<Integer> portNo ; 
	private static Map<String, Long> serverName;   //Value of type long is for no. of occurences
	
	public void initializeServers(){
		intializeIpAddress();
		initializePort();
		
		serverName = new LinkedHashMap<String, Long>();
		
		for(int i = 0; i<intialNumberOfServers;++i){
			String temp = IpAddress.get(i) +":"+ String.valueOf(portNo.get(i));
			serverName.put(temp, 0l);
		}
		
	}

	private void initializePort() {
		IpAddress = new ArrayList<String>();
		IpAddress.add("1.1.1.1");
		IpAddress.add("2.2.2.2");
		IpAddress.add("3.3.3.3");
		IpAddress.add("4.4.4.4");
		IpAddress.add("5.5.5.5");
		IpAddress.add("6.6.6.6");
		IpAddress.add("7.7.7.7");
		IpAddress.add("8.8.8.8");
		IpAddress.add("9.9.9.9");
		IpAddress.add("10.10.10.10");
	}

	private void intializeIpAddress() {
		portNo = new ArrayList<Integer>();
		portNo.add(7001);
		portNo.add(7002);
		portNo.add(7003);
		portNo.add(7004);
		portNo.add(7005);
		portNo.add(7006);
		portNo.add(7007);
		portNo.add(7008);
		portNo.add(7009);
		portNo.add(7010);
	}
	
	public Map<String, Long> getServerNameMap(){
		return serverName;
	}
	
	public int getNoOfServers(){
		return intialNumberOfServers;
	}
	
}
