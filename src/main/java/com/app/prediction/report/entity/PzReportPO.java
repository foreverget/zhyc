package com.app.prediction.report.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class PzReportPO {

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
	 * mat_code
	 */
	private String mat_code;

	/**
	 * mat_desc
	 */
	private String mat_desc;

	private BigDecimal output_amount;

	private BigDecimal sell_price;

	private BigDecimal unit_cost;

	private BigDecimal diff_price;

	private BigDecimal benefit_1;

	private BigDecimal benefit_2;

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

	public String getPl_desc() {
		return pl_desc;
	}

	public void setPl_desc(String pl_desc) {
		this.pl_desc = pl_desc;
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

	public BigDecimal getOutput_amount() {
		return output_amount;
	}

	public void setOutput_amount(BigDecimal output_amount) {
		this.output_amount = output_amount;
	}

	public BigDecimal getSell_price() {
		return sell_price;
	}

	public void setSell_price(BigDecimal sell_price) {
		this.sell_price = sell_price;
	}

	public BigDecimal getUnit_cost() {
		return unit_cost;
	}

	public void setUnit_cost(BigDecimal unit_cost) {
		this.unit_cost = unit_cost;
	}

	public BigDecimal getDiff_price() {
		return diff_price;
	}

	public void setDiff_price(BigDecimal diff_price) {
		this.diff_price = diff_price;
	}

	public BigDecimal getBenefit_1() {
		return benefit_1;
	}

	public void setBenefit_1(BigDecimal benefit_1) {
		this.benefit_1 = benefit_1;
	}

	public BigDecimal getBenefit_2() {
		return benefit_2;
	}

	public void setBenefit_2(BigDecimal benefit_2) {
		this.benefit_2 = benefit_2;
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