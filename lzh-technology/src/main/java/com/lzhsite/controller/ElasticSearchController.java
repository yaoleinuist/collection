package com.lzhsite.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.lzhsite.es.spring.constants.AppConstant;
import com.lzhsite.es.spring.constants.SystemConstants;
import com.lzhsite.es.spring.model.IMJMicroPointModel;
import com.lzhsite.es.util.ElasticSearchHandler;
import com.lzhsite.util.DateFormart;
import com.lzhsite.util.DateUtils;
import com.lzhsite.util.ResponseUtils;
import com.lzhsite.util.redis.RedisUtils;
 


/**
 * 微商城数据埋点
 *
 * 添加并整合庙街数据埋点
 */
@Controller
public class ElasticSearchController {

    private static final Logger logger = Logger.getLogger(ElasticSearchController.class);

    /**微商城数据埋点参数**/
    @Value("${elasticSearch.name}")
    private String microIndexName;
    @Value("${elasticSearch.type}")
    private String microIndexType;
    @Value("${elasticSearch.typeDdl}")
    private String microTypeDdl;

    /**庙街数据埋点参数**/
    @Value("${elasticSearch.imj.name}")
    private String imjIndexName;
    @Value("${elasticSearch.imj.type}")
    private String imjIndexType;
    @Value("${elasticSearch.imj.typeDdl}")
    private String imjTypeDdl;

    /**
     * 大数据平台地址列表参数
     */
    @Value("${elasticSearch.typeServer}")
    private String typeServer;
    @Value("${elasticSearch.server.address}")
    private String elasticSearchServerAddress;

    /**
     * 庙街日志服务类接口
     *
     * @param imjMicroPoint
     * @return
     * @throws Exception
     */
    @RequestMapping( value = "/elasticSearch/imj/logs",method = RequestMethod.POST)
    @ResponseBody
    public String logCollect(@RequestBody IMJMicroPointModel imjMicroPoint){

        StringBuffer indexName = new StringBuffer();

        indexName.append(imjIndexName).append("_").append(DateUtils.format(new Date(),DateUtils.PATTERN_YYYYMMDD));

        ElasticSearchHandler esHandler = ElasticSearchHandler.getInstance(elasticSearchServerAddress);

        Boolean indexExist = createElaticIndex(indexName, esHandler);//创建Index

        if(indexExist) {
            Boolean typeExist = createElaticSearch(indexName, esHandler, imjIndexType, typeServer, imjTypeDdl);
            //已经存在
            if (null != imjMicroPoint && typeExist) {
                initIMJMicroPoint(imjMicroPoint);
                imjMicroPoint.setOpenMicroTimeDate(null);

                String logsText = JSON.toJSONString(imjMicroPoint);
                logger.info("日志服务接收请求参数:" + logsText);

                try {
                    esHandler.replaceIndexResponse(indexName.toString(), imjIndexType, logsText);
                    return JSON.toJSONString(ResponseUtils.getResponseBase(SystemConstants.RES_STAT_OK));
                } catch (Exception e) {
                    logger.error("elasticSearch 添加日志发送错误，e=" + e.getMessage());
                }
            } else {
                logger.error("前端无参数传递或者创建Type失败，数据埋点处理失败！");
            }
        }
        return null;
    }

    /**
     * 初始化庙街数据埋点
     *
     * @param imjMicroPoint
     */
    public void initIMJMicroPoint(IMJMicroPointModel imjMicroPoint) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+0800'");
            if (null != imjMicroPoint.getOpenMicroTime()){
                imjMicroPoint.setOpenMicroTimeDate(
                        DateFormart.paserYYYY_MM_DD_HHMMSSToDate(imjMicroPoint.getOpenMicroTime()));
                String formatDate = sdf.format(imjMicroPoint.getOpenMicroTimeDate());
                imjMicroPoint.setOpenMicroTime(formatDate);
            }

 
 

