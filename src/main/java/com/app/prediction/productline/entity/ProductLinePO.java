package com.app.prediction.productline.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class ProductLinePO {

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
	
	private BigDecimal main_input_cost;
	
	private BigDecimal item_input_cost;
	
	private BigDecimal zz_input_cost;
	
	private BigDecimal sale_cost;
	
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



	public BigDecimal getMain_input_cost() {
		return main_input_cost;
	}

	public void setMain_input_cost(BigDecimal main_input_cost) {
		this.main_input_cost = main_input_cost;
	}

	public BigDecimal getItem_input_cost() {
		return item_input_cost;
	}

	public void setItem_input_cost(BigDecimal item_input_cost) {
		this.item_input_cost = item_input_cost;
	}

	public BigDecimal getZz_input_cost() {
		return zz_input_cost;
	}

	public void setZz_input_cost(BigDecimal zz_input_cost) {
		this.zz_input_cost = zz_input_cost;
	}

	public BigDecimal getSale_cost() {
		return sale_cost;
	}

	public void setSale_cost(BigDecimal sale_cost) {
		this.sale_cost = sale_cost;
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