package com.app.prediction.productline.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class InputPO {

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
	 * input_type_code
	 */
	private String input_type_code;

	
	/**
	 * input_type_desc
	 */
	private String input_type_desc;
	
	/**
	 * mat_code
	 */
	private String mat_code;
	
	/**
	 * mat_desc
	 */
	private String mat_desc;
	

	
	private String input_use_flag;
	
	/**
	 * amount
	 */
	private BigDecimal amount;
	
	/**
	 * unit
	 */
	private String unit;
	
	/**
	 * price
	 */
	private BigDecimal price;
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPl_prediction_code() {
		return pl_prediction_code;
	}

	public void setPl_prediction_code(String pl_prediction_code) {
		this.pl_prediction_code = pl_prediction_code;
	}

	public String getPrediction_month() {
		return prediction_month;
	}

	public void setPrediction_month(String prediction_month) {
		this.prediction_month = prediction_month;
	}

	public String getPl_code() {
		return pl_code;
	}

	public void setPl_code(String pl_code) {
		this.pl_code = pl_code;
	}

	public String getInput_type_code() {
		return input_type_code;
	}

	public void setInput_type_code(String input_type_code) {
		this.input_type_code = input_type_code;
	}

	public String getInput_type_desc() {
		return input_type_desc;
	}

	public void setInput_type_desc(String input_type_desc) {
		this.input_type_desc = input_type_desc;
	}

	public String getMat_code() {
		return mat_code;
	}

	public void setMat_code(String mat_code) {
		this.mat_code = mat_code;
	}

	public String getMat_desc() {
		return mat_desc;
	}

	public void setMat_desc(String mat_desc) {
		this.mat_desc = mat_desc;
	}

	
	public String getInput_use_flag() {
		return input_use_flag;
	}

	public void setInput_use_flag(String input_use_flag) {
		this.input_use_flag = input_use_flag;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCreate_by() {
		return create_by;
	}

	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}

	public Date getCreate_dt() {
		return create_dt;
	}

	public void setCreate_dt(Date create_dt) {
		this.create_dt = create_dt;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getUpdate_by() {
		return update_by;
	}

	public void setUpdate_by(String update_by) {
		this.update_by = update_by;
	}

	public Date getUpdate_dt() {
		return update_dt;
	}

	public void setUpdate_dt(Date update_dt) {
		this.update_dt = update_dt;
	}

	public String getIs_valid() {
		return is_valid;
	}

	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}
	

	

}