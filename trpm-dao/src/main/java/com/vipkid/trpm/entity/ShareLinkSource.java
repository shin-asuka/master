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
	private Long linkClick;


	
	
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

	public ShareLinkSource setLinkClick(Long linkClick){
		this.linkClick = linkClick;
		return this;
	}

	public Long getLinkClick(){
		return this.linkClick;
	}


}

