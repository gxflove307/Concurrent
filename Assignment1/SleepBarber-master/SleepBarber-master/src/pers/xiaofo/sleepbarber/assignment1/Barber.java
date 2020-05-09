package pers.xiaofo.sleepbarber.assignment1;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barber {
	private int id;//id of barber
	private Customer cusId;//the id of currently customer
	private Lock lock;
	private Condition condition;
	private boolean working;// Barber status
	
	public Barber(int id) {
		this.id=id;
		lock=new ReentrantLock();
		condition=lock.newCondition();
		working=false;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Boolean getWorking() {
		return working;
	}
	public void setWorking(Boolean busy) {
		working=busy;
	}
	public Lock getLock() {
		return lock;
	}
	public Customer getCusId() {
		return cusId;
	}
	public void setCusId(Customer cusId) {
		this.cusId = cusId;
	}
	public Condition getCondition() {
		return condition;
	}
	public Customer getCustomer() {
		return cusId;
	}
	public void setCustomer(Customer customer) {
		this.cusId=customer;
	}

}
