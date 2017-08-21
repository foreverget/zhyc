package com.app.prediction.report.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.g4studio.common.dao.impl.DaoImpl;
import org.g4studio.common.service.impl.BaseServiceImpl;
import org.g4studio.core.metatype.Dto;
import org.g4studio.core.metatype.impl.BaseDto;
import org.g4studio.core.orm.xibatis.sqlmap.client.SqlMapExecutor;
import org.g4studio.core.orm.xibatis.support.SqlMapClientCallback;
import org.g4studio.system.common.util.idgenerator.UUIDGenerator;

import com.app.prediction.productline.entity.InputPO;
import com.app.prediction.report.entity.PlReportPO;
import com.app.prediction.report.entity.PzReportPO;
import com.app.prediction.report.service.PlReportService;
import com.app.prediction.util.AppConstants;

public class PlReportServiceImpl extends BaseServiceImpl implements PlReportService {

	private static Log log = LogFactory.getLog(PlReportServiceImpl.class);

	public void doCreatePlReport(final Dto pDto) {
		
		// 查找产线
		//pDto.put("isValid", "1");
		final List productLineList1 = g4Dao.queryForList("ProductLine.findProductLineList", pDto);
		
		// 先删除原有报表
		g4Dao.delete("PlReport.deleteByPredictionMonth", pDto.getAsString("predictionMonth"));
		
		// 创建各产线制造费用
		this.doCreateZzInputCost(pDto.getAsString("predictionMonth"), productLineList1);
		
		// 总产量合计
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		// 商品产量合计
		BigDecimal sumGoodsAmount = BigDecimal.ZERO;
		// 效益1合计
		BigDecimal sumB1= BigDecimal.ZERO;
		// 效益2合计
		BigDecimal sumB2= BigDecimal.ZERO;
		// 效益3合计
		BigDecimal sumB3= BigDecimal.ZERO;
		final List productLineList2 = g4Dao.queryForList("ProductLine.findProductLineList", pDto);
		for (int i = 0; i < productLineList2.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList2.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PlReportPO plReportPO = new PlReportPO();
			plReportPO.setId(id);
			plReportPO.setPl_prediction_code(plDto.getAsString("pl_prediction_code"));
			plReportPO.setPrediction_month(pDto.getAsString("predictionMonth"));
			plReportPO.setPl_code(plDto.getAsString("pl_code"));
			plReportPO.setPl_desc(plDto.getAsString("pl_desc"));
			// 总产量
			plReportPO.setPl_amount(plDto.getAsBigDecimal("pl_amount"));
			sumPlAmount = sumPlAmount.add(plReportPO.getPl_amount());
			// 商品量
			plReportPO.setGoods_amount(plDto.getAsBigDecimal("goods_amount"));
			sumGoodsAmount = sumGoodsAmount.add(plReportPO.getGoods_amount());
			// 单位成本 = 各工序分别累计“消耗量*单价”除以“产量”
			BigDecimal unitCost = (plDto.getAsBigDecimal("main_input_cost")
					 			  .add(plDto.getAsBigDecimal("item_input_cost"))
					 			  .add(plDto.getAsBigDecimal("zz_input_cost")))
					 			  .divide(plReportPO.getPl_amount(),2, BigDecimal.ROUND_HALF_UP);
			plReportPO.setUnit_cost(unitCost);
			// 售价 = 每个物料 （售价*外销品量）/外销量总和
			BigDecimal sellPrice = plDto.getAsBigDecimal("sale_cost")
								   .divide(plReportPO.getGoods_amount(),2, BigDecimal.ROUND_HALF_UP);
			plReportPO.setSell_price(sellPrice);
			// 价差 = 售价-成本
			plReportPO.setDiff_price(plReportPO.getSell_price().subtract(plReportPO.getUnit_cost()));
			// 效益 （元）=价差*商品量
			plReportPO.setBenefit_1(plReportPO.getDiff_price().multiply(plReportPO.getGoods_amount()));
			sumB1 = sumB1.add(plReportPO.getBenefit_1());
			// 效益（万元 ） =效益（元）/10000 
			plReportPO.setBenefit_2(plReportPO.getBenefit_1().divide(BigDecimal.valueOf(10000),2, BigDecimal.ROUND_HALF_UP));
			sumB2 = sumB2.add(plReportPO.getBenefit_2());
			// 效益（万元 ） =效益（万元） 不包括2#镀锌
			if(!AppConstants.PL_DB.equals(plReportPO.getPl_code())){
				plReportPO.setBenefit_3(plReportPO.getBenefit_2());
			}
			sumB3 = sumB3.add(plReportPO.getBenefit_3()==null?BigDecimal.ZERO:plReportPO.getBenefit_3());
			plReportPO.setCreate_dt(new Date());
			g4Dao.insert("PlReport.insert", plReportPO);
		}
		// 计算合计行
		PlReportPO plReportPO = new PlReportPO();
		BaseDto tmpDto = (BaseDto)productLineList2.get(0);
		// 主键
		String id = UUIDGenerator.nextIdentifier();
		plReportPO.setId(id);
		plReportPO.setPl_prediction_code(tmpDto.getAsString("pl_prediction_code"));
		plReportPO.setPrediction_month(pDto.getAsString("predictionMonth"));
		plReportPO.setPl_code("ZZ");
		plReportPO.setPl_desc("总计");
		plReportPO.setPl_amount(sumPlAmount);
		plReportPO.setGoods_amount(sumGoodsAmount);
		plReportPO.setBenefit_1(sumB1);
		plReportPO.setBenefit_2(sumB2);
		plReportPO.setBenefit_3(sumB3);
		g4Dao.insert("PlReport.insert", plReportPO);
	}
	
