package com.app.prediction.report.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.g4studio.common.web.BaseAction;
import org.g4studio.common.web.BaseActionForm;
import org.g4studio.core.json.JsonHelper;
import org.g4studio.core.metatype.Dto;
import org.g4studio.core.metatype.impl.BaseDto;
import org.g4studio.core.mvc.xstruts.action.ActionForm;
import org.g4studio.core.mvc.xstruts.action.ActionForward;
import org.g4studio.core.mvc.xstruts.action.ActionMapping;
import org.g4studio.core.util.G4Constants;
import org.g4studio.core.util.G4Utils;
import org.g4studio.core.web.report.excel.ExcelExporter;

import com.app.prediction.report.service.PlReportService;

/**
 * 产线报表Action
 * @see BaseAction
 */
public class PlReportAction extends BaseAction {

	private PlReportService plReportService = (PlReportService) getService("plReportService");

	/**
	 * 产线报表页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryPlReportViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("createPlReportView");
	}

	/**
	 * 产线分品种预测报表页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryPzReportViewInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("createPzReportView");
	}

	/**
	 * 查询产线预测报表数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryPlReportData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PlReport.findPlReportList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PlReport.findPlReportListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询产线分品种预测报表数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryPzReportData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List list = g4Reader.queryForPage("PzReport.findPzReportList", dto);
		Integer countInteger = (Integer) g4Reader.queryForObject("PzReport.findPzReportListCount", dto);
		String jsonString = JsonHelper.encodeList2PageJson(list, countInteger, G4Constants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	/**
	 * 生成产线预测报表
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward createPlReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		plReportService.doCreatePlReport(dto);
		setOkTipMsg("生成产线预测报表成功！", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 生成产线分品种预测报表
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward createPzReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm aForm = (BaseActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		plReportService.doCreatePzReport(dto);
		setOkTipMsg("生成产线分品种预测报表成功！", response);
		return mapping.findForward(null);
	}
	
	/**
	 * 报表导出
	 * 
	 * @param
	 * @return
	 */
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String predictionMonth = request.getParameter("pm");
		Dto inDto = new BaseDto();
		inDto.put("predictionMonth", predictionMonth);
		Dto parametersDto = new BaseDto();
		parametersDto.put("reportTitle", "彩涂板事业部"+predictionMonth+"综合效益预测");
		parametersDto.put("by", super.getSessionContainer(request).getUserInfo().getUsername());
		parametersDto.put("nowtime", G4Utils.getCurrentTime());
		List dataList = g4Reader.queryForList("PlReport.findPlReportList", inDto);
		ExcelExporter excelExporter = new ExcelExporter();
		excelExporter.setTemplatePath("/app/prediction/report/templet/plReport.xls");
		excelExporter.setData(parametersDto, dataList);
		excelExporter.setFilename(parametersDto.getAsString("reportTitle")+".xls");
		excelExporter.export(request, response);
		return mapping.findForward(null);
	}
	
	/**
	 * 分品种报表导出
	 * 
	 * @param
	 * @return
	 */
	public ActionForward exportPzExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String predictionMonth = request.getParameter("pm");
		Dto inDto = new BaseDto();
		inDto.put("predictionMonth", predictionMonth);
		Dto parametersDto = new BaseDto();
		parametersDto.put("reportTitle", "彩涂板事业部"+predictionMonth+"分品种效益预测");
		parametersDto.put("by", super.getSessionContainer(request).getUserInfo().getUsername());
		parametersDto.put("nowtime", G4Utils.getCurrentTime());
		List dataList = g4Reader.queryForList("PzReport.findPzReportList", inDto);
		ExcelExporter excelExporter = new ExcelExporter();
		excelExporter.setTemplatePath("/app/prediction/report/templet/pzReport.xls");
		excelExporter.setData(parametersDto, dataList);
		excelExporter.setFilename(parametersDto.getAsString("reportTitle")+".xls");
		excelExporter.export(request, response);
		return mapping.findForward(null);
	}
}
