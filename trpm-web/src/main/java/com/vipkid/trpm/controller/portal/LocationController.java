package com.vipkid.trpm.controller.portal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vipkid.trpm.service.portal.LocationService;

@Controller
public class LocationController extends AbstractPortalController {

	private Logger logger = LoggerFactory.getLogger(LocationController.class);

	@Resource
	private LocationService locationService;

	/**
	 * 根据parent id 获取location 列表
	 * 
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/location/getWithParent", method = RequestMethod.POST)
	public String getLocationList(@RequestParam(value = "parentId") int parentId,
			@RequestParam(value = "level") int level, @RequestParam(value = "title") String title, Model model) {
		logger.info("getLocationList with parentId: {}", parentId);
		model.addAttribute("locationList", locationService.getLocationList(parentId, level));
		model.addAttribute("title", title);
		return view("personal/location_option");
	}

}
