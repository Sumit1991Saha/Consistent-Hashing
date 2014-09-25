package store;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

import java.util.TreeMap;
import java.util.Map.Entry;

public class Database<T> {

	private SortedMap<Long, Data> dataBase;
	
	public void createDatabase(){
		dataBase = new TreeMap<Long, Data>();
	}
	
	public SortedMap<Long, Data> getDataMap(){
		return dataBase;
	}
	
	public <T> void add(Long hashValue, Long orderId, T server){
		dataBase.put(hashValue, new Data(orderId, (String) server));
	}

	public void displayDataBaseContents() {
		
		Set<Entry<Long, Data>> set = dataBase.entrySet();
		Iterator<Entry<Long, Data>> it = set.iterator();
		
		while(it.hasNext()){
			Entry<Long, Data> me = it.next();
			System.out.println("OrderId " + me.getValue().getOrderId()+ " having hash value "
								+ me.getKey()+" is in Server "+ me.getValue().getServerName());
		}
		
	}

}
