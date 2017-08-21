package com.app.prediction.productline.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.g4studio.common.web.BaseAction;
import org.g4studio.common.web.BaseActionForm;
import org.g4studio.core.exception.NullAbleException;
import org.g4studio.core.json.JsonHelper;
import org.g4studio.core.metatype.Dto;
import org.g4studio.core.mvc.xstruts.action.ActionForm;
import org.g4studio.core.mvc.xstruts.action.ActionForward;
import org.g4studio.core.mvc.xstruts.action.ActionMapping;
import org.g4studio.core.mvc.xstruts.upload.FormFile;
import org.g4studio.core.util.G4Constants;

import com.app.prediction.productline.service.ProductLineService;

/**
 * 产线Action
 * @see BaseAction
 */
public class ProductLineAction extends BaseAction {

	private ProductLineService productLineService = (ProductLineService) getService("productLineService");

	/**
	 * 查询产线
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryProductLineViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("createProductLineView");
	}

	/**
	 * 查询产线数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryProductLineData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("ProductLine.findProductLineList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("ProductLine.findProductLineListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询产品数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryProductData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("Product.findProductList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("Product.findProductListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询消耗数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryInputData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("Input.findInputList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("Input.findInputListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	/**
	 * 生成产线
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward createProductLineData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		// 先判断预测月份是否已经生成产线，如果已经生成，不让创建产线
		List list = g4Reader.queryForPage("ProductLine.findProductLineList", dto);
		if(list.size()>0){
			setErrTipMsg("产线数据已经存在，不必重新生成！", response);
			return mapping.findForward(null);
		}
		productLineService.doCreateProductLines(dto);
		setOkTipMsg("生成产线成功！", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 导入产品数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importProductExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile1();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		productLineService.doImportProductExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}

	/**
	 * 导入消耗数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importInputExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile2();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		productLineService.doImportInputExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}

	/**
	 * 全量导入
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward impProductAndInputExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		// 先判断预测月份是否已经生成产线，如果已经生成，不让创建产线
		List list = g4Reader.queryForPage("ProductLine.findProductLineList", dto);
		if(list.size()<=0){
			setErrTipMsg("请先点击生成"+dto.getAsString("prediction_month")+"月产线数据！", response);
			return mapping.findForward(null);
			//productLineService.doCreateProductLines(dto);
		}
		// 产品及主投料
		FormFile product = actionForm.getFile3();
		// 其他消耗
		FormFile input = actionForm.getFile4();
		// 调用service
		String resultMsg ="";
		try{
			resultMsg = productLineService.doImpProductAndInputExcel(dto,product.getInputStream(),input.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
			resultMsg = "请检查导入模板格式是否正确！";
			setErrTipMsg(resultMsg, response);
			return mapping.findForward(null);
		}
		setOkTipMsg(resultMsg, response);
		return mapping.findForward(null);
	}
	
}
