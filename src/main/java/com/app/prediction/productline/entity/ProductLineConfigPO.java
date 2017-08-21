package com.app.prediction.productline.entity;

import java.util.Date;

/**
 * 
 */
public class ProductLineConfigPO{

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	
	/**
	 * 产线编码
	 */
	private String pl_code;
	
	/**
	 * 产线描述
	 */
	private String pl_desc;
	
	/**
	 * 是否预测 0 否 1 是
	 */
	private String is_prediction;
	
	/**
	 * 创建人
	 */
	private String create_by;
	
	/**
	 * 创建时间
	 */
	private Date create_dt;
	
	/**
	 * 版本号
	 */
	private Integer version;
	
	/**
	 * 更新人
	 */
	private String update_by;
	
	/**
	 * 更新时间
	 */
	private Date update_dt;
	
	/**
	 * 是否有效
	 */
	private String is_valid;
	

	/**
	 * id
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 产线编码
	 * 
	 * @return pl_code
	 */
	public String getPl_code() {
		return pl_code;
	}
	
	/**
	 * 产线描述
	 * 
	 * @return pl_desc
	 */
	public String getPl_desc() {
		return pl_desc;
	}
	
	/**
	 * 是否预测 0 否 1 是
	 * 
	 * @return is_prediction
	 */
	public String getIs_prediction() {
		return is_prediction;
	}
	
	/**
	 * 创建人
	 * 
	 * @return create_by
	 */
	public String getCreate_by() {
		return create_by;
	}
	
	/**
	 * 创建时间
	 * 
	 * @return create_dt
	 */
	public Date getCreate_dt() {
		return create_dt;
	}
	
	/**
	 * 版本号
	 * 
	 * @return version
	 */
	public Integer getVersion() {
		return version;
	}
	
	/**
	 * 更新人
	 * 
	 * @return update_by
	 */
	public String getUpdate_by() {
		return update_by;
	}
	
	/**
	 * 更新时间
	 * 
	 * @return update_dt
	 */
	public Date getUpdate_dt() {
		return update_dt;
	}
	
	/**
	 * 是否有效
	 * 
	 * @return is_valid
	 */
	public String getIs_valid() {
		return is_valid;
	}
	

	/**
	 * id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 产线编码
	 * 
	 * @param pl_code
	 */
	public void setPl_code(String pl_code) {
		this.pl_code = pl_code;
	}
	
	/**
	 * 产线描述
	 * 
	 * @param pl_desc
	 */
	public void setPl_desc(String pl_desc) {
		this.pl_desc = pl_desc;
	}
	
	/**
	 * 是否预测 0 否 1 是
	 * 
	 * @param is_prediction
	 */
	public void setIs_prediction(String is_prediction) {
		this.is_prediction = is_prediction;
	}
	
	/**
	 * 创建人
	 * 
	 * @param create_by
	 */
	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}
	
	/**
	 * 创建时间
	 * 
	 * @param create_dt
	 */
	public void setCreate_dt(Date create_dt) {
		this.create_dt = create_dt;
	}
	
	/**
	 * 版本号
	 * 
	 * @param version
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * 更新人
	 * 
	 * @param update_by
	 */
	public void setUpdate_by(String update_by) {
		this.update_by = update_by;
	}
	
	/**
	 * 更新时间
	 * 
	 * @param update_dt
	 */
	public void setUpdate_dt(Date update_dt) {
		this.update_dt = update_dt;
	}
	
	/**
	 * 是否有效
	 * 
	 * @param is_valid
	 */
	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}
	

}