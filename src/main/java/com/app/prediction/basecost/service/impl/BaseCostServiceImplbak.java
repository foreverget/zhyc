package com.app.prediction.basecost.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.g4studio.common.service.impl.BaseServiceImpl;
import org.g4studio.core.metatype.Dto;
import org.g4studio.core.metatype.impl.BaseDto;
import org.g4studio.core.orm.xibatis.sqlmap.client.SqlMapExecutor;
import org.g4studio.core.orm.xibatis.support.SqlMapClientCallback;
import org.g4studio.core.web.report.excel.ExcelReader;
import org.g4studio.system.common.util.idgenerator.UUIDGenerator;

import com.app.prediction.basecost.entity.PlBzCostPO;
import com.app.prediction.basecost.entity.PlGzCostPO;
import com.app.prediction.basecost.entity.PlJwCostPO;
import com.app.prediction.basecost.entity.PlQtCostPO;
import com.app.prediction.basecost.entity.PlWxCostPO;
import com.app.prediction.basecost.service.BaseCostService;
import com.app.prediction.productline.entity.InputPO;
import com.app.prediction.util.AppConstants;

public class BaseCostServiceImplbak extends BaseServiceImpl implements BaseCostService {

	
	public void doImportGzExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,gz_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		// 生成工资实体数据，写入表
		//System.out.print(exclelist.size());
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlGzCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode","ZZ-GZ");
		g4Dao.delete("Input.deleteZzCostByParame", parame1);
		
