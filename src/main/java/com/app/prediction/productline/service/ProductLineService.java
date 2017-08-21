package com.app.prediction.productline.service;

import java.io.InputStream;

import org.g4studio.common.service.BaseService;
import org.g4studio.core.metatype.Dto;

/**
 * 产线服务接口
 */
public interface ProductLineService extends BaseService {

	/**
	 * 生成产线
	 * 
	 * @param pDto
	 * @return
	 */
	public void doCreateProductLines(Dto pDto);
	
	/**
	 * 导入产品数据
	 * @param pDto
	 */
	public void doImportProductExcel(Dto pDto,InputStream inputStream) throws Exception;
	
	/**
	 * 导入消耗数据
	 * @param pDto
	 */
	public void doImportInputExcel(Dto pDto,InputStream inputStream) throws Exception;
	
	/**
	 * 全量导入
	 * @param pDto
	 */
	public String doImpProductAndInputExcel(Dto pDto,InputStream inputStreamProduct,InputStream inputStreamInput) throws Exception;
	
	


}
