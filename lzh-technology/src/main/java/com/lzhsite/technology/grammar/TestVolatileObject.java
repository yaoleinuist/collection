package com.lzhsite.technology.grammar;
/**
 *  
 *  
 * @author lzhcode
 *
 */
public class TestVolatileObject implements Runnable {
	
	private  ObjectA a; // 加上volatile 就可以正常结束While循环了 
    
	public TestVolatileObject(ObjectA a) {
        this.a = a;
    }
 
    public ObjectA getA() {
        return a;
    }
 
    public void setA(ObjectA a) {
        this.a = a;
    }
 
    @Override
    public void run() {
        long i = 0;
        while (a.isFlag()) {
            i++;
            //System.out.println("------------------");
        }
        System.out.println("stop My Thread " + i);
    }
 
    public void stop() {
        a.setFlag(false);
    }
 
    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.getProperty("java.vm.name"));
         
        TestVolatileObject test = new TestVolatileObject(new ObjectA());
        new Thread(test).start();
 
        Thread.sleep(1000);
        //主线程内修改了ObjectA的flage字段,子线程无法感知到flag的修改
        test.stop();
        Thread.sleep(1000);
        System.out.println("Main Thread " + test.getA().isFlag());
    }
 
    static class ObjectA {
    	
        private boolean flag = true;
 
        public boolean isFlag() {
            return flag;
        }
 
        public void setFlag(boolean flag) {
            this.flag = flag;
        }
 
    }
 
}
