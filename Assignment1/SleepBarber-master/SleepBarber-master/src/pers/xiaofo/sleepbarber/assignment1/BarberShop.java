package pers.xiaofo.sleepbarber.assignment1;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
	private int barber_number;
	private int chairs_number;
	private static int nLeave;//the number of leaving customer without haircut
	private ArrayList<Barber> barList;//barber list
	private ArrayList<Customer> cusList;//waiting list
	
	private Lock lock=new ReentrantLock();
	//customer coming, weak up barber or waiting
	public int visitShop(int  id) throws InterruptedException {
		lock.lock();
		int barId;
		Barber barber;
		Customer customer=new Customer(id);
		//if no free chair
		
		if(cusList.size()>=chairs_number) {
			System.out.println("customer "+id+" leaving due to no chair free");
			nLeave++;
			lock.unlock();
			return -1;
		}
		//no Barber is free
		if(getSleepBarber()==-1) {
			cusList.add(customer);//push in waiting list(chairs)
			System.out.println("customer "+id+" sit "+"in chair\t "+cusList.size());
			
			lock.unlock();
			customer.getLock().lock();
			customer.getCondition().await();// Interrupt customer
			customer.getLock().unlock();
			
			//Barber look for customer
			lock.lock();
			barId=customer.getBar();//get barber
			barber=barList.get(barId);
			System.out.println("customer "+id+" come to Barber "+barId);
		}else {
			//Barber free
			barId=getSleepBarber();//found sleeping Barber
			customer.setBarber(barId);
			barber=barList.get(barId);
			barber.setCustomer(customer);//bundles the customer and barber
			barber.setWorking(true);//set Barber thread as working status
			System.out.println("customer "+id+" weak up Barber "+barId);
		}
		
		lock.unlock();
		barber.getLock().lock();
		barber.getCondition().signalAll();// barber weak up and working
		barber.getLock().unlock();	
		return barId;
	}
	//customer haircut and leaving
	public void leaveShop(int cusId,int barId) throws InterruptedException {
		lock.lock();
		Barber barber=barList.get(barId);
		Customer customer=barber.getCustomer();
		
		System.out.println("customer "+cusId+" waiting for Barber "+barId+" haircut");
		
		lock.unlock();
		//Waiting Barber finished
		customer.getLock().lock();
		customer.getCondition().await();
		customer.getLock().unlock();
		
		lock.lock();
		System.out.println("customer "+cusId+" say thanks and leave");
		
		barber.getLock().lock();
		barber.getCondition().signalAll();//leaving
		barber.getLock().unlock();
		lock.unlock();
	}
	public void helloCustomer(int id) throws InterruptedException {
		lock.lock();
		Barber barber=barList.get(id);
		Customer customer;
		barber.getLock().lock();
		//no customer in shop
		if(cusList.size()==0) {
			System.out.println("Barber\t "+id+" sleep due to customer free");
			barber.setWorking(false);
			//barber sleeping and waiting for weak up
			lock.unlock();
			barber.getLock().lock();
			barber.getCondition().await();
			barber.getLock().unlock();
			
			lock.lock();
			customer=barber.getCustomer();//is wakened up by customer
		}else {
			customer=cusList.get(0);
			cusList.remove(0);
			customer.setBarber(id);//the customer is assigned to barber
			barber.setCustomer(customer);//the barber work for the customer
			
			lock.unlock();
			customer.getLock().lock();
			customer.getCondition().signalAll();
			customer.getLock().unlock();
			
			
			barber.getLock().lock();
			barber.getCondition().await();
			barber.getLock().unlock();
			//barber is working for the customer 
			lock.lock();
		}
		
		System.out.println("Barber\t "+id+" is working for customer "+customer.getId());
		
		lock.unlock();
	}
	public void byeCustomer(int id) throws InterruptedException {
		lock.lock();
		Barber barber=barList.get(id);
		Customer customer=barber.getCustomer();
		
		System.out.println("Barber\t "+id+" talk customer "+customer.getId()+" finished and holds the exit door");
		
		lock.unlock();
		customer.getLock().lock();
		customer.getCondition().signalAll();
		customer.getLock().unlock();		
		
		barber.getLock().lock();
		barber.getCondition().await();
		barber.getLock().unlock();
		lock.lock();
		System.out.println("Barber\t "+id+" working done£¬call next");
		lock.unlock();
	}
	
	
	public int getLeave() {
		return nLeave; 
	}
	//check free barber
	public int getSleepBarber() {	
		lock.lock();
		for(Barber b:barList) {
			if(b.getWorking()==false) {
				lock.unlock();
				return b.getId();
			}
		}
		lock.unlock();
		return -1;
	}
	//add barber
	public BarberShop(int m,int n) {
		barber_number=m;
		chairs_number=n;
		barList=new ArrayList<>();
		for(int i=0;i<barber_number;i++) {
			barList.add(new Barber(i));
		}
		cusList=new ArrayList<>();
	}
}
