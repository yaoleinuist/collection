package com.weibo.examples.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.weibo.weibo4j.Oauth;
import com.weibo.weibo4j.http.AccessToken;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.util.WeiboConfig;
/**
 * 调用后目前返回200不可与用
 * @author lzhcode
 *
 */
public class Login {
 
	public static AccessToken getToken(String username, String password) throws HttpException, IOException {
		String clientId = WeiboConfig.getValue("client_ID");
		String redirectURI = WeiboConfig.getValue("redirect_URI");
		String url = WeiboConfig.getValue("authorizeURL");

		PostMethod postMethod = new PostMethod(url);
		// 应用的App Key
		postMethod.addParameter("client_id", clientId);
		// 应用的重定向页面
		postMethod.addParameter("redirect_uri", redirectURI);
		// 模拟登录参数
		// 开发者或测试账号的用户名和密码
 		postMethod.addParameter("userId", username);
 		postMethod.addParameter("passwd", password);
		postMethod.addParameter("isLoginSina", "0");
		postMethod.addParameter("action", "submit");
		postMethod.addParameter("response_type", "code");
		HttpMethodParams param = postMethod.getParams();
		param.setContentCharset("UTF-8");
		// 添加头信息
//		List<Header> headers = new ArrayList<Header>();
//		headers.add(new Header("Referer", "https://api.weibo.com/oauth2/authorize?client_id=" + clientId
//				+ "&redirect_uri=" + redirectURI + "&from=sina&response_type=code"));
//		headers.add(new Header("Host", "api.weibo.com"));
//		headers.add(new Header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:11.0) Gecko/20100101 Firefox/11.0"));
		HttpClient client = new HttpClient();
// 		client.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
		client.executeMethod(postMethod);
		int status = postMethod.getStatusCode();
		System.out.println(status);
		if (status != 302) {
			System.out.println("token刷新失败");
			return null;
		}
		// 解析Token
		Header location = postMethod.getResponseHeader("Location");
		if (location != null) {
			String retUrl = location.getValue();
			int begin = retUrl.indexOf("code=");
			if (begin != -1) {
				int end = retUrl.indexOf("&", begin);
				if (end == -1)
					end = retUrl.length();
				String code = retUrl.substring(begin + 5, end);
				if (code != null) {
					Oauth oauth = new Oauth();
					try {
						AccessToken token = oauth.getAccessTokenByCode(code);
						return token;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	
 
	
	public static String getUid(String access_token) throws WeiboException {

		com.weibo.weibo4j.http.HttpClient client = new com.weibo.weibo4j.http.HttpClient();

		return client.get(WeiboConfig.getValue("baseURL") + "account/get_uid.json", access_token).asJSONObject()
				.toString();
	}

	public static void main(String[] args) {
		try {
			AccessToken at = getToken("18046049715", "lzhsky059413");
			System.out.println(getUid(at.getAccessToken()));
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
