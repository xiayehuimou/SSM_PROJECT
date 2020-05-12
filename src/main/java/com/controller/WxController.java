package com.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.util.WeiXinUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/wx")
public class WxController {
	public static final String APPID="wx601b7405bdf52b5f";
    public static final String APPSECRET ="72ab93fa3aa7ef5b4f0b942b688c94d1";
    
    private static final Logger logger = Logger.getLogger(WxController.class);
	
	@RequestMapping(value = "/wxLogin", method = RequestMethod.GET)
	public String wxLogin(HttpServletRequest request, HttpServletResponse response)throws ParseException, UnsupportedEncodingException {
		//这个url的域名必须要进行再公众号中进行注册验证，这个地址是成功后的回调地址
		String backUrl="http://xiayehuimou.free.idcfengye.com/ssm_demo/wx/callBack";
		// 第一步：用户同意授权，获取code
		String url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ APPID
					+ "&redirect_uri="
					+URLEncoder.encode(backUrl, "utf-8")
					+ "&response_type=code"
					+ "&scope=snsapi_userinfo"
					+ "&state=STATE#wechat_redirect";
		logger.info("forward重定向地址{" + url + "}");
		return "redirect:" + url ;
	}
	
	@RequestMapping(value = "/callBack", method = RequestMethod.GET)
	public String callBack(ModelMap modelMap,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code =req.getParameter("code");
		//第二步：通过code换取网页授权access_token
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APPID
				+ "&secret="+APPSECRET
				+ "&code="+code
				+ "&grant_type=authorization_code";
		System.out.println("url:"+url);
		JSONObject jsonObject = WeiXinUtil.httpsRequest(url, "GET", null);
		String openid = jsonObject.getString("openid");
		String access_token = jsonObject.getString("access_token");
		String refresh_token = jsonObject.getString("refresh_token");
		//第五步验证access_token是否失效；展示都不需要
		String chickUrl="https://api.weixin.qq.com/sns/auth?access_token="+access_token+"&openid="+openid;
		JSONObject chickuserInfo =WeiXinUtil.httpsRequest(chickUrl, "GET", null);
		System.out.println(chickuserInfo.toString());
		if(!"0".equals(chickuserInfo.getString("errcode"))){
			// 第三步：刷新access_token（如果需要）-----暂时没有使用,参考文档https://mp.weixin.qq.com/wiki，
			String refreshTokenUrl="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+openid+"&grant_type=refresh_token&refresh_token="+refresh_token;
			JSONObject refreshInfo =  WeiXinUtil.httpsRequest(chickUrl, "GET", null);
			System.out.println(refreshInfo.toString());
			access_token=refreshInfo.getString("access_token");
		}
		// 第四步：拉取用户信息(需scope为 snsapi_userinfo)
		// 这个url的token是网络授权token
		//String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token
		//		+ "&openid="+openid
		//		+ "&lang=zh_CN";
		// 此处的token是全局token
		String infoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+WeiXinUtil.getToken(APPID,APPSECRET).getAccessToken()
				+ "&openid="+openid
				+ "&lang=zh_CN";
		System.out.println("infoUrl:"+infoUrl);
		JSONObject userInfo = WeiXinUtil.httpsRequest(infoUrl, "GET", null);
		System.out.println("JSON-----"+userInfo.toString());
		System.out.println("名字-----"+userInfo.getString("nickname"));
		System.out.println("头像-----"+userInfo.getString("headimgurl"));
		/*
         * end 获取微信用户基本信息
         */
		//获取到用户信息后就可以进行重定向，走自己的业务逻辑了。。。。。。
		//接来的逻辑就是你系统逻辑了，请自由发挥
		return "file";
	}

}
