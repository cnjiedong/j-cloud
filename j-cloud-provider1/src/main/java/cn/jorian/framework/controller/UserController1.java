package cn.jorian.framework.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController1 {

	static int callCounts = 0;

	@RequestMapping("/sayHello")
	public String sayhello(){
		System.out.println("call to provider 1");
		return "I`m provider 1 ,Hello consumer!" + " calls:" + ++callCounts;
	}
	@RequestMapping("/sayHi")
	public String sayHi(){
		System.out.println("call to provider 1");
		return "I`m provider 1 ,Hello consumer!"+ " calls:" + ++callCounts;
	}
	@RequestMapping("/sayHaha")
	public String sayHaha(){
		System.out.println("call to provider 1");
		return "I`m provider 1 ,Hello consumer!"+ " calls:" + ++callCounts;
	}
}
