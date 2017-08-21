package com.app.prediction.report.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class PlReportPO {

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
	
	private BigDecimal pl_amount; 
	
	private BigDecimal goods_amount;
	
	private BigDecimal unit_cost;
	
	private BigDecimal sell_price;
	
	private BigDecimal diff_price;
	
	private BigDecimal benefit_1;
	
	private BigDecimal benefit_2;
	
	private BigDecimal benefit_3;
	
	
	
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
	
	
	public BigDecimal getPl_amount() {
		return pl_amount;
	}

	public void setPl_amount(BigDecimal pl_amount) {
		this.pl_amount = pl_amount;
	}

	public BigDecimal getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(BigDecimal goods_amount) {
		this.goods_amount = goods_amount;
	}



	public BigDecimal getUnit_cost() {
		return unit_cost;
	}

	public void setUnit_cost(BigDecimal unit_cost) {
		this.unit_cost = unit_cost;
	}

	public BigDecimal getSell_price() {
		return sell_price;
	}

	public void setSell_price(BigDecimal sell_price) {
		this.sell_price = sell_price;
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

	public BigDecimal getBenefit_3() {
		return benefit_3;
	}

	public void setBenefit_3(BigDecimal benefit_3) {
		this.benefit_3 = benefit_3;
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