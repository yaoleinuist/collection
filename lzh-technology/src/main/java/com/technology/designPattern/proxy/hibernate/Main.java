package com.technology.designPattern.proxy.hibernate;

public class Main {

	/**
	 * @param args
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		User u = (User) HibernateSessionFactory.getSession().load(User.class, 1);
		System.out.println("Class Name:" + u.getClass().getName());
		System.out.println("Super Class Name:" + u.getClass().getSuperclass().getName());
		Class[] ins = u.getClass().getInterfaces();
		for (Class cls : ins) {
			System.out.println("interface:" + cls.getName());
		}
		System.out.println(u.getName());
	}

}