            Date date = new Date();
            imjMicroPoint.setCreateTime(sdf.format(date));
            imjMicroPoint.setCreateDt(DateFormart.convertToYYYYMMDD(date));
            imjMicroPoint.setSessionId((String) SecurityUtils.getSubject().getSession().getId());


        } catch (Exception e) {
            logger.error("初始化采集埋点数据失败：" + e.getMessage());
        }
    }

    
    /**
     * 创建Indx
     * @param indexName
     * @param esHandler
     * @param indexName
     * @return
     */
    private Boolean createElaticIndex(StringBuffer indexName,ElasticSearchHandler esHandler){

        /** 判断是否已经被创建过3次以上*/
        String key = AppConstant.REDIS_PREFIX+"esIndex"+AppConstant.COLON + indexName;
        if (null != RedisUtils.get(key,Integer.class) && RedisUtils.get(key,Integer.class)>3){
            logger.info("esIndex已经被创建过了key="+key);
            return true;
        }
        Boolean indexExist = esHandler.indexExist(indexName.toString());
        if(!indexExist){
            Long start = System.currentTimeMillis();
            String indexUrl = typeServer+"/" + indexName.toString();
            try {
                CloseableHttpResponse response = esHandler.createIndexResponse(indexUrl);
                int statusCode = response.getStatusLine().getStatusCode();

                logger.info("response stautsCode:"+statusCode);

                if(statusCode == HttpStatus.OK.value()){
                    indexExist = true;
                    logger.info("elasticSearch创建indexUrl成功"+statusCode);
                }
                logger.info("elasticSearch创建Index,返回response:"+JSON.toJSONString(response));
            } catch (IOException e) {
                logger.error("elasticSearch创建Index,response失败", e);
            }
            logger.info("es新建Index耗时=" + (System.currentTimeMillis()-start));
        }
        if (indexExist){
            logger.info("esIndex被创建key="+key);
            RedisUtils.incr(key);
            RedisUtils.setExpire(key,24*60*60);
        }
        return indexExist;
    }

    /**
     * 创建数据埋点数据结构
     * 创建type
     * @param esHandler
     * @param indexType
     * @param indexName
     * @param typeServer
     * @param typeDdl
     */
    private Boolean createElaticSearch(StringBuffer indexName,ElasticSearchHandler esHandler,String indexType,String typeServer,String typeDdl){
        String key = AppConstant.REDIS_PREFIX+"esType" + AppConstant.COLON + indexName +  AppConstant.COLON + indexType;
        /** 判断是否已经被创建过3次以上*/
        if (null != RedisUtils.get(key,Integer.class) && RedisUtils.get(key,Integer.class)>3){
            logger.info("esType已经被创建过了key="+key);
            return true;
        }
        Boolean typeExist = esHandler.typeExist(indexName.toString(),indexType);


        logger.info("elasticSearch创建Type,indexName:" + indexName + ",idnexType=" + indexType);
        if(!typeExist){
            Long start = System.currentTimeMillis();

            String typeUrl = typeServer + "/"+indexName+"/"+indexType.toString()+"/"+"_mapping";

            String newTypeDdl = typeDdl.replace("{elasticSearchType}",indexType);

            logger.info(">>>>>>>>>typeDdl:"+typeDdl);
            try {
                CloseableHttpResponse response = esHandler.createTypeWithResponse(typeUrl,newTypeDdl);

                int statusCode = response.getStatusLine().getStatusCode();

                logger.info("response stautsCode:"+statusCode);

                if(statusCode == HttpStatus.OK.value()){
                    typeExist = true;
                    logger.info("elasticSearch创建Type成功"+statusCode);
                }
                logger.info("elasticSearch创建Type,返回response:"+JSON.toJSONString(response));
            } catch (IOException e) {
                logger.error("elasticSearch创建type,response失败",e);
            }
            logger.info("es新建type耗时="+(System.currentTimeMillis()-start));
        }
        if (typeExist){
            logger.info("esType被创建key="+key);
            RedisUtils.incr(key);
            RedisUtils.setExpire(key,24*60*60);
        }
        return typeExist;
    }
}