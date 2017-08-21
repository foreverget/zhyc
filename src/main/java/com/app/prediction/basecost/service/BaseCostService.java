package com.app.prediction.basecost.service;

import java.io.InputStream;

import org.g4studio.common.service.BaseService;
import org.g4studio.core.metatype.Dto;

/**
 * 基础费用服务接口
 */
public interface BaseCostService extends BaseService {

	
	/**
	 * 导入工资及附加数据
	 * @param pDto
	 */
	public void doImportGzExcel(Dto pDto,InputStream inputStream) throws Exception;

	/**
	 * 导入维修数据
	 * @param pDto
	 */
	public void doImportWxExcel(Dto pDto,InputStream inputStream) throws Exception;

	/**
	 * 导入机物料数据
	 * @param pDto
	 */
	public void doImportJwExcel(Dto pDto,InputStream inputStream) throws Exception;

	/**
	 * 导入包装数据
	 * @param pDto
	 */
	public void doImportBzExcel(Dto pDto,InputStream inputStream) throws Exception;

	/**
	 * 导入其他数据
	 * @param pDto
	 */
	public void doImportQtExcel(Dto pDto,InputStream inputStream) throws Exception;


}
