package com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.entity.TbUser;
import com.dao.sqlmap.TbUserMapper;
import com.service.TestService;

@Service
public class TestServiceImpl implements TestService{
	
	@Autowired
	private TbUserMapper tbUserMapper;

	@Override
	public void insert() {
		TbUser record = new TbUser();
		record.setId(Long.valueOf(1));
		record.setName("小米");
		
		tbUserMapper.insert(record);
		
	}

}
