package com.app.prediction.productline.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.g4studio.common.service.impl.BaseServiceImpl;
import org.g4studio.core.metatype.Dto;
import org.g4studio.core.metatype.impl.BaseDto;
import org.g4studio.core.orm.xibatis.sqlmap.client.SqlMapExecutor;
import org.g4studio.core.orm.xibatis.support.SqlMapClientCallback;
import org.g4studio.core.util.G4Utils;
import org.g4studio.core.web.report.excel.ExcelReader;
import org.g4studio.system.common.util.idgenerator.UUIDGenerator;

import com.app.prediction.productline.entity.InputPO;
import com.app.prediction.productline.entity.ProductLinePO;
import com.app.prediction.productline.entity.ProductPO;
import com.app.prediction.productline.service.ProductLineService;
import com.app.prediction.util.AppConstants;

public class ProductLineServiceImpl extends BaseServiceImpl implements ProductLineService {

	/**
	 * 生成产线
	 * 
	 * @param pDto
	 * @return
	 */
	public void doCreateProductLines(final Dto pDto) {
		
		// 查找配置产线
		pDto.put("isValid", "1");
		pDto.put("isPrediction", "1");
		final List productLineConfigList = g4Dao.queryForList("ProductLineConfig.findProductLineConfigList", pDto);
		// 批量插入操作
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < productLineConfigList.size(); i++) {
					
					Dto plcDto = new BaseDto();
					plcDto = (BaseDto)productLineConfigList.get(i);					
					// 主键
					String id = UUIDGenerator.nextIdentifier();
					ProductLinePO productLinePO = new ProductLinePO();
					productLinePO.setId(id);
					productLinePO.setPl_prediction_code(plcDto.getAsString("pl_code").concat(pDto.getAsString("predictionMonth")));
					productLinePO.setPrediction_month(pDto.getAsString("predictionMonth"));
					productLinePO.setPl_code(plcDto.getAsString("pl_code"));
					productLinePO.setPl_desc(plcDto.getAsString("pl_desc"));
					productLinePO.setPl_amount(BigDecimal.ZERO);
					productLinePO.setGoods_amount(BigDecimal.ZERO);
					
					executor.insert("ProductLine.insert", productLinePO);
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	public void doImportProductExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "output_type_desc,output_mat_code,output_mat_desc,output_amount,output_unit,output_sale_price,feed_mat_code,feed_mat_desc,feed_amount,feed_unit,feed_sale_price";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		doImpProductData(pDto,exclelist);
	}
	
	public void doImportInputExcel(Dto pDto,InputStream inputStream) throws Exception{
		// 列
		String metaData = "input_type_desc,mat_code,mat_desc,amount,unit,price";
		ExcelReader excelReader = new ExcelReader(metaData, inputStream);
		// 从第一行开始，到最后一行
		final List exclelist = excelReader.read(1, 0);
		doImpInputData(pDto,exclelist);
	}
	
