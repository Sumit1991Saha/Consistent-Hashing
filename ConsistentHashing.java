package Hashing;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.print.attribute.standard.PresentationDirection;

import Test.ConsistentHashTest;
import store.Data;
import store.Database;


public class ConsistentHashing<T> {

	private final HashFunction hashFunction;
	private SortedMap<Long, T> circle;
	private long noOfVirtualNodes;
	private Collection<T> nodes;

	public ConsistentHashing(HashFunction hash, long noOfDuplicateNodes, Collection<T> nodes){
		
		circle = new TreeMap<Long, T>();
		this.hashFunction = hash;
		noOfVirtualNodes = noOfDuplicateNodes;
		
		
		for(T node: nodes ){
			addServers(node);
			
		}
	}
	
	public void addServers(T node) {
		for(int i = 0; i < noOfVirtualNodes; ++i){
			singleHash(node,i);
			//singleHash(node, i);
		}
	}
	
	public void singleHash(T node, int index){
		circle.put(hashFunction.getHash(node.toString()+index),node);
	}
	//implemented double hashing to put more variance in keys for same node(while creating replicas)
	public void doubleHash(T node, int i){
		circle.put(hashFunction.getHash(Long.toString(hashFunction.getHash(node.toString()+i))),node);
	}
	
	public void addNewServers(T newServerName, Database<?> db, ConsistentHashTest cht) {
		
		for(int i = 0; i < noOfVirtualNodes; ++i){
			singleHash(newServerName, i);    // adds the server in the circle Map
			
			//create a subMap of the orders which went in successive server which need to be brought to the newly added server
			Long newServerHashValue = hashFunction.getHash((String)newServerName+i);
			//System.out.println("Hash Value for new server " + newServerHashValue);
			
			Long previousLink = ((TreeMap<Long, T>) circle).lowerKey(newServerHashValue);
			Long nextLink = ((TreeMap<Long, T>) circle).higherKey(newServerHashValue);
			
			Long previousServer = (previousLink!=null)?previousLink:circle.lastKey();
			Long nextServer =  (nextLink!=null)? nextLink:circle.firstKey();
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			SortedMap<Long, Data> subMap = new TreeMap(),temp1,temp2;
			if(newServerHashValue.equals(circle.firstKey())){
				//System.out.println((db.getDataMap()).tailMap(previousServer));
				temp1 = (db.getDataMap()).tailMap(previousServer);
				temp2 = (db.getDataMap()).headMap(newServerHashValue);
				//System.out.println(subMap);
				//System.out.println(temp);

				subMap.putAll(temp1);
				subMap.putAll(temp2);
			}
			else {
				subMap = (db.getDataMap()).subMap(previousServer, newServerHashValue);
			}
			
			//now change the entry for orders in database
			Set<Entry<Long, Data>> set = subMap.entrySet();
			Iterator<Entry<Long, Data>> it = set.iterator();
			//System.out.println(set);
			while(it.hasNext()){
				
				Entry<Long, Data> me = it.next();
				String nextServerName = (String)circle.get(nextServer);
				db.add(me.getKey(), me.getValue().getOrderId(), newServerName);
				//System.out.println("Mapping for OrderID "+ me.getValue().getOrderId()+ " having hashValue " + me.getKey() + " is changed from Server " + nextServerName + " to Server "  + newServerName );
				(cht.getServerDetails()).put((String)newServerName, (cht.getServerDetails()).get(newServerName)+1);  //adds new orders to the new server
				(cht.getServerDetails()).put(nextServerName, (cht.getServerDetails()).get(nextServerName)-1); // removes the order from the successive order
				
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void delete(T node, Database db, ConsistentHashTest cht){
		Long presentServer;
		
		for(int i = 0; i < noOfVirtualNodes; ++i){
			presentServer = hashFunction.getHash(node.toString()+i);
			
			Long previousLink = ((TreeMap<Long, T>) circle).lowerKey(presentServer);
			Long nextLink = ((TreeMap<Long, T>) circle).higherKey(presentServer);
			
			Long previousServer = previousLink!=null ? previousLink : circle.lastKey();
			Long nextServer = nextLink!=null ? nextLink : circle.firstKey();
			
			@SuppressWarnings("rawtypes")
			SortedMap<Long, Data> subMap = new TreeMap(),temp1,temp2;
			if(presentServer.equals(circle.firstKey())){
				temp1 = (db.getDataMap()).tailMap(previousServer);
				temp2 = (db.getDataMap()).headMap(presentServer);
				//System.out.println(subMap);
				//System.out.println(temp);

				subMap.putAll(temp1);
				subMap.putAll(temp2);
			}
			
			else {
				subMap = (db.getDataMap()).subMap(previousServer, presentServer); 
			}
			//System.out.println(subMap);
			
			//now change the entry for orders in database
			Set<Entry<Long, Data>> set = subMap.entrySet();
			Iterator<Entry<Long, Data>> it = set.iterator();
			//System.out.println(set);
			while(it.hasNext()){
				
				Entry<Long, Data> me = it.next();
				String nextServerName = (String)circle.get(nextServer);
				//System.out.println(nextServerName);
				db.add(me.getKey(), me.getValue().getOrderId(), nextServerName);
				String presentServerName = (String) circle.get(presentServer);
				//System.out.println((cht.getServerNameMap()).get(presentServerName));
				(cht.getServerDetails()).put(presentServerName, (cht.getServerDetails()).get(presentServerName)-1);
				(cht.getServerDetails()).put(nextServerName, (cht.getServerDetails()).get(nextServerName)+1);
			}
			
			circle.remove(presentServer); //delete the replica of the server from the map
			
			
		}
	}
	
	public T get(Object key, Database<?> db) {
	    if (circle.isEmpty()) {
	      return null;
	    }
	    long hashOrder = hashFunction.getHash((String) key);
	    long serverHash = hashOrder;
	    //System.out.println("For OrderId " + key + " Hash is "+ hash);
	    if (!circle.containsKey(hashOrder)) {
	      SortedMap<Long, T> tailMap = circle.tailMap(hashOrder);
	      serverHash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey(); //required to access keys near 0
	      //System.out.println("OrderId " + key + " goes to "+ serverHash); // orders goes to which server
	      db.add((Long)hashOrder, Long.parseLong((String)key), circle.get(serverHash));
	      return circle.get(serverHash);
	    }
	    //System.out.println("OrderId " + key + " goes to "+ serverHash); // orders goes to which server
	    db.add((Long)serverHash, Long.parseLong((String)key), circle.get(serverHash));
	    return circle.get(hashOrder);
	}
	
	public void dispMapContents() {
		
		Set<Entry<Long, T>> set = circle.entrySet();
		Iterator<Entry<Long, T>> it = set.iterator();
		
		while(it.hasNext()){
			Entry<Long, T> me = it.next();
			System.out.println("Hash is "+ me.getKey()+ " for "+ me.getValue());
		}
	}
}
