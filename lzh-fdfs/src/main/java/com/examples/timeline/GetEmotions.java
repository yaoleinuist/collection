package com.examples.timeline;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Timeline;
import com.weibo4j.model.Emotion;
import com.weibo4j.model.WeiboException;

public class GetEmotions {

	public static void main(String[] args) {
		String access_token = args[0];
		Timeline tm = new Timeline(access_token);
		try {
			List<Emotion> emotions =  tm.getEmotions();
			for(Emotion e : emotions){
				Log.logInfo(e.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
