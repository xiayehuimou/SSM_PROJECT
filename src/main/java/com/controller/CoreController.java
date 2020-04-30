package com.controller;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.util.List;
 
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
 
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.entity.SNSUserInfo;
import com.entity.WeixinOauth2Token;
import com.entity.WeixinUserInfo;
import com.util.MyX509TrustManager;
import com.util.WeiXinUtil;
 
public class CoreController {
	
	public static void main(String args[]) {
	    // 获取接口访问凭证
	    String accessToken = WeiXinUtil.getToken("自己的appid", "自己的密钥").getAccessToken();
	    /**
	     * 获取用户信息
	     */
	    WeixinUserInfo user = getUserInfo(accessToken, "这里写关注用户的openid");
        //做这个测试的时候可以先关注，或者取消关注，控制台会打印出来此用户的openid
        System.out.println("OpenID：" + user.getOpenId());
	    System.out.println("关注状态：" + user.getSubscribe());
	    System.out.println("关注时间：" + user.getSubscribeTime());
	    System.out.println("昵称：" + user.getNickname());
	    System.out.println("性别：" + user.getSex());
	    System.out.println("国家：" + user.getCountry());
	    System.out.println("省份：" + user.getProvince());
	    System.out.println("城市：" + user.getCity());
	    System.out.println("语言：" + user.getLanguage());
	    System.out.println("头像：" + user.getHeadImgUrl());
	  }
 