	/**
	 * 计算出各产线的投料 制造费用
	 * 
	 * @param predictionMonth
	 * @param productLineList
	 */
	private void doCreateZzInputCost(String predictionMonth,List productLineList){
		
		Dto parame1 = new BaseDto();
		parame1.put("predictionMonth", predictionMonth);
		// 导入的折旧费记录不做删除
		parame1.put("matCode", "ZZ-ZJ");
		// 删除各产线除折扣以外的所有制造费用
		g4Dao.delete("Input.deleteAllZzCostButZjByParame", parame1);
		
		// 同时更新每条产线的zz_input_cost 制造费初始化值为0
		g4Dao.update("ProductLine.updateZzInputCostZeroByPm",parame1);
		
		// 总产量合计
		BigDecimal sumPlAmount = BigDecimal.ZERO;
		// 循环计算所有产量
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			sumPlAmount = sumPlAmount.add(plDto.getAsBigDecimal("pl_amount"));
		}
		this.doCreateGzInputCost(predictionMonth,productLineList,sumPlAmount);
		this.doCreateWxInputCost(predictionMonth,productLineList,sumPlAmount);
		this.doCreateJwInputCost(predictionMonth,productLineList,sumPlAmount);
		this.doCreateBzInputCost(predictionMonth,productLineList,sumPlAmount);
		this.doCreateQtInputCost(predictionMonth,productLineList,sumPlAmount);
		
