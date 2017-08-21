package com.app.prediction.productline.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class ProductPO {

	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 产线预测编号
	 */
	private String pl_prediction_code;
	
	/**
	 * prediction_month
	 */
	private String prediction_month;
	
	/**
	 * pl_code
	 */
	private String pl_code;
	
	/**
	 * pl_desc
	 */
	private String pl_desc;
	
	/**
	 * output_mat_code
	 */
	private String output_mat_code;
	
	/**
	 * output_mat_desc
	 */
	private String output_mat_desc;
	
	/**
	 * output_amount
	 */
	private BigDecimal output_amount;
	
	/**
	 * output_unit
	 */
	private String output_unit;
	
	/**
	 * output_sale_price
	 */
	private BigDecimal output_sale_price;
	
	/**
	 * output_flag
	 */
	private String output_type_code;
	
	private String output_type_desc;
	/**
	 * feed_mat_code
	 */
	private String feed_mat_code;
	
	/**
	 * feed_mat_desc
	 */
	private String feed_mat_desc;
	
	private BigDecimal feed_amount;
	
	/**
	 * feed_unit
	 */
	private String feed_unit;
	
	/**
	 * feed_sale_price
	 */
	private BigDecimal feed_sale_price;
		
	/**
	 * create_by
	 */
	private String create_by;
	
	/**
	 * create_dt
	 */
	private Date create_dt;
	
	/**
	 * version
	 */
	private Integer version;
	
	/**
	 * update_by
	 */
	private String update_by;
	
	/**
	 * update_dt
	 */
	private Date update_dt;
	
	/**
	 * 是否有效0:否 1:是
	 */
	private String is_valid;
	

	/**
	 * ID
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 产线预测编号
	 * 
	 * @return pl_prediction_code
	 */
	public String getPl_prediction_code() {
		return pl_prediction_code;
	}
	
	/**
	 * prediction_month
	 * 
	 * @return prediction_month
	 */
	public String getPrediction_month() {
		return prediction_month;
	}
	
	/**
	 * pl_code
	 * 
	 * @return pl_code
	 */
	public String getPl_code() {
		return pl_code;
	}
	
	/**
	 * pl_desc
	 * 
	 * @return pl_desc
	 */
	public String getPl_desc() {
		return pl_desc;
	}
	
	
	
	public String getOutput_mat_code() {
		return output_mat_code;
	}

	public void setOutput_mat_code(String output_mat_code) {
		this.output_mat_code = output_mat_code;
	}

	public String getOutput_mat_desc() {
		return output_mat_desc;
	}

	public void setOutput_mat_desc(String output_mat_desc) {
		this.output_mat_desc = output_mat_desc;
	}

	public BigDecimal getOutput_amount() {
		return output_amount;
	}

	public void setOutput_amount(BigDecimal output_amount) {
		this.output_amount = output_amount;
	}

	public String getOutput_unit() {
		return output_unit;
	}

	public void setOutput_unit(String output_unit) {
		this.output_unit = output_unit;
	}

	public BigDecimal getOutput_sale_price() {
		return output_sale_price;
	}

	public void setOutput_sale_price(BigDecimal output_sale_price) {
		this.output_sale_price = output_sale_price;
	}

	public String getOutput_type_code() {
		return output_type_code;
	}

	public void setOutput_type_code(String output_type_code) {
		this.output_type_code = output_type_code;
	}

	public String getOutput_type_desc() {
		return output_type_desc;
	}

	public void setOutput_type_desc(String output_type_desc) {
		this.output_type_desc = output_type_desc;
	}

	public String getFeed_mat_code() {
		return feed_mat_code;
	}

	public void setFeed_mat_code(String feed_mat_code) {
		this.feed_mat_code = feed_mat_code;
	}

	
	public BigDecimal getFeed_amount() {
		return feed_amount;
	}

	public void setFeed_amount(BigDecimal feed_amount) {
		this.feed_amount = feed_amount;
	}

	public String getFeed_mat_desc() {
		return feed_mat_desc;
	}

	public void setFeed_mat_desc(String feed_mat_desc) {
		this.feed_mat_desc = feed_mat_desc;
	}

	public String getFeed_unit() {
		return feed_unit;
	}

	public void setFeed_unit(String feed_unit) {
		this.feed_unit = feed_unit;
	}

	public BigDecimal getFeed_sale_price() {
		return feed_sale_price;
	}

	public void setFeed_sale_price(BigDecimal feed_sale_price) {
		this.feed_sale_price = feed_sale_price;
	}

	/**
	 * create_by
	 * 
	 * @return create_by
	 */
	public String getCreate_by() {
		return create_by;
	}
	
	/**
	 * create_dt
	 * 
	 * @return create_dt
	 */
	public Date getCreate_dt() {
		return create_dt;
	}
	
	/**
	 * version
	 * 
	 * @return version
	 */
	public Integer getVersion() {
		return version;
	}
	
	/**
	 * update_by
	 * 
	 * @return update_by
	 */
	public String getUpdate_by() {
		return update_by;
	}
	
	/**
	 * update_dt
	 * 
	 * @return update_dt
	 */
	public Date getUpdate_dt() {
		return update_dt;
	}
	
	/**
	 * 是否有效0:否 1:是
	 * 
	 * @return is_valid
	 */
	public String getIs_valid() {
		return is_valid;
	}
	

	/**
	 * ID
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 产线预测编号
	 * 
	 * @param pl_prediction_code
	 */
	public void setPl_prediction_code(String pl_prediction_code) {
		this.pl_prediction_code = pl_prediction_code;
	}
	
	/**
	 * prediction_month
	 * 
	 * @param prediction_month
	 */
	public void setPrediction_month(String prediction_month) {
		this.prediction_month = prediction_month;
	}
	
	/**
	 * pl_code
	 * 
	 * @param pl_code
	 */
	public void setPl_code(String pl_code) {
		this.pl_code = pl_code;
	}
	
	/**
	 * pl_desc
	 * 
	 * @param pl_desc
	 */
	public void setPl_desc(String pl_desc) {
		this.pl_desc = pl_desc;
	}
	
	/**
	 * create_by
	 * 
	 * @param create_by
	 */
	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}
	
	/**
	 * create_dt
	 * 
	 * @param create_dt
	 */
	public void setCreate_dt(Date create_dt) {
		this.create_dt = create_dt;
	}
	
	/**
	 * version
	 * 
	 * @param version
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * update_by
	 * 
	 * @param update_by
	 */
	public void setUpdate_by(String update_by) {
		this.update_by = update_by;
	}
	
	/**
	 * update_dt
	 * 
	 * @param update_dt
	 */
	public void setUpdate_dt(Date update_dt) {
		this.update_dt = update_dt;
	}
	
	/**
	 * 是否有效0:否 1:是
	 * 
	 * @param is_valid
	 */
	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}
	

}