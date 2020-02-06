package org.brasbat.entityview.entityviewreact.controller;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EntityViewerHomeController
{
	private static final Log LOGGER = LogFactory.getLog(EntityViewerHomeController.class);
	@PostConstruct
	public void initInfo()
	{
		LOGGER.info("Entity viewer UI located at [/entity]");
	}

	@RequestMapping(value = "/entity")
	public String index()
	{
		return "entity/index.html";
	}
}