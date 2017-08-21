package com.app.prediction.basecost.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class PlQtCostPO{

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	
	/**
	 * 预测月份
	 */
	private String prediction_month;
	 
	
	/**
	 * 费用编码
	 */
	private String cost_code;
	
	/**
	 * 费用描述
	 */
	private String cost_desc;
	
	/**
	 * 机关费用
	 */
	private BigDecimal jg_cost;
	
	/**
	 * 酸洗费用
	 */
	private BigDecimal sx_cost;
	
	/**
	 * 轧机费用
	 */
	private BigDecimal zj_cost;
	
	/**
	 * 1#镀锌费用
	 */
	private BigDecimal da_cost;
	
	/**
	 * 彩涂费用
	 */
	private BigDecimal ct_cost;
	
	/**
	 * 2#镀锌费用
	 */
	private BigDecimal db_cost;
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrediction_month() {
		return prediction_month;
	}

	public void setPrediction_month(String prediction_month) {
		this.prediction_month = prediction_month;
	}

	public String getCost_code() {
		return cost_code;
	}

	public void setCost_code(String cost_code) {
		this.cost_code = cost_code;
	}

	public String getCost_desc() {
		return cost_desc;
	}

	public void setCost_desc(String cost_desc) {
		this.cost_desc = cost_desc;
	}

	public BigDecimal getJg_cost() {
		return jg_cost;
	}

	public void setJg_cost(BigDecimal jg_cost) {
		this.jg_cost = jg_cost;
	}

	public BigDecimal getSx_cost() {
		return sx_cost;
	}

	public void setSx_cost(BigDecimal sx_cost) {
		this.sx_cost = sx_cost;
	}

	public BigDecimal getZj_cost() {
		return zj_cost;
	}

	public void setZj_cost(BigDecimal zj_cost) {
		this.zj_cost = zj_cost;
	}

	public BigDecimal getDa_cost() {
		return da_cost;
	}

	public void setDa_cost(BigDecimal da_cost) {
		this.da_cost = da_cost;
	}

	public BigDecimal getCt_cost() {
		return ct_cost;
	}

	public void setCt_cost(BigDecimal ct_cost) {
		this.ct_cost = ct_cost;
	}

	public BigDecimal getDb_cost() {
		return db_cost;
	}

	public void setDb_cost(BigDecimal db_cost) {
		this.db_cost = db_cost;
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