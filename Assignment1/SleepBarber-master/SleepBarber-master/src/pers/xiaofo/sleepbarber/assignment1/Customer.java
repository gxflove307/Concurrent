package pers.xiaofo.sleepbarber.assignment1;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Customer {
	private int id;// id of customer
	private int barId;//the id of currently barber
	private Lock lock;
	private Condition condition;
	
	public Customer(int id) {
		this.id=id;
		lock=new ReentrantLock();
		condition=lock.newCondition();
	}
	public int getBarId() {
		return barId;
	}
	public int getBar() {
		return barId;
	}
	public void setBarId(int barId) {
		this.barId = barId;
	}
	public void setBarber(int m) {
		barId=m;
	}
	public int getId() {
		return id;
	}
	public Lock getLock() {
		return lock;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Condition getCondition() {
		return condition;
	}

}
