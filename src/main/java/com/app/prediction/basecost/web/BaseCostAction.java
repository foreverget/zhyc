package com.app.prediction.basecost.web;

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

import com.app.prediction.basecost.service.BaseCostService;

/**
 * 基础费用Action
 * @see BaseAction
 */
public class BaseCostAction extends BaseAction {

	private BaseCostService baseCostService = (BaseCostService) getService("baseCostService");

	/**
	 * 工资及附加页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryGzViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("gzView");
	}

	/**
	 * 维修页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryWxViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("wxView");
	}

	/**
	 * 机物料页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryJwViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("jwView");
	}

	/**
	 * 包装费页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryBzViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("bzView");
	}

	/**
	 * 其他费用页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryQtViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("qtView");
	}

	/**
	 * 查询工资数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryGzData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PlGzCost.findPlGzCostList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PlGzCost.findPlGzCostListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询维修数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryWxData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PlWxCost.findPlWxCostList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PlWxCost.findPlWxCostListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询机物料数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryJwData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PlJwCost.findPlJwCostList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PlJwCost.findPlJwCostListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询包装数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryBzData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PlBzCost.findPlBzCostList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PlBzCost.findPlBzCostListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询其他数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryQtData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PlQtCost.findPlQtCostList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PlQtCost.findPlQtCostListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	/**
	 * 导入工资及附加数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importGzExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile1();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		baseCostService.doImportGzExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 导入维修数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importWxExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile1();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		baseCostService.doImportWxExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 导入机物料数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importJwExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile1();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		baseCostService.doImportJwExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 导入包装数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importBzExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile1();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		baseCostService.doImportBzExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 导入其他数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward importQtExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm actionForm = (BaseActionForm) form;
		Dto dto = actionForm.getParamAsDto(request);
		FormFile theFile = actionForm.getFile1();
		if(null==theFile){
			throw new NullAbleException();
		}
		// 调用service
		baseCostService.doImportQtExcel(dto,theFile.getInputStream());
		setOkTipMsg("导入成功", response);
		return mapping.findForward(null);
	}
	
}
