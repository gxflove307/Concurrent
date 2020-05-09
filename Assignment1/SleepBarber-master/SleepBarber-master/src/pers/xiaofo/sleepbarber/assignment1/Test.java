package pers.xiaofo.sleepbarber.assignment1;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Test {
	private static BarberShop barbershop;

	public static void main(String[] args) throws InterruptedException {
		Test test=new Test();
		Scanner scanner=new Scanner(System.in);
		System.out.println("Please input the number of Barbers,Chairs and Customers delimited by the space");
		System.out.println("Such as  2 3 10");
		String str=scanner.nextLine();//input 		
		String []strs=str.split(" ");		
		if(strs.length!= 3) {
			System.out.println("Please input 3 parameters");
			return;
		}
		int barber_number=Integer.parseInt(strs[0]);//number of barbers
		int chair_number=Integer.parseInt(strs[1]);//number of chairs
		int customer_number=Integer.parseInt(strs[2]);//number of Customers
				
		barbershop=new BarberShop(barber_number, chair_number);
		//Create barber thread
		for(int i=0;i<barber_number;i++) {
			BarThread barThread=test.new BarThread(i);
			barThread.start();
		}
		//Create customer thread
		Vector<Thread> threads = new Vector<>();  
		for(int i=0;i<customer_number;i++) {
			CusThread cusThread=test.new CusThread(i);
			Random random=new Random();
			//int x;
			Thread.sleep(random.nextInt(1000));// the time interval between customers
			//System.out.println("customer come in after "+x +"ms");
			threads.add(cusThread);
			cusThread.start();
		}
		for (Thread thread : threads) {  
		      try {  
		        // waiting all thread finish
		        thread.join();
		      } catch (InterruptedException e) {  
		        e.printStackTrace();  
		      }  
		    } 
		Thread.sleep(1000);
		System.out.println("the number of leaving customer without haircut£º"+barbershop.getLeave());
	}
	//Barber thread
	private class BarThread extends Thread{
		private int id;//barber ID
		public BarThread(int id) {
			this.id=id;
		}
		public void run() {
			while(true) {
				try {
					barbershop.helloCustomer(id);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					int time;
				    Random random = new Random(); 
					sleep(time = random.nextInt(2000));// random haircut time
					System.out.println("Barber   "+ id +" haircut  "+ time+"ms");
				} catch (InterruptedException e) {
					System.out.print("Bar"+id+"cannot work");
				}
				try {
					barbershop.byeCustomer(id);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	//Customer thread
	private class CusThread extends Thread{
		private int id;
		private int barber=-1;
		public CusThread(int id) {
			this.id=id;
		}
		@Override
		public void run() {
			try {
				if((barber=barbershop.visitShop(id))!=-1)
					barbershop.leaveShop(id, barber);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
