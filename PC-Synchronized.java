public class MyConsumerProductor {
	ArrayList<String> list=new ArrayList<>();
	final private int Capacity = 10;
	//生产者
	class Consumer implements Runnable{
		@Override
		public void run() {
			while(true) {
				synchronized(MyConsumerProductor.class) {
					while(list.size()==Capacity) {//循环判断,因为激活多个线程
						try {
							System.out.println("容量满了，生产者进入等待");
							MyConsumerProductor.class.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
 
					if(list.size()<10) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						list.add(Thread.currentThread().getName());
						System.out.println("product:"+Thread.currentThread().getName()+" size:"+list.size());
						if(list.size()==10) {
							MyConsumerProductor.class.notifyAll();
						}
					}
				}
			}
		}
	}
 
	//生产者
	class Productor implements Runnable{
		@Override
		public void run() {
			while(true) {
				synchronized(MyConsumerProductor.class) {
					while(list.size()==0) {
						try {
							System.out.println("容量没了，消费者进入等待");
							MyConsumerProductor.class.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
 
					if(list.size()>0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("consumer:"+list.remove(list.size()-1)+" size:"+list.size());
						if(list.size()==Capacity) {
							MyConsumerProductor.class.notifyAll();
						}
					}
				}
			}
		}
	}
 
 
	public static void main(String[] args) {
		MyConsumerProductor pc=new MyConsumerProductor();
		//生产者线程
		for(int i=0;i<5;i++) {
			new Thread(pc.new Consumer()).start();
		}
		//消费者线程
		for(int i=0;i<5;i++) {
			new Thread(pc.new Productor()).start();
		}
	}
 
}
