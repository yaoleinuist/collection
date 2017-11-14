package com.lzhsite.technology.thread.testLinuxPerformance;

/**
 * -agentlib:hprof=cpu=times,interval=10
 * -agentlib:hprof=cpu=samples,interval=10
 * -agentlib:hprof=heap=dump,format=b,file=c:\core.hprof	//��ʾ��dump
 * -agentlib:hprof=heap=sites	//��ʾ����ٷֱ�
 */
public class HProfTest {
	  public void slowMethod()
	  {
	    try {
	      Thread.sleep( 1000 );
	    } catch (InterruptedException e) {
	      e.printStackTrace();     }
	  }

	  public void slowerMethod()
	  {
	    try {
	      Thread.sleep( 10000 );
	    } catch (InterruptedException e) {
	      e.printStackTrace();     }
	  }

	  public void fastMethod()
	  {
	    try {
	      Thread.yield();
	    } catch (Exception e) {
	      e.printStackTrace();     }
	  }

	  public static void main( String[] args )
	  {
	    HProfTest test = new HProfTest();
	    test.fastMethod();
	    test.slowMethod();
	    test.slowerMethod();
	  }
}
