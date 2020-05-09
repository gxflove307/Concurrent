    package com.lzb.common;    
        
    import java.util.Random;    
    import java.util.concurrent.TimeUnit;    
    /**  
     *   
     * 功能描述：生产者消费者  
     *          注：锁synhronized是放在“资源的类的内部方法中”，而不是在线程的代码中  
     */    
    public class ProducterCustomer {    
            
        private PCResource pc = new PCResource();    
        // 生产者与消费者调用的时间随机    
        private Random rand = new Random(50);    
            
        public void init() {            
            // 生产者    
            new Thread(new Runnable(){    
        
                public void run() {                 
                    while(true) {    
                        pc.producter();    
                        try {    
                            TimeUnit.MILLISECONDS.sleep(rand.nextInt(1000));    
                        } catch (InterruptedException e) {    
                            e.printStackTrace();    
                        }    
                    }    
                        
                }}).start();    
            
            // 消费者    
            new Thread(new Runnable(){    
        
                public void run() {    
                    while(true) {    
                        pc.customer();    
                        try {    
                            TimeUnit.MILLISECONDS.sleep(rand.nextInt(1000));    
                        } catch (InterruptedException e) {    
                            e.printStackTrace();    
                        }    
                    }    
                }}).start();    
        }    
            
        public static void main(String[] args) {    
                
            ProducterCustomer startPc = new ProducterCustomer();    
            startPc.init();    
        }           
    }    
        
    /**  
     *   
     * 功能描述：同步资源  
     *  
     */    
    class PCResource {    
            
        private static final Integer MAX = 1;    
        private static final Integer MIN = 0;    
        private int product = 0;    
        // 同步互斥通信标志    
        private boolean isRunning = true;    
            
        /**  
         *   
         * 功能描述：生产产品，当生产一个商品后挂起，等待消费者消费完成  
         */    
        public synchronized void producter() {    
                
            while(isRunning) {    
                try {    
                    wait();    
                } catch (InterruptedException e) {    
                    e.printStackTrace();    
                }    
                product++;    
                System.out.println("-------->Product " + product + " good");    
                if(product >= MAX)    
                    break;    
            }    
            isRunning = false;    
            notify();    
        }    
            
        /**  
         *   
         * 功能描述：消费者，消费产品，当产品为0时，等待生产者生产产品  
         */    
        public synchronized void customer() {    
                
            while(!isRunning) {    
                try {    
                    wait();    
                } catch (InterruptedException e) {    
                    e.printStackTrace();    
                }    
                product--;    
                System.out.println("Limit " + product + " goods<----------");    
                if(product <= MIN) {    
                    break;    
                }    
            }    
            isRunning = true;    
            notify();    
        }    
    }    