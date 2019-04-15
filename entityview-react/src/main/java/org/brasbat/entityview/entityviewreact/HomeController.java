package org.brasbat.entityview.entityviewreact;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController
{

	@RequestMapping(value = "/entity")
	public String index()
	{
		return "entity/index.html";
	}
}