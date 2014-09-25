package Test;

import java.io.ObjectInputStream.GetField;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import store.Data;
import store.Database;
import store.Servers;
import Hashing.ConsistentHashing;
import Hashing.HashFunction;
import Hashing.MD5HashFunction;


public class ConsistentHashTest {
	
	private static int noOfServers;
	private static Long OrderId = 500000l;
	private static Long noOfReplica = 3000l;
	private Map<String, Long> serverName;
	
	//private List<Map<String, Long>> listOfMaps = new ArrayList<Map<String, Long>>();
	public ConsistentHashTest(Servers servers){
		noOfServers = servers.getNoOfServers();
		servers.initializeServers();
		serverName = servers.getServerNameMap();
	}
	
	public Map<String, Long> getServerDetails(){
		return serverName;
	}
	
	public void display(){
		Set<Entry<String, Long>> set = serverName.entrySet();
		Iterator<Entry<String, Long>> it = set.iterator();
		
		while(it.hasNext()){
			Entry<String, Long> me = it.next();
			System.out.println("No. of Orders Mapped to "+ me.getKey() + " are " + me.getValue());
		}
	}

	private void deleteServer(ConsistentHashing<String> consistentHashing, Database<?> db, ConsistentHashTest cht) {
		
		// delete a random server from the available list of servers
		Random       random    = new Random();
		List<String> keys      = new ArrayList<String>(serverName.keySet());
		String       deletedServer = keys.get( random.nextInt(keys.size()) );
		
		//deletedServer = "10.10.10.10:7010";
		System.out.println("Deleted server is "+ deletedServer);
		long noOfChanges = serverName.get(deletedServer);
		System.out.println("No. of Orders remapped are "+ noOfChanges + " Out of "+ OrderId );
		System.out.println();
		
		consistentHashing.delete(deletedServer,db, cht);// delete replicas in the circle Map
		serverName.remove(deletedServer); // delete the original one in the base Map
		cht.display();
		
		//db.displayDataBaseContents();
		//consistentHashing.dispMapContents();
	}	
	
	private void addServer(ConsistentHashing<String> consistentHashing,	Database<?> db, ConsistentHashTest cht,String newServerName) {
		
		// add a server
		System.out.println("Added server is "+ newServerName);
		serverName.put(newServerName,0l);
		consistentHashing.addNewServers((String)newServerName, db, cht);
		
		long noOfChanges = serverName.get(newServerName);   // returns the no. of order's rehashed
		 			
		//db.displayDataBaseContents();
		//consistentHashing.dispMapContents();
				
		cht.display();
		
		System.out.println("No. of Orders remapped are "+ noOfChanges + " Out of "+ OrderId );
		
	}
	
	@SuppressWarnings("rawtypes")
	private void addEntryInMainServer(Database<?> db) {
		Set<Entry<Long, Data>> set = db.getDataMap().entrySet();
		Iterator<Entry<Long, Data>> it = set.iterator();
		
		while(it.hasNext()){
			Entry<Long, Data> me = it.next();
			String temp = me.getValue().getServerName();
			serverName.put(temp, serverName.get(temp)+1);
		}
		
	}
	public static void main(String[] args) {
		
		ConsistentHashTest cht = new ConsistentHashTest(new Servers());   // initializes the servers
		  
		Database<?> db = new Database<Object>();
		db.createDatabase();                      //creates DB of objects
		
		HashFunction hf1 = new MD5HashFunction();  // creates Hashes from a HashFunction
		
		Set<String> keySet = cht.getServerDetails().keySet();
		// Assign the nodes as well as replicas in the the ring format
		ConsistentHashing<String> consistentHashing = new ConsistentHashing<String>(hf1, noOfReplica, keySet);
		
		//consistentHashing.dispMapContents();
		
		
		for(int i =1; i<=OrderId; ++i){
			consistentHashing.get(String.valueOf(i), db);
		}
		
		
		//db.displayDataBaseContents();
		//consistentHashing.dispMapContents();
		cht.addEntryInMainServer(db);
		cht.display();
		System.out.println();
		
		cht.deleteServer(consistentHashing, db, cht);
		
		
		//cht.addServer(consistentHashing, db, cht,"212.123.143.178:7342");
		
	}

	
}
