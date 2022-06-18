package cn.ljd.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author jorian
 */
@SpringBootApplication(scanBasePackages = {"cn.ljd.framework"})
//@EnableEurekaClient //表明这是一个eureka客户端
//@EnableFeignClients(basePackages = "cn.ljd.*") //开启feign
@EnableScheduling
public class JCloudStockApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(JCloudStockApplication.class, args);
	}

}
