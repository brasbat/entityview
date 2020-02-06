package org.brasbat.entityviewdemoapp.demo;

import org.brasbat.entityview.entityviewreact.annotations.EnableEntityViews;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEntityViews
public class DemoApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(DemoApplication.class, args);
	}
}