		// 定义需计算中间变量值
		// 1、SX(酸洗+酸再生)
		BigDecimal sxCost = BigDecimal.ZERO;
		// 2、ZJ(1750轧机+1420轧机+磨辊间)  = 
		BigDecimal zjCost = BigDecimal.ZERO;
		// 3、DA(1#镀锌)
		BigDecimal daCost = BigDecimal.ZERO;
		// 4、DB(2#镀锌)
		BigDecimal dbCost = BigDecimal.ZERO;
		// 5、CT(彩涂)
		BigDecimal ctCost = BigDecimal.ZERO;
		// 6、JF(机关+公辅) 
		BigDecimal jfCost = BigDecimal.ZERO;
		
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
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				daCost = dbCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}
			g4Dao.insert("PlGzCost.insert", plGzCostPO);
		}
		
		/**
		 *  工资及附加(逻辑)						
			酸洗=酸洗+酸再生+（机关+公辅）/所有产量*酸洗产量						
			轧机=1750轧机+1420轧机+磨辊间+（机关+公辅）/所有产量*轧机产量						
			1#镀锌=1#镀锌+（机关+公辅）/所有产量*1#镀锌产量						
			2#镀锌=2#镀锌+（机关+公辅）/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		/**
		 *  工资及附加(逻辑)						
			酸洗=SX+JF/所有产量*酸洗产量						
			轧机=ZJ+JF/所有产量*轧机产量						
			1#镀锌=DA+JF/所有产量*1#镀锌产量						
			2#镀锌=DB+JF/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		// 计算各产线工资及附加
		// 查找当前预测 月份下各产线数据
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		List productLineList = g4Dao.queryForList("ProductLine.findProductLineList", parame);
		// 循环计算所有产量
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			sumPlAmount = sumPlAmount.add(plDto.getAsBigDecimal("pl_amount"));
		}
		final List<InputPO> inputList = new ArrayList<InputPO>();
		// 酸洗工资
		BigDecimal calcSxCost = BigDecimal.ZERO;
		// 单机架
		BigDecimal calcZjCost = BigDecimal.ZERO;
		// 彩涂
		BigDecimal calcCtCost = BigDecimal.ZERO;
		// 1#镀锌
		BigDecimal calcDaCost = BigDecimal.ZERO;
		// 2#镀锌
		BigDecimal calcDbCost = BigDecimal.ZERO;
		// 循环计算各产线工资附加 并更新input表制造费
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				// 计算出值
				calcSxCost = calcSxCost.add(sxCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-GZ");
				inputPO.setMat_desc("工资及附加");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcSxCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcZjCost = calcZjCost.add(zjCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-GZ");
				inputPO.setMat_desc("工资及附加");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcZjCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDaCost = calcDaCost.add(daCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-GZ");
				inputPO.setMat_desc("工资及附加");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDaCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDbCost = calcDbCost.add(dbCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-GZ");
				inputPO.setMat_desc("工资及附加");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDbCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcCtCost = calcCtCost.add(ctCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-GZ");
				inputPO.setMat_desc("工资及附加");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcCtCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
			}
		}
		
		// 批量插入消耗表
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < inputList.size(); i++) {
					executor.insert("Input.insert", inputList.get(i));
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	public void doImportWxExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,bj_cost,rg_cost,yt_cost,sum_row_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		// 生成工资实体数据，写入表
		System.out.print(exclelist.size());
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlWxCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode","ZZ-WX");
		g4Dao.delete("Input.deleteZzCostByParame", parame1);
		// 定义需计算中间变量值
		// 1、SX(酸洗+酸再生)
		BigDecimal sxCost = BigDecimal.ZERO;
		// 2、ZJ(1750轧机+1420轧机+磨辊间)
		BigDecimal zjCost = BigDecimal.ZERO;
		// 3、DA(1#镀锌)
		BigDecimal daCost = BigDecimal.ZERO;
		// 4、DB(2#镀锌)
		BigDecimal dbCost = BigDecimal.ZERO;
		// 5、CT(彩涂)
		BigDecimal ctCost = BigDecimal.ZERO;
		// 6、JF(机关+公辅) 
		BigDecimal jfCost = BigDecimal.ZERO;
		
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
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				daCost = dbCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}
			g4Dao.insert("PlWxCost.insert", plWxCostPO);
		}

		/**
		 *  酸洗=酸洗（备件+人工费+预提）+（机关+公辅（备件+人工费+预提））/所有产量*酸洗产量								
			单机架=1750、1420（备件+人工费+预提）+（机关+公辅（备件+人工费+预提））/所有产量*单机架产量								
			1#镀锌=1#镀锌（备件+人工费+预提）+（机关+公辅（备件+人工费+预提））/所有产量*1#镀锌产量								
			2#镀锌=2#镀锌（备件+人工费+预提）+（机关+公辅（备件+人工费+预提））/所有产量*2#镀锌产量								
			彩涂=彩涂（备件+人工费+预提）+（机关+公辅（备件+人工费+预提））/所有产量*彩涂产量								
				
		 */
		/**
		 *  (逻辑)						
			酸洗=SX+JF/所有产量*酸洗产量						
			轧机=ZJ+JF/所有产量*轧机产量						
			1#镀锌=DA+JF/所有产量*1#镀锌产量						
			2#镀锌=DB+JF/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		// 计算
		// 查找当前预测 月份下各产线数据
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		List productLineList = g4Dao.queryForList("ProductLine.findProductLineList", parame);
		// 循环计算所有产量
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			sumPlAmount = sumPlAmount.add(plDto.getAsBigDecimal("pl_amount"));
		}
		final List<InputPO> inputList = new ArrayList<InputPO>();
		// 酸洗
		BigDecimal calcSxCost = BigDecimal.ZERO;
		// 单机架
		BigDecimal calcZjCost = BigDecimal.ZERO;
		// 彩涂
		BigDecimal calcCtCost = BigDecimal.ZERO;
		// 1#镀锌
		BigDecimal calcDaCost = BigDecimal.ZERO;
		// 2#镀锌
		BigDecimal calcDbCost = BigDecimal.ZERO;
		// 循环计算各产线 并更新input表制造费
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				// 计算出值
				calcSxCost = calcSxCost.add(sxCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-WX");
				inputPO.setMat_desc("维修");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcSxCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcZjCost = calcZjCost.add(zjCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-WX");
				inputPO.setMat_desc("维修");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcZjCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDaCost = calcDaCost.add(daCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-WX");
				inputPO.setMat_desc("维修");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDaCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDbCost = calcDbCost.add(dbCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-WX");
				inputPO.setMat_desc("维修");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDbCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcCtCost = calcCtCost.add(ctCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-WX");
				inputPO.setMat_desc("维修");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcCtCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
			}
		}
		
		// 批量插入消耗表
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < inputList.size(); i++) {
					executor.insert("Input.insert", inputList.get(i));
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	public void doImportJwExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,jw_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		// 生成工资实体数据，写入表
		System.out.print(exclelist.size());
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlJwCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode","ZZ-JW");
		g4Dao.delete("Input.deleteZzCostByParame", parame1);
		// 定义需计算中间变量值
		// 1、SX(酸洗+酸再生)
		BigDecimal sxCost = BigDecimal.ZERO;
		// 2、ZJ(1750轧机+1420轧机+磨辊间) 
		BigDecimal zjCost = BigDecimal.ZERO;
		// 3、DA(1#镀锌)
		BigDecimal daCost = BigDecimal.ZERO;
		// 4、DB(2#镀锌)
		BigDecimal dbCost = BigDecimal.ZERO;
		// 5、CT(彩涂)
		BigDecimal ctCost = BigDecimal.ZERO;
		// 6、JF(机关+公辅) 
		BigDecimal jfCost = BigDecimal.ZERO;
		
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
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				daCost = dbCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}
			g4Dao.insert("PlJwCost.insert", plJwCostPO);
		}
		
		/**
		 *  (逻辑)						
			酸洗=酸洗+（机关+公辅）/所有产量*酸洗产量				
			单机架=1750+1420+（机关+公辅）/所有产量*单机架产量				
			1#镀锌=1#镀锌+（机关+公辅）/所有产量*1#镀锌产量				
			2#镀锌=2#镀锌+（机关+公辅）/所有产量*2#镀锌产量				
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量				
				
		 */
		/**
		 *  工资及附加(逻辑)						
			酸洗=SX+JF/所有产量*酸洗产量						
			轧机=ZJ+JF/所有产量*轧机产量						
			1#镀锌=DA+JF/所有产量*1#镀锌产量						
			2#镀锌=DB+JF/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		// 计算各产线
		// 查找当前预测 月份下各产线数据
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		List productLineList = g4Dao.queryForList("ProductLine.findProductLineList", parame);
		// 循环计算所有产量
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			sumPlAmount = sumPlAmount.add(plDto.getAsBigDecimal("pl_amount"));
		}
		final List<InputPO> inputList = new ArrayList<InputPO>();
		// 酸洗
		BigDecimal calcSxCost = BigDecimal.ZERO;
		// 单机架
		BigDecimal calcZjCost = BigDecimal.ZERO;
		// 彩涂
		BigDecimal calcCtCost = BigDecimal.ZERO;
		// 1#镀锌
		BigDecimal calcDaCost = BigDecimal.ZERO;
		// 2#镀锌
		BigDecimal calcDbCost = BigDecimal.ZERO;
		// 循环计算各产线  并更新input表制造费
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				// 计算出值
				calcSxCost = calcSxCost.add(sxCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-JW");
				inputPO.setMat_desc("机物料消耗");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcSxCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcZjCost = calcZjCost.add(zjCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-JW");
				inputPO.setMat_desc("机物料消耗");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcZjCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDaCost = calcDaCost.add(daCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-JW");
				inputPO.setMat_desc("机物料消耗");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDaCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDbCost = calcDbCost.add(dbCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-JW");
				inputPO.setMat_desc("机物料消耗");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDbCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcCtCost = calcCtCost.add(ctCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-JW");
				inputPO.setMat_desc("机物料消耗");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcCtCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
			}
		}
		
		// 批量插入消耗表
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < inputList.size(); i++) {
					executor.insert("Input.insert", inputList.get(i));
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	public void doImportBzExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "pl_code,pl_desc,bz_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		// 生成工资实体数据，写入表
		System.out.print(exclelist.size());
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlBzCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode","ZZ-BZ");
		g4Dao.delete("Input.deleteZzCostByParame", parame1);
		// 定义需计算中间变量值
		// 1、SX(酸洗+酸再生)
		BigDecimal sxCost = BigDecimal.ZERO;
		// 2、ZJ(1750轧机+1420轧机+磨辊间)
		BigDecimal zjCost = BigDecimal.ZERO;
		// 3、DA(1#镀锌)
		BigDecimal daCost = BigDecimal.ZERO;
		// 4、DB(2#镀锌)
		BigDecimal dbCost = BigDecimal.ZERO;
		// 5、CT(彩涂)
		BigDecimal ctCost = BigDecimal.ZERO;
		// 6、JF(机关+公辅) 
		BigDecimal jfCost = BigDecimal.ZERO;
		
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
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				daCost = dbCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}
			g4Dao.insert("PlBzCost.insert", plBzCostPO);
		}

		/**
		 *  工资及附加(逻辑)						
			酸洗=酸洗+酸再生+（机关+公辅）/所有产量*酸洗产量						
			轧机=1750轧机+1420轧机+磨辊间+（机关+公辅）/所有产量*轧机产量						
			1#镀锌=1#镀锌+（机关+公辅）/所有产量*1#镀锌产量						
			2#镀锌=2#镀锌+（机关+公辅）/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		/**
		 *  工资及附加(逻辑)						
			酸洗=SX+JF/所有产量*酸洗产量						
			轧机=ZJ+JF/所有产量*轧机产量						
			1#镀锌=DA+JF/所有产量*1#镀锌产量						
			2#镀锌=DB+JF/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		// 计算各产线工资及附加
		// 查找当前预测 月份下各产线数据
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		List productLineList = g4Dao.queryForList("ProductLine.findProductLineList", parame);
		// 循环计算所有产量
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			sumPlAmount = sumPlAmount.add(plDto.getAsBigDecimal("pl_amount"));
		}
		final List<InputPO> inputList = new ArrayList<InputPO>();
		// 酸洗工资
		BigDecimal calcSxCost = BigDecimal.ZERO;
		// 单机架
		BigDecimal calcZjCost = BigDecimal.ZERO;
		// 彩涂
		BigDecimal calcCtCost = BigDecimal.ZERO;
		// 1#镀锌
		BigDecimal calcDaCost = BigDecimal.ZERO;
		// 2#镀锌
		BigDecimal calcDbCost = BigDecimal.ZERO;
		// 循环计算各产线工资附加 并更新input表制造费
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				// 计算出值
				calcSxCost = calcSxCost.add(sxCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-BZ");
				inputPO.setMat_desc("包装费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcSxCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcZjCost = calcZjCost.add(zjCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-BZ");
				inputPO.setMat_desc("包装费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcZjCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDaCost = calcDaCost.add(daCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-BZ");
				inputPO.setMat_desc("包装费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDaCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDbCost = calcDbCost.add(dbCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-BZ");
				inputPO.setMat_desc("包装费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDbCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcCtCost = calcCtCost.add(ctCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-BZ");
				inputPO.setMat_desc("包装费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcCtCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
			}
		}
		
		// 批量插入消耗表
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < inputList.size(); i++) {
					executor.insert("Input.insert", inputList.get(i));
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	public void doImportQtExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "cost_code,cost_desc,jg_cost,sx_cost,zj_cost,da_cost,ct_cost,db_cost";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		final Dto tempDto = pDto;
		// 生成工资实体数据，写入表
		System.out.print(exclelist.size());
		
		// 先删除
		Dto parame = new BaseDto();
		parame.put("predictionMonth", pDto.getAsString("prediction_month"));
		g4Dao.delete("PlQtCost.deleteByPredictionMonth", parame);
		
		// 同时删除input表相关数据
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", pDto.getAsString("prediction_month"));
		parame1.put("matCode","ZZ-QT");
		g4Dao.delete("Input.deleteZzCostByParame", parame1);

		// 定义需计算中间变量值
		// 1、SX(酸洗+酸再生)
		BigDecimal sxCost = BigDecimal.ZERO;
		// 2、ZJ(1750轧机+1420轧机+磨辊间)
		BigDecimal zjCost = BigDecimal.ZERO;
		// 3、DA(1#镀锌)
		BigDecimal daCost = BigDecimal.ZERO;
		// 4、DB(2#镀锌)
		BigDecimal dbCost = BigDecimal.ZERO;
		// 5、CT(彩涂)
		BigDecimal ctCost = BigDecimal.ZERO;
		// 6、JF(机关+公辅) 
		BigDecimal jfCost = BigDecimal.ZERO;
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
			if("合计".equals(plQtCostPO.getCost_desc())){
				// 计算中间变量的值
				sxCost = plQtCostPO.getSx_cost();
				zjCost = plQtCostPO.getZj_cost();
				daCost = plQtCostPO.getDa_cost();
				dbCost = plQtCostPO.getDb_cost();
				ctCost = plQtCostPO.getCt_cost();
				jfCost = plQtCostPO.getJg_cost();
			}
			g4Dao.insert("PlQtCost.insert", plQtCostPO);
		}
		
		
		/**
		 *  (逻辑)						
			酸洗=酸洗+机关费用总和/所有产量*酸洗产量	
			单机架=轧机+机关费用总和/所有产量*单机架产量	
			1#镀锌=1#镀锌+机关费用总和/所有产量*1#镀锌产量	
			2#镀锌=2#镀锌+机关费用总和/所有产量*2#镀锌产量	
			彩涂=彩涂+机关费用总和/所有产量*彩涂产量	
		 */
		/**
		 *  (逻辑)						
			酸洗=SX+JF/所有产量*酸洗产量						
			轧机=ZJ+JF/所有产量*轧机产量						
			1#镀锌=DA+JF/所有产量*1#镀锌产量						
			2#镀锌=DB+JF/所有产量*2#镀锌产量						
			彩涂=彩涂+（机关+公辅）/所有产量*彩涂产量						
		 */
		// 计算各产线
		// 查找当前预测 月份下各产线数据
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		List productLineList = g4Dao.queryForList("ProductLine.findProductLineList", parame);
		// 循环计算所有产量
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			sumPlAmount = sumPlAmount.add(plDto.getAsBigDecimal("pl_amount"));
		}
		final List<InputPO> inputList = new ArrayList<InputPO>();
		// 酸洗
		BigDecimal calcSxCost = BigDecimal.ZERO;
		// 单机架
		BigDecimal calcZjCost = BigDecimal.ZERO;
		// 彩涂
		BigDecimal calcCtCost = BigDecimal.ZERO;
		// 1#镀锌
		BigDecimal calcDaCost = BigDecimal.ZERO;
		// 2#镀锌
		BigDecimal calcDbCost = BigDecimal.ZERO;
		// 循环计算各产线  并更新input表制造费
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				// 计算出值
				calcSxCost = calcSxCost.add(sxCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-QT");
				inputPO.setMat_desc("其他制造费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcSxCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcZjCost = calcZjCost.add(zjCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-QT");
				inputPO.setMat_desc("其他制造费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcZjCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDaCost = calcDaCost.add(daCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-QT");
				inputPO.setMat_desc("其他制造费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDaCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcDbCost = calcDbCost.add(dbCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-QT");
				inputPO.setMat_desc("其他制造费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcDbCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){

				// 计算出值
				calcCtCost = calcCtCost.add(ctCost).add(jfCost.divide(sumPlAmount,2).multiply(plDto.getAsBigDecimal("pl_amount")));
				// 插入消耗表
				InputPO inputPO = new InputPO();
				// 主键
				String id = UUIDGenerator.nextIdentifier();
				inputPO.setId(id);
				inputPO.setPl_prediction_code(plDto.getAsString("pl_code").concat(pDto.getAsString("prediction_month")));
				inputPO.setPrediction_month(pDto.getAsString("prediction_month"));
				inputPO.setPl_code(plDto.getAsString("pl_code"));
				inputPO.setInput_type_code("ZZ");
				inputPO.setInput_type_desc("制造费");
				inputPO.setMat_code("ZZ-QT");
				inputPO.setMat_desc("其他制造费");
				inputPO.setInput_use_flag("+");
				inputPO.setAmount(calcCtCost);
				inputPO.setUnit("元");
				inputPO.setPrice(BigDecimal.ZERO);
				inputPO.setCreate_dt(new Date());
				inputList.add(inputPO);
			}
		}
		
		// 批量插入消耗表
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < inputList.size(); i++) {
					executor.insert("Input.insert", inputList.get(i));
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	
	
}