	private void doImpProductData(Dto pDto,List productList) {
		
		final Dto tempDto = pDto;
		// 先删除
		Dto parame = new BaseDto();
		parame.put("plPredictionCode", pDto.getAsString("pl_prediction_code"));
		g4Dao.delete("Product.deleteByPlPredictionCode", parame);
		
		// 总产量= 外销+内供
		BigDecimal plAmount = BigDecimal.ZERO;
		// 商品产量 = 外销
		BigDecimal goodsAmount = BigDecimal.ZERO;
		// 主投料 消耗量*单价 和
		BigDecimal mainInputCost = BigDecimal.ZERO;
		// 产量*单价 和
		BigDecimal saleCost = BigDecimal.ZERO;
		
		// 生成成品实体数据，写入表
		for (int i = 0; i < productList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)productList.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			ProductPO productPO = new ProductPO();
			productPO.setId(id);
			productPO.setPl_prediction_code(tempDto.getAsString("pl_prediction_code"));
			productPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			productPO.setPl_code(tempDto.getAsString("pl_code"));
			productPO.setPl_desc(tempDto.getAsString("pl_desc"));
			
			productPO.setOutput_mat_code(baseDto.getAsString("output_mat_code"));
			productPO.setOutput_mat_desc(baseDto.getAsString("output_mat_desc"));
			productPO.setOutput_amount(baseDto.getAsBigDecimal("output_amount"));
			plAmount = plAmount.add(productPO.getOutput_amount());
			productPO.setOutput_unit(baseDto.getAsString("output_unit"));
			productPO.setOutput_sale_price(baseDto.getAsBigDecimal("output_sale_price"));
			productPO.setOutput_type_desc(baseDto.getAsString("output_type_desc"));

			if(AppConstants.OUTPUT_TYPE_DESC_W.equals(baseDto.getAsString("output_type_desc"))){
				goodsAmount = goodsAmount.add(productPO.getOutput_amount());
				productPO.setOutput_type_code(AppConstants.OUTPUT_TYPE_CODE_W);
				// 售价*外销 产量 累加和
				saleCost = saleCost.add(productPO.getOutput_amount().multiply(productPO.getOutput_sale_price()));
			}else if(AppConstants.OUTPUT_TYPE_DESC_N.equals(baseDto.getAsString("output_type_desc"))){
				productPO.setOutput_type_code(AppConstants.OUTPUT_TYPE_CODE_N);
			}
			productPO.setFeed_mat_code(baseDto.getAsString("feed_mat_code"));
			productPO.setFeed_mat_desc(baseDto.getAsString("feed_mat_desc"));
			productPO.setFeed_amount(baseDto.getAsBigDecimal("feed_amount"));
			productPO.setFeed_unit(baseDto.getAsString("feed_unit"));
			productPO.setFeed_sale_price(baseDto.getAsBigDecimal("feed_sale_price"));
			// 分别累计 消耗量*单价
			mainInputCost = mainInputCost.add(productPO.getFeed_amount().multiply(productPO.getFeed_sale_price()));
			productPO.setCreate_dt(new Date());
			g4Dao.insert("Product.insert", productPO);
		}
				