		Dto parame2 = new BaseDto();
		// 计算产线zzInputCost
		for (int i = 0; i < productLineList.size(); i++) {
			Dto plDto = new BaseDto();
			plDto = (BaseDto)productLineList.get(i);
			// 查找产线的制造费
			parame2.put("plPredictionCode", plDto.getAsString("pl_prediction_code"));
			parame2.put("inputTypeCode", AppConstants.INPUT_TYPE_CODE_ZZ);
			List plInputZzCostList = g4Dao.queryForList("Input.findInputList", parame2);
			BigDecimal zzInputCost = BigDecimal.ZERO;
			for(int j = 0; j < plInputZzCostList.size(); j++){
				BaseDto baseDto = (BaseDto)plInputZzCostList.get(j);
				BigDecimal amount = baseDto.getAsBigDecimal("amount");
				zzInputCost = zzInputCost.add(amount);
			}
			parame2.put("zzInputCost", zzInputCost);
			g4Dao.update("ProductLine.update", parame2);
			parame2.clear();
		}
		
	}

	private List<InputPO> doCreateGzInputCost(String predictionMonth,List productLineList,BigDecimal sumPlAmount){
		
		Dto parame = new BaseDto();
		parame.put("predictionMonth", predictionMonth);
		List plGzCostList = g4Dao.queryForList("PlGzCost.findPlGzCostList", parame);
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
		
		for (int i = 0; i < plGzCostList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)plGzCostList.get(i);					
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				dbCost = dbCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("gz_cost"));
			}
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
		// 循环计算各产线工资附加 并更新input表制造费
		for (int i = 0; i < productLineList.size(); i++) {
//			Dto plDto = new BaseDto();
			Dto plDto = (BaseDto)productLineList.get(i);
			BigDecimal avgJfCost = jfCost.divide(sumPlAmount,2, BigDecimal.ROUND_HALF_UP)
					.multiply(plDto.getAsBigDecimal("pl_amount"));
			
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				calcSxCost = calcSxCost.add(sxCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto,AppConstants.INPUT_ZZ_GZ,AppConstants.INPUT_DESC_ZZ_GZ,calcSxCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){
				calcZjCost = calcZjCost.add(zjCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto,AppConstants.INPUT_ZZ_GZ,AppConstants.INPUT_DESC_ZZ_GZ,calcZjCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){
				calcDaCost = calcDaCost.add(daCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto,AppConstants.INPUT_ZZ_GZ,AppConstants.INPUT_DESC_ZZ_GZ,calcDaCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){
				calcDbCost = calcDbCost.add(dbCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto,AppConstants.INPUT_ZZ_GZ,AppConstants.INPUT_DESC_ZZ_GZ,calcDbCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){
				calcCtCost = calcCtCost.add(ctCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto,AppConstants.INPUT_ZZ_GZ,AppConstants.INPUT_DESC_ZZ_GZ,calcCtCost);
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
		return inputList;
	}

	private List<InputPO> doCreateWxInputCost(String predictionMonth,List productLineList,BigDecimal sumPlAmount){

		Dto parame = new BaseDto();
		parame.put("predictionMonth", predictionMonth);
		List plWxCostList = g4Dao.queryForList("PlWxCost.findPlWxCostList", parame);
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
		
		for (int i = 0; i < plWxCostList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)plWxCostList.get(i);					
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				dbCost = dbCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("sum_row_cost"));
			}
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
			Dto plDto = (BaseDto)productLineList.get(i);
			BigDecimal avgJfCost = jfCost.divide(sumPlAmount,2, BigDecimal.ROUND_HALF_UP)
					.multiply(plDto.getAsBigDecimal("pl_amount"));
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				calcSxCost = calcSxCost.add(sxCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_WX,  AppConstants.INPUT_DESC_ZZ_WX, calcSxCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){
				calcZjCost = calcZjCost.add(zjCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_WX,  AppConstants.INPUT_DESC_ZZ_WX, calcZjCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){
				calcDaCost = calcDaCost.add(daCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_WX,  AppConstants.INPUT_DESC_ZZ_WX, calcDaCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){
				calcDbCost = calcDbCost.add(dbCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_WX,  AppConstants.INPUT_DESC_ZZ_WX, calcDbCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){
				calcCtCost = calcCtCost.add(ctCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_WX,  AppConstants.INPUT_DESC_ZZ_WX, calcCtCost);
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
		return inputList;
	}

	private List<InputPO> doCreateJwInputCost(String predictionMonth,List productLineList,BigDecimal sumPlAmount){
		
		Dto parame = new BaseDto();
		parame.put("predictionMonth", predictionMonth);
		List plJwCostList = g4Dao.queryForList("PlJwCost.findPlJwCostList", parame);
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
		
		for (int i = 0; i < plJwCostList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)plJwCostList.get(i);					
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				dbCost = dbCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("jw_cost"));
			}
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
			Dto plDto = (BaseDto)productLineList.get(i);
			BigDecimal avgJfCost = jfCost.divide(sumPlAmount,2, BigDecimal.ROUND_HALF_UP)
					.multiply(plDto.getAsBigDecimal("pl_amount"));
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				calcSxCost = calcSxCost.add(sxCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_JW,  AppConstants.INPUT_DESC_ZZ_JW, calcSxCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){
				calcZjCost = calcZjCost.add(zjCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_JW,  AppConstants.INPUT_DESC_ZZ_JW, calcZjCost);
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){
				calcDaCost = calcDaCost.add(daCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_JW,  AppConstants.INPUT_DESC_ZZ_JW, calcDaCost);
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){
				calcDbCost = calcDbCost.add(dbCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_JW,  AppConstants.INPUT_DESC_ZZ_JW, calcDbCost);
				inputList.add(inputPO);
				
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){
				calcCtCost = calcCtCost.add(ctCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_JW,  AppConstants.INPUT_DESC_ZZ_JW, calcCtCost);
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
		return inputList;
	}

	private List<InputPO> doCreateBzInputCost(String predictionMonth,List productLineList,BigDecimal sumPlAmount){
		Dto parame = new BaseDto();
		parame.put("predictionMonth", predictionMonth);
		List plBzCostList = g4Dao.queryForList("PlBzCost.findPlBzCostList", parame);
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
		
		for (int i = 0; i < plBzCostList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)plBzCostList.get(i);					
			if(AppConstants.PL_SX.equals(baseDto.getAsString("pl_code"))){
				sxCost = sxCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_ZJ.equals(baseDto.getAsString("pl_code"))){
				zjCost = zjCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_DA.equals(baseDto.getAsString("pl_code"))){
				daCost = daCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_DB.equals(baseDto.getAsString("pl_code"))){
				dbCost = dbCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.PL_CT.equals(baseDto.getAsString("pl_code"))){
				ctCost = ctCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}else if(AppConstants.JG.equals(baseDto.getAsString("pl_code"))
					||AppConstants.GF.equals(baseDto.getAsString("pl_code"))){
				jfCost = jfCost.add(baseDto.getAsBigDecimal("bz_cost"));
			}
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

			BigDecimal avgJfCost = jfCost.divide(sumPlAmount,2, BigDecimal.ROUND_HALF_UP)
					.multiply(plDto.getAsBigDecimal("pl_amount"));
			
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				calcSxCost = calcSxCost.add(sxCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_BZ,  AppConstants.INPUT_DESC_ZZ_BZ, calcSxCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){
				calcZjCost = calcZjCost.add(zjCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_BZ,  AppConstants.INPUT_DESC_ZZ_BZ, calcZjCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){
				calcDaCost = calcDaCost.add(daCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_BZ,  AppConstants.INPUT_DESC_ZZ_BZ, calcDaCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){
				calcDbCost = calcDbCost.add(dbCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_BZ,  AppConstants.INPUT_DESC_ZZ_BZ, calcDbCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){
				calcCtCost = calcCtCost.add(ctCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_BZ,  AppConstants.INPUT_DESC_ZZ_BZ, calcCtCost);
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
		return inputList;
	}

	private List<InputPO> doCreateQtInputCost(String predictionMonth,List productLineList,BigDecimal sumPlAmount){
		Dto parame = new BaseDto();
		parame.put("predictionMonth", predictionMonth);
		List plQtCostList = g4Dao.queryForList("PlQtCost.findPlQtCostList", parame);
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
		for (int i = 0; i < plQtCostList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)plQtCostList.get(i);					
			if("合计".equals(baseDto.getAsString("cost_desc"))){
				// 计算中间变量的值
				sxCost = baseDto.getAsBigDecimal("sx_cost");
				zjCost = baseDto.getAsBigDecimal("zj_cost");
				daCost = baseDto.getAsBigDecimal("da_cost");
				dbCost = baseDto.getAsBigDecimal("db_cost");
				ctCost = baseDto.getAsBigDecimal("ct_cost");
				jfCost = baseDto.getAsBigDecimal("jg_cost");
			}
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
			Dto plDto = (BaseDto)productLineList.get(i);
			BigDecimal avgJfCost = jfCost.divide(sumPlAmount,2, BigDecimal.ROUND_HALF_UP)
					.multiply(plDto.getAsBigDecimal("pl_amount"));
			if(AppConstants.PL_SX.equals(plDto.getAsString("pl_code"))){
				calcSxCost = calcSxCost.add(sxCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_QT,  AppConstants.INPUT_DESC_ZZ_QT, calcSxCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_ZJ.equals(plDto.getAsString("pl_code"))){
				calcZjCost = calcZjCost.add(zjCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_QT,  AppConstants.INPUT_DESC_ZZ_QT, calcZjCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DA.equals(plDto.getAsString("pl_code"))){
				calcDaCost = calcDaCost.add(daCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_QT,  AppConstants.INPUT_DESC_ZZ_QT, calcDaCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_DB.equals(plDto.getAsString("pl_code"))){
				calcDbCost = calcDbCost.add(dbCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_QT,  AppConstants.INPUT_DESC_ZZ_QT, calcDbCost);
				inputList.add(inputPO);
			}else if(AppConstants.PL_CT.equals(plDto.getAsString("pl_code"))){
				calcCtCost = calcCtCost.add(ctCost).add(avgJfCost);
				InputPO inputPO = this.doBuildInputPo(plDto, AppConstants.INPUT_ZZ_QT,  AppConstants.INPUT_DESC_ZZ_QT, calcCtCost);
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
		return inputList;
	}
	
	/**
	 * 组建制造费投料
	 * @param plDto
	 * @param matCode
	 * @param matDesc
	 * @param cost
	 * @return
	 */
	private InputPO doBuildInputPo(Dto plDto,String matCode,String matDesc,BigDecimal cost){
		// 插入消耗表
		InputPO inputPO = new InputPO();
		// 主键
		String id = UUIDGenerator.nextIdentifier();
		inputPO.setId(id);
		inputPO.setPl_prediction_code(plDto.getAsString("pl_prediction_code"));
		inputPO.setPrediction_month(plDto.getAsString("prediction_month"));
		inputPO.setPl_code(plDto.getAsString("pl_code"));
		inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_ZZ);
		inputPO.setInput_type_desc(AppConstants.INPUT_TYPE_DESC_ZZ);
		inputPO.setMat_code(matCode);
		inputPO.setMat_desc(matDesc);
		inputPO.setInput_use_flag("+");
		inputPO.setAmount(cost);
		inputPO.setUnit("元");
		inputPO.setPrice(BigDecimal.ONE);
		inputPO.setCreate_dt(new Date());
		return inputPO;
	}
	
	public void doCreatePzReport(final Dto pDto) {
		
	    //  获取产线物料分组数据
		//pDto.put("isValid", "1");
		final List plProductMatGroupList = g4Dao.queryForList("PzReport.getPlProductMatGroupList", pDto);
		// 先删除原报表数据
		g4Dao.delete("PzReport.deleteByPredictionMonth", pDto.getAsString("predictionMonth"));
		
		for (int i = 0; i < plProductMatGroupList.size(); i++) {
			
			Dto plDto = new BaseDto();
			plDto = (BaseDto)plProductMatGroupList.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			PzReportPO pzReportPO = new PzReportPO();
			pzReportPO.setId(id);
			pzReportPO.setPl_prediction_code(plDto.getAsString("pl_prediction_code"));
			pzReportPO.setPrediction_month(pDto.getAsString("predictionMonth"));
			pzReportPO.setPl_code(plDto.getAsString("pl_code"));
			pzReportPO.setPl_desc(plDto.getAsString("pl_desc"));
			pzReportPO.setMat_code(plDto.getAsString("mat_code"));
			pzReportPO.setMat_desc(plDto.getAsString("mat_desc"));
			// 销售量
			pzReportPO.setOutput_amount(plDto.getAsBigDecimal("output_amount"));
			// 销售价格
			pzReportPO.setSell_price(plDto.getAsBigDecimal("sell_price"));
			// 成本   =  按产线分别计算其他投料的成本+主投料
			pzReportPO.setUnit_cost(this.doCompPlInputUnitCost(
							pzReportPO.getPl_prediction_code(),
							pzReportPO.getPl_code()).add(plDto.getAsBigDecimal("main_feed_cost")));
			// 价差 = 售价 - 成本
			pzReportPO.setDiff_price(pzReportPO.getSell_price().subtract(pzReportPO.getUnit_cost()));
			// 效益(元) = 价差 * 商品量
			pzReportPO.setBenefit_1(pzReportPO.getDiff_price().multiply(pzReportPO.getOutput_amount()));
			// 效益(万元) = 效益(元 ) * 10000
			pzReportPO.setBenefit_2(pzReportPO.getBenefit_1().divide(new BigDecimal(10000), 2));
			pzReportPO.setCreate_dt(new Date());
			g4Dao.insert("PzReport.insert", pzReportPO);
		}
	}
	
	/**
	 * 计算单位成本
	 * @param plPredictionCode
	 * @param predictionMonth
	 */
	private BigDecimal doCompPlInputUnitCost(String plPredictionCode,String plCode){
		Dto paramDto = new BaseDto();
		paramDto.put("plPredictionCode", plPredictionCode);
		// 获取其他投料数据，并按类投料种类分别计算出成本
		final List plInputMatGroupList = g4Dao.queryForList("PzReport.getPlInputMatGroupList", paramDto);
		BigDecimal inputCost = BigDecimal.ZERO;
		/**
		 * 
		锌锭：       锌锭价值总和/内供外销产量和
		废钢：	废钢价值总和/内供外销产量和
		燃料：	燃料价值总和/内供外销产量和
		动力：	动力价值总和/内供外销产量和
		
		辅材：	辅材价值总和/总外销量  酸洗
		辅材：	辅材价值总和/内供外销产量和  单机架、彩涂、1#镀锌、2#镀锌
		
		制造费：	（工资及附加+折旧+维修+其他制造费+机物料消耗）/（内供外销产量和）+（包装费/总外销量）酸洗、单机架
		制造费：（工资及附加+折旧+维修+其他制造费+机物料消耗+包装费）/（内供外销产量和）  彩涂、1#镀锌、2#镀锌
		 */
		// 锌锭价值总和/内供外销产量和  物料消耗－原料
		BigDecimal ylInputCost = BigDecimal.ZERO;
		// 废钢价值总和  物料回收原料 HS
		BigDecimal hsInputCost = BigDecimal.ZERO;
		// 辅材价值总和
		BigDecimal fcInputCost = BigDecimal.ZERO;
		// 燃料价值总和
		BigDecimal rlInputCost = BigDecimal.ZERO;
		// 动力价值总和
		BigDecimal dlInputCost = BigDecimal.ZERO;
		// 制造费： 工资及附加+折旧+维修+其他制造费+机物料消耗 价值总和
		BigDecimal zz5InputCost = BigDecimal.ZERO;
		// 制造费： 包装费 价值总和
		BigDecimal zz1InputCost = BigDecimal.ZERO;
		// 制造费： 工资及附加+折旧+维修+其他制造费+机物料消耗+包装费 价值总和
		BigDecimal zzInputCost = BigDecimal.ZERO;
		
		// 消耗原料
		BigDecimal ylInputUnitCost = BigDecimal.ZERO;
		// 回收原料
		BigDecimal hsInputUnitCost = BigDecimal.ZERO;
		// 燃料
		BigDecimal rlInputUnitCost = BigDecimal.ZERO;
		// 动力
		BigDecimal dlInputUnitCost = BigDecimal.ZERO;
		// 辅材
		BigDecimal fcInputUnitCost = BigDecimal.ZERO;
		// 制造费
		BigDecimal zzInputUnitCost = BigDecimal.ZERO;
		
		for(int i = 0; i < plInputMatGroupList.size(); i++){
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)plInputMatGroupList.get(i);
			// 物料消耗－原料
			if("YL".equals(baseDto.getAsString("input_type_code"))){
				ylInputCost = ylInputCost.add(baseDto.getAsBigDecimal("amount").multiply(baseDto.getAsBigDecimal("price")));
			// 物料回收－原料
			}else if("HS".equals(baseDto.getAsString("input_type_code"))){
				hsInputCost = hsInputCost.add(baseDto.getAsBigDecimal("amount").multiply(baseDto.getAsBigDecimal("price")));
			// 辅材
			}else if("FC".equals(baseDto.getAsString("input_type_code"))){
				fcInputCost = fcInputCost.add(baseDto.getAsBigDecimal("amount").multiply(baseDto.getAsBigDecimal("price")));
			// 燃料
			}else if("RL".equals(baseDto.getAsString("input_type_code"))){
				rlInputCost = rlInputCost.add(baseDto.getAsBigDecimal("amount").multiply(baseDto.getAsBigDecimal("price")));
			// 动力
			}else if("DL".equals(baseDto.getAsString("input_type_code"))){
				dlInputCost = dlInputCost.add(baseDto.getAsBigDecimal("amount").multiply(baseDto.getAsBigDecimal("price")));
			// 包装费
			}else if("ZZ".equals(baseDto.getAsString("input_type_code"))&& "ZZ-BZ".equals(baseDto.getAsString("mat_code"))){
				zz1InputCost = zz1InputCost.add(baseDto.getAsBigDecimal("amount"));
			}else if("ZZ".equals(baseDto.getAsString("input_type_code"))&& !"ZZ-BZ".equals(baseDto.getAsString("mat_code"))){
				zz5InputCost = zz5InputCost.add(baseDto.getAsBigDecimal("amount"));
			}
		}
		// 总制造费
		zzInputCost = zz5InputCost.add(zz1InputCost);
		
		BaseDto plDto = (BaseDto) plInputMatGroupList.get(0);
		// 计算
		ylInputUnitCost = ylInputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2);
		hsInputUnitCost = hsInputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2);
		rlInputUnitCost = rlInputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2);
		dlInputUnitCost = dlInputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2);
		fcInputUnitCost = fcInputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2);
		zzInputUnitCost = zzInputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2);
		// 酸洗辅材计算特殊
		if("SX".equals(plCode)){
			fcInputUnitCost = fcInputCost.divide(plDto.getAsBigDecimal("goods_amount"), 2);
		}
		// 酸洗、单机架制造费计算特殊
		if("SX".equals(plCode)||"ZJ".equals(plCode)){
			zzInputUnitCost  = zz5InputCost.divide(plDto.getAsBigDecimal("pl_amount"), 2)
							   .add(zz1InputCost.divide(plDto.getAsBigDecimal("goods_amount"), 2));
		}
		
		inputCost = ylInputUnitCost
				    .add(rlInputUnitCost)
				    .add(dlInputUnitCost)
				    .add(fcInputUnitCost)
				    .add(zzInputUnitCost)
				    .subtract(hsInputUnitCost);
		
		log.debug("============================================================");
		log.debug(plPredictionCode+"，zz1InputCost=制造费-包装费="+zz1InputCost);
		log.debug(plPredictionCode+"，zz5InputCost=制造费-5项费和="+zz5InputCost);
		log.debug(plPredictionCode+"，zzInputCost=制造费总和="+zzInputCost);
		log.debug(plPredictionCode+"，pl_amount=总产量="+plDto.getAsBigDecimal("pl_amount"));
		log.debug(plPredictionCode+"，ylInputCost=原料量总费用="+ylInputCost);
		log.debug(plPredictionCode+"，hsInputCost=回收量总费用="+hsInputCost);
		log.debug(plPredictionCode+"，rlInputCost=燃料量总费用="+rlInputCost);
		log.debug(plPredictionCode+"，dlInputCost=动力量总费用="+dlInputCost);
		log.debug(plPredictionCode+"，fcInputCost=辅材量总费用="+fcInputCost);

		log.debug(plPredictionCode+"，ylInputUnitCost=原料量单位成本="+ylInputUnitCost);
		log.debug(plPredictionCode+"，hsInputUnitCost=回收量单位成本="+hsInputUnitCost);
		log.debug(plPredictionCode+"，rlInputUnitCost=燃料量单位成本="+rlInputUnitCost);
		log.debug(plPredictionCode+"，dlInputUnitCost=动力量单位成本="+dlInputUnitCost);
		log.debug(plPredictionCode+"，fcInputUnitCost=辅材量单位成本="+fcInputUnitCost);
		log.debug(plPredictionCode+"，zzInputUnitCost=制造费单位成本="+zzInputUnitCost);
		
		log.debug(plPredictionCode+"，inputCost=单位成本="+inputCost);
		
		
		return inputCost;
	}
	
}
