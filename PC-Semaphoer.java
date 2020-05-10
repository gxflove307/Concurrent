import java.util.concurrent.Semaphore;
/**
 * 老紫竹JAVA提高教程-信号量(Semaphore)的使用。<br>
 * 生产者和消费者的例子，库存的管理。
 * 
 * @author 老紫竹(java2000.net,laozizhu.com)
 */
public class TestSemaphore {
  public static void main(String[] args) {
    // 启动线程 设置3个生产者和消费者
    for (int i = 0; i <= 3; i++) {
      // 生产者
      new Thread(new Producer()).start();
      // 消费者
      new Thread(new Consumer()).start();
    }
  }
  // 仓库
  static Warehouse buffer = new Warehouse();
  // 生产者，负责增加
  static class Producer implements Runnable {
    static int num = 1;
    @Override
    public void run() {
      int n = num++;
      while (true) {
        try {
          buffer.put(n);
          System.out.println(">" + n);
          // 速度较快。休息10毫秒
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  // 消费者，负责减少
  static class Consumer implements Runnable {
    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("<" + buffer.take());
          // 速度较慢，休息1000毫秒
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  /**
   * 仓库
   * 
   * @author 老紫竹(laozizhu.com)
   */
  static class Warehouse {
    // 非满锁
    final Semaphore notFull = new Semaphore(10);//设置临界区容量为10
    // 非空锁
    final Semaphore notEmpty = new Semaphore(0);//设置非空信号量
    // 核心锁
    final Semaphore mutex = new Semaphore(1);//设置互斥信号量
    // 库存容量
    final Object[] items = new Object[10];
    int putptr, takeptr, count;
    /**
     * 把商品放入仓库.<br>
     * 
     * @param x
     * @throws InterruptedException
     */
    public void put(Object x) throws InterruptedException {
      // 保证非满
      notFull.acquire();  //producer获取信号，notFull信号量减一
      // 保证不冲突
      mutex.acquire();//当前进程获得信号，mutex信号量减1，其他线程被阻塞操作共享区块data
      try {
        // 增加库存
        items[putptr] = x;
        if (++putptr == items.length)
          putptr = 0;
        ++count;
      } finally {
        // 退出核心区
        mutex.release(); //mutex信号量+1, 其他线程可以继续信号操作共享区块data
        // 增加非空信号量，允许获取商品
        notEmpty.release();//成功生产数据，notEmpty信号量加1
      }
    }
    /**
     * 从仓库获取商品
     * 
     * @return
     * @throws InterruptedException
     */
    public Object take() throws InterruptedException {
      // 保证非空
      notEmpty.acquire(); //customer获取信号，notEmpty信号量减一
      // 核心区
      mutex.acquire();//当前进程获得信号，mutex信号量减1，其他线程被阻塞操作共享区块data
      try {
        // 减少库存
        Object x = items[takeptr];
        if (++takeptr == items.length)
          takeptr = 0;
        --count;
        return x;
      } finally {
        // 退出核心区
        mutex.release();//mutex信号量+1, 其他线程可以继续信号操作共享区块data
        // 增加非满的信号量，允许加入商品
        notFull.release();//成功消耗数据，notFull信号量加1
      }
    }
  }
}
