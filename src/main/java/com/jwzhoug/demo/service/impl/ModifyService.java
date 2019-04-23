package com.jwzhoug.demo.service.impl;


import com.jwzhoug.demo.service.IModifyService;
import com.jwzhoug.fremework.annotation.GPService;

/**
 * 增删改业务
 * @author Tom
 *
 */
@GPService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception {
//		return "modifyService add,name=" + name + ",addr=" + addr;
		throw new Exception("故意抛出异常！！！");
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