		// 更新产线表
		// 总产量
		parame.put("plAmount", plAmount);
		// 总商品量
		parame.put("goodsAmount", goodsAmount);
	    // 主投料量费用 
		parame.put("mainInputCost", mainInputCost);
		// 售价*外销 产量 累加和 = 总外销量费用
		parame.put("saleCost", saleCost);
		g4Dao.update("ProductLine.updatebyPpc", parame);
		
	}
	
	private void doImpInputData(Dto pDto,List inputList) {
		final Dto tempDto = pDto;
		// 先删除
		Dto parame = new BaseDto();
		parame.put("plPredictionCode", pDto.getAsString("pl_prediction_code"));
		g4Dao.delete("Input.deleteByPlPredictionCode", parame);
		
		// 消耗量*单价 和  
		BigDecimal itemInputCost = BigDecimal.ZERO;
		
		// 生成实体数据，写入表
		for (int i = 0; i < inputList.size(); i++) {
			Dto baseDto = new BaseDto();
			baseDto = (BaseDto)inputList.get(i);					
			// 主键
			String id = UUIDGenerator.nextIdentifier();
			InputPO inputPO = new InputPO();
			inputPO.setId(id);
			inputPO.setPl_prediction_code(tempDto.getAsString("pl_prediction_code"));
			inputPO.setPrediction_month(tempDto.getAsString("prediction_month"));
			inputPO.setPl_code(tempDto.getAsString("pl_code"));
			inputPO.setInput_type_desc(baseDto.getAsString("input_type_desc"));
			
			if(AppConstants.INPUT_TYPE_DESC_YL.equals(inputPO.getInput_type_desc().trim())){
				inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_YL);
			}else if(AppConstants.INPUT_TYPE_DESC_DL.equals(inputPO.getInput_type_desc().trim())){
				inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_DL);
			}else if(AppConstants.INPUT_TYPE_DESC_RL.equals(inputPO.getInput_type_desc().trim())){
				inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_RL);
			}else if(AppConstants.INPUT_TYPE_DESC_HS.equals(inputPO.getInput_type_desc().trim())){
				inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_HS);
			}else if(AppConstants.INPUT_TYPE_DESC_FC.equals(inputPO.getInput_type_desc().trim())){
				inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_FC);
			}else if(AppConstants.INPUT_TYPE_DESC_ZZ.equals(inputPO.getInput_type_desc().trim())){
				inputPO.setInput_type_code(AppConstants.INPUT_TYPE_CODE_ZZ);
			}else{
				inputPO.setInput_type_code("无");
			}
			
			inputPO.setMat_code(baseDto.getAsString("mat_code"));
			inputPO.setMat_desc(baseDto.getAsString("mat_desc"));
			inputPO.setInput_use_flag("+");
			// 减法
			if(AppConstants.INPUT_TYPE_DESC_HS.equals(inputPO.getInput_type_desc())){
				inputPO.setInput_use_flag("-");
			}
			
			inputPO.setAmount(baseDto.getAsBigDecimal("amount"));
			inputPO.setUnit(baseDto.getAsString("unit"));
			inputPO.setPrice(baseDto.getAsBigDecimal("price"));
			// 不计算 制造费
			if(!AppConstants.INPUT_TYPE_DESC_ZZ.equals(inputPO.getInput_type_desc())){
				if("+".equals(inputPO.getInput_use_flag())){
					itemInputCost =  itemInputCost.add(inputPO.getAmount().multiply(inputPO.getPrice()));
				}else{
					itemInputCost =  itemInputCost.subtract(inputPO.getAmount().multiply(inputPO.getPrice()));
				}
			}
			
			inputPO.setCreate_dt(new Date());
			g4Dao.insert("Input.insert", inputPO);
		}
		
		// 更新产线表item_input_cost字段 ,导入部分 消耗量总费用
		parame.put("itemInputCost", itemInputCost);
		g4Dao.update("ProductLine.update", parame);
	}
	
	
	public String doImpProductAndInputExcel(Dto pDto,InputStream inputStreamProduct,InputStream inputStreamInput) throws Exception{ 
		StringBuffer resultMsg = new StringBuffer();
//		try {
			List productList = new ArrayList();
			List inputList = new ArrayList();
			Workbook productWorkbook = Workbook.getWorkbook(inputStreamProduct);
			Workbook inputWorkbook = Workbook.getWorkbook(inputStreamInput);
			int pBegin = 1;
			int pBack = 0;
			// 处理产品数据
			resultMsg.append("产品及主投料数据：<br>");
			String productMetaData = "output_type_desc,output_mat_code,output_mat_desc,output_amount,output_unit,output_sale_price,feed_mat_code,feed_mat_desc,feed_amount,feed_unit,feed_sale_price";
			Sheet productSheet = null;
			for(int m = 0; m < productWorkbook.getNumberOfSheets(); m++){
				productSheet = productWorkbook.getSheet(m);
				int rows = productSheet.getRows();
				for (int i = pBegin; i < rows - pBack; i++) {
					Dto rowDto = new BaseDto();
					Cell[] cells = productSheet.getRow(i);
					String[] arrMeta = productMetaData.trim().split(",");
					for (int j = 0; j < arrMeta.length; j++) {
						String key = arrMeta[j];
						if(G4Utils.isNotEmpty(key)) 
							if(null!=cells[j]){
								rowDto.put(key,cells[j].getContents());
							}
					}
					productList.add(rowDto);
				}
				// 处理产品数据
				if(AppConstants.PL_SX_DESC.equals(productSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_SX);
					pDto.put("pl_desc", AppConstants.PL_SX_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_SX+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_ZJ_DESC.equals(productSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_ZJ);
					pDto.put("pl_desc", AppConstants.PL_ZJ_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_ZJ+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_CT_DESC.equals(productSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_CT);
					pDto.put("pl_desc", AppConstants.PL_CT_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_CT+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_DA_DESC.equals(productSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_DA);
					pDto.put("pl_desc", AppConstants.PL_DA_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_DA+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_DB_DESC.equals(productSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_DB);
					pDto.put("pl_desc", AppConstants.PL_DB_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_DB+pDto.getAsString("prediction_month"));
				}else{
					throw new RuntimeException("请检查产品及主投料EXCEL模板sheet页名称是否合法：<br>"
							+ AppConstants.PL_SX_DESC + "<br>"
							+ AppConstants.PL_ZJ_DESC+"<br>"
							+ AppConstants.PL_CT_DESC+"<br>"
							+ AppConstants.PL_DA_DESC+"<br>"
							+ AppConstants.PL_DB_DESC+"<br>"
							);
				}
				
				doImpProductData(pDto,productList);
				
				resultMsg.append("【"+productSheet.getName()+"】产线，成功导入【"+productList.size()+"】条<br>");
				productList.clear();
			}
			resultMsg.append("==================================================<br>");
			resultMsg.append("其他投料数据：<br>");
			// 处理其他投料数据
			String inputMetaData = "input_type_desc,mat_code,mat_desc,amount,unit,price";
			Sheet inputSheet = null;
			for(int m = 0; m < inputWorkbook.getNumberOfSheets(); m++){
				inputSheet = inputWorkbook.getSheet(m);
				int rows = inputSheet.getRows();
				for (int i = pBegin; i < rows - pBack; i++) {
					Dto rowDto = new BaseDto();
					Cell[] cells = inputSheet.getRow(i);
					String[] arrMeta = inputMetaData.trim().split(",");
					for (int j = 0; j < arrMeta.length; j++) {
						String key = arrMeta[j];
						if(G4Utils.isNotEmpty(key)) 
							if(null!=cells[j]){
								rowDto.put(key,cells[j].getContents());
							}
					}
					inputList.add(rowDto);
				}
				// 处理投料数据

				if(AppConstants.PL_SX_DESC.equals(inputSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_SX);
					pDto.put("pl_desc", AppConstants.PL_SX_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_SX+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_ZJ_DESC.equals(inputSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_ZJ);
					pDto.put("pl_desc", AppConstants.PL_ZJ_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_ZJ+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_CT_DESC.equals(inputSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_CT);
					pDto.put("pl_desc", AppConstants.PL_CT_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_CT+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_DA_DESC.equals(inputSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_DA);
					pDto.put("pl_desc", AppConstants.PL_DA_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_DA+pDto.getAsString("prediction_month"));
				}else if (AppConstants.PL_DB_DESC.equals(inputSheet.getName())){
					pDto.put("pl_code", AppConstants.PL_DB);
					pDto.put("pl_desc", AppConstants.PL_DB_DESC);
					pDto.put("pl_prediction_code", AppConstants.PL_DB+pDto.getAsString("prediction_month"));
				}else{
					throw new Exception("请检查其他投料EXCEL模板sheet页名称是否合法：<br>"
							+ AppConstants.PL_SX_DESC + "<br>"
							+ AppConstants.PL_ZJ_DESC+"<br>"
							+ AppConstants.PL_CT_DESC+"<br>"
							+ AppConstants.PL_DA_DESC+"<br>"
							+ AppConstants.PL_DB_DESC+"<br>"
							);
				}
				doImpInputData(pDto,inputList);
				resultMsg.append("【"+inputSheet.getName()+"】产线，成功导入【"+inputList.size()+"】条<br>");
				inputList.clear();
			}
			
//		}catch(Exception e){
//			e.printStackTrace();
//			resultMsg.setLength(0);
//			resultMsg.append("导入失败，发生异常!"+e.getMessage());
//		}
		
		return resultMsg.toString();
		
	}
	
}
