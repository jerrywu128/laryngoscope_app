/**
 * Added by zhangyanhu C01012,2014-8-5
 */
package com.icatch.sbcapp.SdkApi;

import com.icatch.wificam.customer.ICatchWificamConfig;
import com.icatch.sbcapp.Log.AppLog;


public class AppCfgParameter {
	private final String tag="AppCfgParameter";
	private ICatchWificamConfig configuration = ICatchWificamConfig.getInstance();

	public int getPreviewCacheTime() {
		AppLog.i(tag, "begin getPreviewCacheTime");
		int retVal = 0;
		retVal = configuration.getPreviewCacheTime();
		AppLog.i(tag, "end getPreviewCacheTime retVal =" + retVal);
		return retVal;
	}

	public void setPreviewCacheParam(int cacheTimeInMs) {
		AppLog.i(tag, "begin setPreviewCacheParam cacheTimeInMs =" + cacheTimeInMs);
		configuration.setPreviewCacheParam(cacheTimeInMs, 200);
		AppLog.i(tag, "end setPreviewCacheParam");
	}
}
