package com.lzhsite.technology.test;

import java.util.ArrayList;

public class TestCPU {

	static class OoMobject {
	}

	public static void main(String[] args) {

		final ArrayList<OoMobject> list = new ArrayList<OoMobject>();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					System.out.println(Thread.currentThread().getName());
					list.add(new OoMobject());
				}
			}
		}).start();
	}

}