	/**
	   * 获取用户信息
	   * 
	   * @param accessToken 接口访问凭证
	   * @param openId 用户标识
	   * @return WeixinUserInfo
	   */
	  public static WeixinUserInfo getUserInfo(String accessToken, String openId) {
	    WeixinUserInfo weixinUserInfo = null;
	    // 拼接请求地址
	    String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
	    requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
	    // 获取用户信息
	    JSONObject jsonObject = WeiXinUtil.httpsRequest(requestUrl, "GET", null);
	    if (null != jsonObject) {
	      try {
	        weixinUserInfo = new WeixinUserInfo();
	        // 用户的标识
	        weixinUserInfo.setOpenId(jsonObject.getString("openid"));
	        // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
	        weixinUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
	        // 用户关注时间
	        weixinUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
	        // 昵称
	        weixinUserInfo.setNickname(jsonObject.getString("nickname"));
	        // 用户的性别（1是男性，2是女性，0是未知）
	        weixinUserInfo.setSex(jsonObject.getInt("sex"));
	        // 用户所在国家
	        weixinUserInfo.setCountry(jsonObject.getString("country"));
	        // 用户所在省份
	        weixinUserInfo.setProvince(jsonObject.getString("province"));
	        // 用户所在城市
	        weixinUserInfo.setCity(jsonObject.getString("city"));
	        // 用户的语言，简体中文为zh_CN
	        weixinUserInfo.setLanguage(jsonObject.getString("language"));
	        // 用户头像
	        weixinUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
	      } catch (Exception e) {
	        if (0 == weixinUserInfo.getSubscribe()) {
	        	System.err.printf("用户{}已取消关注", weixinUserInfo.getOpenId());
	        } else {
	          int errorCode = jsonObject.getInt("errcode");
	          String errorMsg = jsonObject.getString("errmsg");
	          	System.err.printf("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
	        }
	      }
	    }
	    return weixinUserInfo;
	  }
    
	  
	  /**
	     * 获取网页授权凭证
	     * 
	     * @param appId 公众账号的唯一标识
	     * @param appSecret 公众账号的密钥
	     * @param code
	     * @return WeixinAouth2Token
	     */
	    public static WeixinOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) {
	        WeixinOauth2Token wat = null;
	        // 拼接请求地址
	        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	        requestUrl = requestUrl.replace("APPID", appId);
	        requestUrl = requestUrl.replace("SECRET", appSecret);
	        requestUrl = requestUrl.replace("CODE", code);
	        // 获取网页授权凭证
	        JSONObject jsonObject = WeiXinUtil.httpsRequest(requestUrl, "GET", null);
	        if (null != jsonObject) {
	            try {
	                wat = new WeixinOauth2Token();
	                wat.setAccessToken(jsonObject.getString("access_token"));
	                wat.setExpiresIn(jsonObject.getInt("expires_in"));
	                wat.setRefreshToken(jsonObject.getString("refresh_token"));
	                wat.setOpenId(jsonObject.getString("openid"));
	                wat.setScope(jsonObject.getString("scope"));
	            } catch (Exception e) {
	                wat = null;
	                int errorCode = jsonObject.getInt("errcode");
	                String errorMsg = jsonObject.getString("errmsg");
	                System.err.printf("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
	            }
	        }
	        return wat;
	    }
	  
	    
	    /**
	     * 通过网页授权获取用户信息
	     * 
	     * @param accessToken 网页授权接口调用凭证
	     * @param openId 用户标识
	     * @return SNSUserInfo
	     */
	    @SuppressWarnings( { "deprecation", "unchecked" })
	    public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) {
	        SNSUserInfo snsUserInfo = null;
	        // 拼接请求地址
	        String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
	        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
	        // 通过网页授权获取用户信息
	        JSONObject jsonObject = WeiXinUtil.httpsRequest(requestUrl, "GET", null);
 
	        if (null != jsonObject) {
	            try {
	                snsUserInfo = new SNSUserInfo();
	                // 用户的标识
	                snsUserInfo.setOpenId(jsonObject.getString("openid"));
	                // 昵称
	                snsUserInfo.setNickname(jsonObject.getString("nickname"));
	                // 性别（1是男性，2是女性，0是未知）
	                snsUserInfo.setSex(jsonObject.getInt("sex"));
	                // 用户所在国家
	                snsUserInfo.setCountry(jsonObject.getString("country"));
	                // 用户所在省份
	                snsUserInfo.setProvince(jsonObject.getString("province"));
	                // 用户所在城市
	                snsUserInfo.setCity(jsonObject.getString("city"));
	                // 用户头像
	                snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
	                // 用户特权信息
	                snsUserInfo.setPrivilegeList(JSONArray.toList(jsonObject.getJSONArray("privilege"), List.class));
	            } catch (Exception e) {
	                snsUserInfo = null;
	                int errorCode = jsonObject.getInt("errcode");
	                String errorMsg = jsonObject.getString("errmsg");
	                System.err.printf("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
	            }
	        }
	        return snsUserInfo;
	    }
	    
	    /**
	     * 发送https请求
	     * 
	     * @param requestUrl 请求地址
	     * @param requestMethod 请求方式（GET、POST）
	     * @param outputStr 提交的数据
	     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	     */
	    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
	        JSONObject jsonObject = null;
	        try {
	            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
	            TrustManager[] tm = { new MyX509TrustManager() };
	            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
	            sslContext.init(null, tm, new java.security.SecureRandom());
	            // 从上述SSLContext对象中得到SSLSocketFactory对象
	            SSLSocketFactory ssf = sslContext.getSocketFactory();
 
	            URL url = new URL(requestUrl);
	            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	            conn.setSSLSocketFactory(ssf);
	            
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setUseCaches(false);
	            // 设置请求方式（GET/POST）
	            conn.setRequestMethod(requestMethod);
 
	            // 当outputStr不为null时向输出流写数据
	            if (null != outputStr) {
	                OutputStream outputStream = conn.getOutputStream();
	                // 注意编码格式
	                outputStream.write(outputStr.getBytes("UTF-8"));
	                outputStream.close();
	            }
 
	            // 从输入流读取返回内容
	            InputStream inputStream = conn.getInputStream();
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String str = null;
	            StringBuffer buffer = new StringBuffer();
	            while ((str = bufferedReader.readLine()) != null) {
	                buffer.append(str);
	            }
 
	            // 释放资源
	            bufferedReader.close();
	            inputStreamReader.close();
	            inputStream.close();
	            inputStream = null;
	            conn.disconnect();
	            jsonObject = JSONObject.fromObject(buffer.toString());
	        } catch (ConnectException ce) {
	        	System.err.printf("连接超时：{}", ce);
	        } catch (Exception e) {
	        	System.err.printf("https请求异常：{}", e);
	        }
	        return jsonObject;
	    }
	    
	    /**
	     * URL编码（utf-8）
	     * 
	     * @param source
	     * @return
	     */
	    public static String urlEncodeUTF8(String source) {
	        String result = source;
	        try {
	            result = java.net.URLEncoder.encode(source, "utf-8");
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        return result;
	    }
}