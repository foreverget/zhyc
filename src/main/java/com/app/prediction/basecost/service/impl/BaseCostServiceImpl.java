package com.app.prediction.basecost.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.g4studio.common.service.impl.BaseServiceImpl;
import org.g4studio.core.metatype.Dto;
import org.g4studio.core.metatype.impl.BaseDto;
import org.g4studio.core.web.report.excel.ExcelReader;
import org.g4studio.system.common.util.idgenerator.UUIDGenerator;

import com.app.prediction.basecost.entity.PlBzCostPO;
import com.app.prediction.basecost.entity.PlGzCostPO;
import com.app.prediction.basecost.entity.PlJwCostPO;
import com.app.prediction.basecost.entity.PlQtCostPO;
import com.app.prediction.basecost.entity.PlWxCostPO;
import com.app.prediction.basecost.service.BaseCostService;
import com.app.prediction.util.AppConstants;

public class BaseCostServiceImpl extends BaseServiceImpl implements BaseCostService {

	
	public void doImportGzExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,gz_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlGzCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode",AppConstants.INPUT_ZZ_GZ);
		g4Dao.delete("Input.deleteZzCostByParame", parame1);
		
		// 生成工资实体数据，写入表
		for (int i = 0; i < exclelist.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)exclelist.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PlGzCostPO plGzCostPO = new PlGzCostPO();
			plGzCostPO.setId(id);
			plGzCostPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			plGzCostPO.setPl_code(baseDto.getAsString("pl_code"));
			plGzCostPO.setPl_desc(baseDto.getAsString("pl_desc"));
			plGzCostPO.setGz_cost(baseDto.getAsBigDecimal("gz_cost"));
			plGzCostPO.setCreate_dt(new Date());
			g4Dao.insert("PlGzCost.insert", plGzCostPO);
		}
	}
	
	public void doImportWxExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,bj_cost,rg_cost,yt_cost,sum_row_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlWxCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode",AppConstants.INPUT_ZZ_WX);
		g4Dao.delete("Input.deleteZzCostByParame", parame1);

		// 生成实体数据，写入表
		for (int i = 0; i < exclelist.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)exclelist.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PlWxCostPO plWxCostPO = new PlWxCostPO();
			plWxCostPO.setId(id);
			plWxCostPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			plWxCostPO.setPl_code(baseDto.getAsString("pl_code"));
			plWxCostPO.setPl_desc(baseDto.getAsString("pl_desc"));
			plWxCostPO.setBj_cost(baseDto.getAsBigDecimal("bj_cost"));
			plWxCostPO.setRg_cost(baseDto.getAsBigDecimal("rg_cost"));
			plWxCostPO.setYt_cost(baseDto.getAsBigDecimal("yt_cost"));
			plWxCostPO.setSum_row_cost(baseDto.getAsBigDecimal("sum_row_cost"));
			plWxCostPO.setCreate_dt(new Date());
			g4Dao.insert("PlWxCost.insert", plWxCostPO);
		}
	}
	
	public void doImportJwExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,jw_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlJwCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode",AppConstants.INPUT_ZZ_JW);
		g4Dao.delete("Input.deleteZzCostByParame", parame1);

		// 生成实体数据，写入表
		for (int i = 0; i < exclelist.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)exclelist.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PlJwCostPO plJwCostPO = new PlJwCostPO();
			plJwCostPO.setId(id);
			plJwCostPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			plJwCostPO.setPl_code(baseDto.getAsString("pl_code"));
			plJwCostPO.setPl_desc(baseDto.getAsString("pl_desc"));
			plJwCostPO.setJw_cost(baseDto.getAsBigDecimal("jw_cost"));
			plJwCostPO.setCreate_dt(new Date());
			g4Dao.insert("PlJwCost.insert", plJwCostPO);
		}

	}
	
	public void doImportBzExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,bz_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlBzCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode",AppConstants.INPUT_ZZ_BZ);
		g4Dao.delete("Input.deleteZzCostByParame", parame1);

		// 生成实体数据，写入表
		for (int i = 0; i < exclelist.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)exclelist.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PlBzCostPO plBzCostPO = new PlBzCostPO();
			plBzCostPO.setId(id);
			plBzCostPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			plBzCostPO.setPl_code(baseDto.getAsString("pl_code"));
			plBzCostPO.setPl_desc(baseDto.getAsString("pl_desc"));
			plBzCostPO.setBz_cost(baseDto.getAsBigDecimal("bz_cost"));
			plBzCostPO.setCreate_dt(new Date());
			g4Dao.insert("PlBzCost.insert", plBzCostPO);
		}

	}
	
	public void doImportQtExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "cost_code,cost_desc,jg_cost,sx_cost,zj_cost,da_cost,ct_cost,db_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlQtCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode",AppConstants.INPUT_ZZ_QT);
		g4Dao.delete("Input.deleteZzCostByParame", parame1);

		// 生成实体数据，写入表
		for (int i = 0; i < exclelist.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)exclelist.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PlQtCostPO plQtCostPO = new PlQtCostPO();
			plQtCostPO.setId(id);
			plQtCostPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			plQtCostPO.setCost_code(baseDto.getAsString("cost_code"));
			plQtCostPO.setCost_desc(baseDto.getAsString("cost_desc"));
			plQtCostPO.setJg_cost(baseDto.getAsBigDecimal("jg_cost"));
			plQtCostPO.setSx_cost(baseDto.getAsBigDecimal("sx_cost"));
			plQtCostPO.setZj_cost(baseDto.getAsBigDecimal("zj_cost"));
			plQtCostPO.setDa_cost(baseDto.getAsBigDecimal("da_cost"));
			plQtCostPO.setCt_cost(baseDto.getAsBigDecimal("ct_cost"));
			plQtCostPO.setDb_cost(baseDto.getAsBigDecimal("db_cost"));
			plQtCostPO.setCreate_dt(new Date());
			g4Dao.insert("PlQtCost.insert", plQtCostPO);
		}
	}
	
	
	
}
