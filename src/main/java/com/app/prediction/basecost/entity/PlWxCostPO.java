package com.app.prediction.basecost.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 */
public class PlWxCostPO{

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
	 * 产线编码
	 */
	private String pl_code;
	
	/**
	 * 产线描述
	 */
	private String pl_desc;
	
	/**
	 * 备件费用
	 */
	private BigDecimal bj_cost;
	
	/**
	 * 人工费用
	 */
	private BigDecimal rg_cost;
	
	/**
	 * 预提费用
	 */
	private BigDecimal yt_cost;
	
	/**
	 * 行总费用
	 */
	private BigDecimal sum_row_cost;
	
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

	public BigDecimal getBj_cost() {
		return bj_cost;
	}

	public void setBj_cost(BigDecimal bj_cost) {
		this.bj_cost = bj_cost;
	}

	public BigDecimal getRg_cost() {
		return rg_cost;
	}

	public void setRg_cost(BigDecimal rg_cost) {
		this.rg_cost = rg_cost;
	}

	public BigDecimal getYt_cost() {
		return yt_cost;
	}

	public void setYt_cost(BigDecimal yt_cost) {
		this.yt_cost = yt_cost;
	}

	public BigDecimal getSum_row_cost() {
		return sum_row_cost;
	}

	public void setSum_row_cost(BigDecimal sum_row_cost) {
		this.sum_row_cost = sum_row_cost;
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