package com.lzhsite.pojo;


public class ResponseData {
	
	/**
	 * 当前页
	 */
	private int currentPage = 1;
	
	/**
	 * 总页数
	 */
	private int totalPages = 0;
	
	/**
	 * 总记录数
	 */
	private int totalCount = 0;

	/**

	 * 存放单个对象
	 */
	private Object model;
	/**
	 * 存放集合对象（可以是List或者Map）
	 */
	private Object collection;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public Object getCollection() {
		return collection;
	}

	public void setCollection(Object collection) {
		this.collection = collection;
	}
}


