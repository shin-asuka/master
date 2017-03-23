package com.vipkid.trpm.entity;
import java.io.Serializable;


/**
 * 
 * @Along
 **/
@SuppressWarnings("serial")
public class ShareLinkSource implements Serializable {
	
	
	/****/
	private Long id;

	/**APP,PC**/
	private String sourceName;

	/****/
	private Long clinkClick;


	
	
	public ShareLinkSource setId(Long id){
		this.id = id;
		return this;
	}

	public Long getId(){
		return this.id;
	}

	public ShareLinkSource setSourceName(String sourceName){
		this.sourceName = sourceName;
		return this;
	}

	public String getSourceName(){
		return this.sourceName;
	}

	public ShareLinkSource setClinkClick(Long clinkClick){
		this.clinkClick = clinkClick;
		return this;
	}

	public Long getClinkClick(){
		return this.clinkClick;
	}


}

