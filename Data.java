package store;

public class Data<T> {

	private Long orderId;
	private String server;
	
	public Data(Long orderId, String server){
		this.orderId = orderId;
		this.server = server;
	}
	
	public Long getOrderId(){
		return orderId;
	}
	
	public String getServerName(){
		return server;
	}
}
