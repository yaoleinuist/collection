package com.lzhsite.es.demo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.lzhsite.context.SpringContextHolder;

 

@SuppressWarnings("rawtypes")
public class ElasticsearchTransactionUtil {
	/***线程操作队列*/
	private static ThreadLocal<List<Operation>> operationListLocal = new ThreadLocal<List<Operation>>();

	/**
	 * 启动事务
	 */
	public static void init() {
		operationListLocal.set(new ArrayList<Operation>());
	}

	/**
	 * 将对象操作保存到事务
	 */
	private static void innerPushOperation(Class jpaServiceClass, Action action, Serializable data, boolean needClone) {
		String beanName = StringUtils.uncapitalize(jpaServiceClass.getSimpleName().replace("SERVICE_CLASS_SUFFIX",
				"SEARCH_SERVICE_CLASS_SUFFIX")); // 根据名称匹配找到xxxSearchService的springBean
		if (!SpringContextHolder.containsBean(beanName)) {
			return;
		}
		Object bean = SpringContextHolder.getBean(beanName);
		if (bean instanceof BaseSearchService) {
			operationListLocal.get().add(new Operation((BaseSearchService) bean, action,
					needClone ? (Serializable) SerializationUtils.clone(data) : data)); // 将操作添加到队列，对当时保存的对象做浅克隆快照
		}
	}

	public static void pushSave(Class jpaServiceClass, Serializable data) {
		innerPushOperation(jpaServiceClass, Action.SAVE, data, true);
	}

	public static void pushDelete(Class jpaServiceClass, Serializable data) {
		innerPushOperation(jpaServiceClass, Action.DELETE, data, true);
	}

	public static void pushDeleteById(Class jpaServiceClass, Serializable id) {
		innerPushOperation(jpaServiceClass, Action.DELETE_BY_ID, id, false);
	}

	public static enum Action {
		SAVE, DELETE, DELETE_BY_ID
	}

	/**
	 * 需要flush时 也调用此方法
	 */
	public static void commit() {
		List<Operation> operationList = operationListLocal.get();
		for (Operation operation : operationList) {
			switch (operation.getAction()) {
			case SAVE:
				operation.getSearchService().save(operation.getData()); // 执行保存
				break;
			case DELETE:
				operation.getSearchService().delete(operation.getData()); // 执行删除
				break;
			case DELETE_BY_ID:
				operation.getSearchService().deleteById(operation.getData()); // 执行删除
				break;
			default:
				break;
			}
		}
		operationList.clear();
	}

	public static void rollback() {
		operationListLocal.get().clear();
	}

	/**
    * 索引操作对象.
    * @author JIM
    */
   private static class Operation{
       public Operation(BaseSearchService searchService, Action action, Serializable data){
           this.searchService = searchService;
           this.action = action;
           this.data = data;
       }

       private Action action;


       public Action getAction() {
           return action;
       }

       public void setAction(Action action) {
           this.action = action;
       }

       private Serializable data;

       public Serializable getData() {
           return data;
       }

       public void setData(Serializable data) {
           this.data = data;
       }

       private BaseSearchService searchService;

       public BaseSearchService getSearchService() {
           return searchService;
       }

       public void setSearchService(BaseSearchService searchService) {
           this.searchService = searchService;
       }
   }
}
