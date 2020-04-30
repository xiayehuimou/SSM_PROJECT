package com.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.service.TestService;

import commons.JsonResult;

@Controller
public class TetsController {
	
	private static Logger LOGGER = Logger.getLogger(TetsController.class);
	
	@Autowired
	private TestService testService;

	@RequestMapping("/test")
	public String init(Model model) {
		//model.addAttribute("hello");
		return "file";
	}
	
	@RequestMapping(value="/insert",method=RequestMethod.GET)
	public String insert() {
		testService.insert();
		return "";
	}
	
	
	
	@PostMapping("/upload")
    public String upload(HttpServletRequest request, @RequestBody MultipartFile[] multiFiles,Model model){
        //获取上传根目录
        String uploadRootPath = request.getServletContext().getRealPath("upload");
        LOGGER.info("当前上传文件根路径：" + uploadRootPath);
        File rootFile = new File(uploadRootPath);
        //判断目录是否存在，不存在层级目录
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        //设置返回值，默认为成功
        JsonResult jsonResult = new JsonResult();
        //上传目录
        try {
            for (MultipartFile file : multiFiles){
                //如果文件不为空，则可以上传
                if (null != file){
                    File serverFile = new File(rootFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    LOGGER.info("当前上传文件路径：" + serverFile.getAbsolutePath());
                    if (serverFile.exists()){
                        serverFile.delete();
                    }
                    file.transferTo(serverFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("文件上传失败！");
            //设置返回消息为失败
            jsonResult.setCode(-1);
            jsonResult.setMessage("文件上传失败！");
        }
        model.addAttribute("result",jsonResult);
        return "file";
    }
}
