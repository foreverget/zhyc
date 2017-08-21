package com.app.prediction.report.service;

import org.g4studio.common.service.BaseService;
import org.g4studio.core.metatype.Dto;

/**
 * 产线预测报表服务接口
 */
public interface PlReportService extends BaseService {

	/**
	 * 生成产线预测报表
	 * 
	 * @param pDto
	 * @return
	 */
	public void doCreatePlReport(Dto pDto);

	/**
	 * 生成产线分品种预测报表
	 * 
	 * @param pDto
	 * @return
	 */
	public void doCreatePzReport(Dto pDto);


}
