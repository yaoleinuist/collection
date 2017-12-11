package com.lzhsite.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.db.Pager;
import com.lzhsite.pojo.ErrorsBean;
import com.lzhsite.pojo.ResponseBase;
import com.lzhsite.pojo.ResponseData;

/**
 * ShenLF 2015/5/7
 */
public class ResponseUtils {

	private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

	/**
	 * 封装返回对象
	 * 
	 * @param stat
	 * @return
	 */
	public static ResponseBase getResponseBase(String state) {
		ResponseBase rb = new ResponseBase();
		rb.setState(state);
		rb.setData(new Object());
//		rb.setErrors(null);
		/*
		 * String jsonString = JSON.toJSONString(rb); logger.info(jsonString);
		 */
		return rb;
	}

	/**
	 * 封装返回对象
	 * 
	 * @param stat
	 * @param errors
	 * @return
	 */
	public static ResponseBase getResponseBase(String stat, List<ErrorsBean> errors) {
		ResponseBase rb = new ResponseBase();
		rb.setState(stat);
		rb.setData(new Object());
//		rb.setErrors(errors);
        if(null != errors && !errors.isEmpty() && errors.size()!=0 ){
            ErrorsBean errorsBean = errors.get(0);
            rb.setError(errorsBean);
        }
		/*
		 * String jsonString = JSON.toJSONString(rb); logger.info(jsonString);
		 */
		return rb;
	}


	/**
	 * 封装返回对象
	 * 
	 * @param stat
	 * @param errors
	 * @return
	 */
	public static ResponseBase getResponseBase(String stat, List<ErrorsBean> errors, Object model) {
		ResponseBase rb = new ResponseBase();
		ResponseData rd = new ResponseData();
		rd.setModel(model);
		rb.setState(stat);
		rb.setData(rd);
//		rb.setErrors(errors);
        if(null != errors && !errors.isEmpty() && errors.size()!=0 ){
            ErrorsBean errorsBean = errors.get(0);
            rb.setError(errorsBean);
        }
		/*
		 * String jsonString = JSON.toJSONString(rb); logger.info(jsonString);
		 */
		return rb;
	}

	/**
	 * 封装返回对象
	 * 
	 * @param stat
	 * @param error
	 * @return
	 */
	public static ResponseBase getResponseBase(String stat, String error) {
		ResponseBase rb = new ResponseBase();
		rb.setState(stat);
		rb.setData(new Object());
		List<ErrorsBean> errors = new ArrayList<>();
		ErrorsBean errorsBean = new ErrorsBean();
		errorsBean.setMsg(error);
		errors.add(errorsBean);
//		rb.setErrors(errors);
        rb.setError(errorsBean);
		/*
		 * String jsonString = JSON.toJSONString(rb); logger.info(jsonString);
		 */
		return rb;
	}

	/**
	 * 封装返回对象
	 * 
	 * @author wangzuoxu 2015年4月30日
	 * @param stat
	 *            状态
	 * @param model
	 *            返回单个对象
	 * @return
	 */
	public static ResponseBase getResponseBase(String stat, Object model) {

		ResponseBase rb = new ResponseBase();

		ResponseData rd = new ResponseData();

		rb.setState(stat);
		// 单个对象
		rd.setModel(model);

		// 返回数据Rd
		rb.setData(rd);

		/*
		 * String jsonString = JSON.toJSONString(rb);
		 * 
		 * logger.info(jsonString);
		 */

		return rb;

	}

//	/**
//	 * 封装返回对象
//	 *
//	 * @author wangzuoxu 2015年4月30日
//	 * @param stat
//	 *            状态
//	 * @param model
//	 *            返回单个对象
//	 * @param collections
//	 *            返回集合
//	 * @param query
//	 *            分页数据
//	 * @return
//	 */
//	public static ResponseBase getResponseBase(String stat, List<ErrorsBean> errors, Object model, Object collections,
//			Query query) {
//		ResponseBase rb = new ResponseBase();
//		ResponseData rd = new ResponseData();
//		rb.setState(stat);
//		// 单个对象
//		rd.setModel(model);
//		rb.setErrors(errors);
//		// 返回数据Rd
//		rb.setData(rd);
//		rd.setCollection(collections);
//
//		if(StringUtils.equals(stat, SystemConstants.RES_STAT_ERROR) && !errors.isEmpty()){
//			ErrorsBean eb = errors.get(0);
//			rb.setError(eb);
//		}
//
//        return setQueryInfo(query,rb,rd);
//	}
    /**
     * 封装返回对象
     *
     * @author wangzuoxu 2015年4月30日
     * @param stat
     *            状态
     * @param model
     *            返回单个对象
     * @param collections
     *            返回集合
     * @param query
     *            分页数据
     * @return
     */
    public static ResponseBase getResponseBase(String stat, Object model, Object collections, Pager  query) {
        ResponseBase rb = new ResponseBase();
        ResponseData rd = new ResponseData();
        rb.setState(stat);
        // 单个对象
        rd.setModel(model);
        // 返回数据Rd
        rb.setData(rd);
        rd.setCollection(collections);

        return setQueryInfo(query,rb,rd);
    }


	/**
	 * 封装返回对象
	 *
	 * @author wangzuoxu 2015年4月30日
	 * @param stat
	 *            状态
	 * @param model
	 *            返回单个对象
	 * @param collections
	 *            返回集合
	 * @param query
	 *            分页数据
	 * @return
	 */
	public static ResponseBase getResponseBase(String stat, ErrorsBean errorsBean, Object model, Object collections,
			Pager query) {
		ResponseBase rb = new ResponseBase();
		ResponseData rd = new ResponseData();
		rb.setState(stat);
		// 单个对象
		rd.setModel(model);
		// 返回数据Rd
		rb.setData(rd);
		rd.setCollection(collections);
		//错误信息
		rb.setError(errorsBean);
        List<ErrorsBean> errors = new ArrayList<>();
        errors.add(errorsBean);
//        rb.setErrors(errors);

		return setQueryInfo(query,rb,rd);
	}

    /**
     *
     * @param query
     * @param rd
     * @return
     */
	private static ResponseBase setQueryInfo(Pager query, ResponseBase rb,ResponseData rd){
        if (null == query || ( null != query && query.getTotalCount() == 0 )) {
			rd.setCurrentPage(1);
			rd.setTotalCount(0);
			rd.setTotalPages(0);
            rb.setData(rd);
			return rb;
        }
        if (query.getCurrentPage() != 0) {
            rd.setCurrentPage(query.getCurrentPage());
        }
        // 总记录数
        if (query.getTotalCount() != 0) {
            rd.setTotalCount(query.getTotalCount());
        }else{
			rd.setCurrentPage(0);
		}
        // 总页数
        if (query.getPageCount() != 0) {
            rd.setTotalPages(query.getPageCount());
        }
        rb.setData(rd);
        return rb;
	}


    /**
     *
     * @param stat
     * @param errorsBean
     * @return
     */
	public static ResponseBase getResponseBase(String stat, ErrorsBean errorsBean) {
		ResponseBase rb = new ResponseBase();
		ResponseData rd = new ResponseData();
		rb.setState(stat);

		// 返回数据Rd
		rb.setData(rd);
		//错误信息
		rb.setError(errorsBean);
        List<ErrorsBean> errors = new ArrayList<>();
        errors.add(errorsBean);
//        rb.setErrors(errors);

		return rb;
	}

}
