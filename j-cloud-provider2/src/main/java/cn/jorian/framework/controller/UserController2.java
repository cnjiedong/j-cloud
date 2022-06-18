package cn.jorian.framework.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController2 {
	static int callCounts = 0;
	@RequestMapping("/sayHello")
	public String sayhello(){
		System.out.println("call to provider 2");
		return "I`m provider 2 ,Hello consumer!"+ " calls:" + ++callCounts + "provider2";
	}
	@RequestMapping("/sayHi")
	public String sayHi(){
		System.out.println("call to provider 2");
		return "I`m provider 2 ,Hello consumer!"+ " calls:" + ++callCounts+ "provider2";
	}
	@RequestMapping("/sayHaha")
	public String sayHaha(){
		System.out.println("call to provider 2");
		return "I`m provider 2 ,Hello consumer!"+ " calls:" + ++callCounts+ "provider2";
	}
}
