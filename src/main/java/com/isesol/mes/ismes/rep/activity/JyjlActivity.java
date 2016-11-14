
package com.isesol.mes.ismes.rep.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

/**
 * Created on 2016年10月31日
 * <p>Title: [报表生成]_[检验记录]</p>
 * <p>Describing: [查询检验记录及详细信息]</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * 
 * @author {mawenwen}
 * @email {maww@neusoft.com}
 * @version 1.0
 */
public class JyjlActivity {
	/**
	 * 查询检验记录信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_jyjl(Parameters parameters, Bundle bundle) {
		
		return "rep_jyjl";
	}
	
	/**查询物料库存
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public String table_jyjl(Parameters parameters, Bundle bundle) throws Exception {
		//查询条件
		
		String query_xh = parameters.getString("query_xh");//---箱号
		String query_zjbz = parameters.getString("query_zjbz");//---质检类型
		String query_zjjgbzw = parameters.getString("query_zjjgbzw");//---质检结果
		
		parameters.set("xh", (null==query_xh || "".equals(query_xh.trim()))?"":query_xh);
		parameters.set("zjbz", null==query_zjbz?"":query_zjbz);
		parameters.set("zjjgbzw", null==query_zjjgbzw?"":query_zjjgbzw);
		
		List<Map<String, Object>> zjjgzb = new ArrayList();
		List<Map<String, Object>> ljglb = new ArrayList();
		List<Map<String, Object>> scrw = new ArrayList();
		
		//查询质检主表“pl_zjjgzb”
		Bundle b_zjjgzb = Sys.callModuleService("pl", "plservice_query_zjjgzb", parameters);
		if(null != b_zjjgzb){
			zjjgzb = (List<Map<String, Object>>) b_zjjgzb.get("zjjgzb");	
		}
		
		//查询零件表“pm_ljglb”
		Bundle b_ljglb = Sys.callModuleService("pm", "pmservice_query_ljglb", parameters);
		if (null != b_ljglb){
			ljglb = (List<Map<String, Object>>) b_ljglb.get("ljxx");	
		}
		
		//查询生产任务表“sc_scrw”
		Bundle b_scrw = Sys.callModuleService("pro", "proService_query_scrw", parameters);
		if (null != b_scrw){
			scrw = (List<Map<String, Object>>) b_scrw.get("scrw");	
		}
		
		for (Map m : zjjgzb){
			for (Map mlj : ljglb){
				if (((Integer)mlj.get("ljid")).intValue()==((Integer)m.get("ljid")).intValue()){
					m.put("ljbh", (String)mlj.get("ljbh"));
				}
			}
			
			for (Map mscrw : scrw){
				if (((Integer)mscrw.get("scrwid")).intValue()==((Integer)m.get("scrwid")).intValue()){
					m.put("scph", (String)mscrw.get("scph"));
				}
			}
			
		}
		
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		bundle.put("rows", zjjgzb);
		int totalPage = zjjgzb.size()%pageSize==0?zjjgzb.size()/pageSize:zjjgzb.size()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", zjjgzb.size());
		return "json:";
	}
	
	/**设置明细信息查询条件
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
//	@SuppressWarnings("unchecked")
//	public String detail_show_con_back(Parameters parameters, Bundle bundle) throws Exception {
//
//		String query_jyxm_detail = parameters.getString("query_jyxm_detail");//---检验项目：游标卡尺、千分尺。。。。
//		String query_gyyq_detail = parameters.getString("query_gyyq_detail");//---工艺要求
//		String query_yxsx_detail = parameters.getString("query_yxsx_detail");//---允许上限
//		String query_yxxx_detail = parameters.getString("query_yxxx_detail");//---允许下线
//		Map<String, Object> detail_map = new HashMap<String, Object>();;
//		
//		detail_map.put("query_jyxm_detail", query_jyxm_detail);
//		detail_map.put("query_gyyq_detail", query_gyyq_detail);
//		detail_map.put("query_yxsx_detail", query_yxsx_detail);
//		detail_map.put("query_yxxx_detail", query_yxxx_detail);
//		bundle.put("detail_map", detail_map);
//		
//		return "json:detail_map";
//	}
	
	/**查询质检结果明细信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public String table_jyjl_detail(Parameters parameters, Bundle bundle) throws Exception {
		if (null == parameters.get("zjjgzbid")){
			return "json:";
		}
		int zjjgzbid = 0;
		if(null != parameters.get("zjjgzbid") && !"".equals(parameters.get("zjjgzbid").toString())){
			zjjgzbid = Integer.parseInt(parameters.get("zjjgzbid").toString());
		}
		
		/*
		 * 1、获取页面传递参数 “零件ID、工序组ID”	
		 * 2、通过"零件ID、工序组ID"查询零件工序组的质检项
		 * 3、通过质检结果主表ID、质检项ID查询明细信息 
		 */
		int ljid = 0;
		int gxzid = 0;
		if(null != parameters.get("ljid") && !"".equals(parameters.get("ljid").toString())){
			ljid = Integer.parseInt(parameters.get("ljid").toString());
		}
		if(null != parameters.get("gxzid") && !"".equals(parameters.get("gxzid").toString())){
			gxzid = Integer.parseInt(parameters.get("gxzid").toString());
		}
		
		if(null != parameters.get("query_jyxm_detail") && !"".equals(parameters.get("query_jyxm_detail").toString().trim())){
			gxzid = Integer.parseInt(parameters.get("gxzid").toString());
		}
	
		//质检项查询条件数据准备
		parameters.set("zjbzlx", parameters.get("query_jyxm").toString().trim());
		parameters.set("gyyq", parameters.get("query_gyyq").toString().trim());
		parameters.set("yxsx", parameters.get("query_yxsx").toString().trim());
		parameters.set("yxxx", parameters.get("query_yxxx").toString().trim());
		
		
		//查询质检主表“pl_zjjgzb”
		parameters.set("ljid", ljid);
		parameters.set("gxzid", gxzid);
		Bundle b_zjx = Sys.callModuleService("pm", "pmservice_query_zjxbycon", parameters);
		List<Map<String, Object>> zjx = new ArrayList();
		//查询条件（质检结果明细表的质检项集合）
		String condition = "";
		if (null != b_zjx){
			zjx = (List<Map<String, Object>>) b_zjx.get("zjx");
			//取出质检项ID的集合
			
			for (Map m : zjx){
				condition = condition + Integer.toString((Integer)m.get("gxzjxlrid")) + ",";
			}
			
			if (condition.length() > 0){
				condition = condition.substring(0, condition.length()-1);
			}
		}
		
		//查询质检结果明细信息表
		parameters.set("zjxidset", condition);
		Bundle b_zjjgdetail = Sys.callModuleService("pl", "plservice_query_zjjgdetail", parameters);
		List<Map<String, Object>> zjjgdetail = new ArrayList();
		if (null != b_zjjgdetail){
			zjjgdetail = (List<Map<String, Object>>) b_zjjgdetail.get("zjjgdetail");
			for (Map m1 : zjjgdetail){
				for (Map m2 : zjx){
					if (((Integer)m2.get("gxzjxlrid")).intValue() == ((Integer)m1.get("gxzjxlrid")).intValue()){
						m1.put("gxzjxlrmc", (String)m2.get("zjbzlx"));
						m1.put("gyyq", (String)m2.get("gyyq"));
					}
				}
				
			}
			
		}
		
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		bundle.put("rows", zjjgdetail);
		int totalPage = zjjgdetail.size()%pageSize==0?zjjgdetail.size()/pageSize:zjjgdetail.size()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", zjjgdetail.size());
		return "json:";
	}
	
	/**
	 * 查询检验项目
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String jyxmSelect(Parameters parameters,Bundle bundle){
//		Dataset datasetkfgl = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz, zzjgid, qybsdm ", "qybsdm = '10' " , " kfid desc ",new Object[] {});
		Bundle b_zjxm = Sys.callModuleService("pm", "pmservice_searchZjbzlx", parameters);
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> kfgl = (List<Map<String, Object>>) b_zjxm.get("zjxm");
		for(Map<String,Object> map : kfgl){
			Map<String,Object> m = new HashMap<String, Object>();
			//查找的质检项目对象中有四类信息“label”、“title”、“pinyin1”、“pinyin2”
			m.put("label", map.get("label"));
			m.put("value", map.get("label"));
			returnList.add(m);
		}
		bundle.put("select_zjxm", returnList.toArray());
		return "json:select_zjxm";
	}
	/**
	 * 选择库房
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kfSelect(Parameters parameters,Bundle bundle){
//		Dataset datasetkfgl = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz, zzjgid, qybsdm ", "qybsdm = '10' " , " kfid desc ",new Object[] {});
		Bundle b_kfxx = Sys.callModuleService("wm", "wmService_query_kfxx_by_kfmc", parameters);
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> kfgl = (List<Map<String, Object>>) b_kfxx.get("kfxx");
		for(Map<String,Object> map : kfgl){
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("label", map.get("kfmc"));
			m.put("value", map.get("kfid"));
			returnList.add(m);
		}
		bundle.put("select_kf", returnList.toArray());
		return "json:select_kf";
	}
	/**
	 * 选择库位
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kwSelect(Parameters parameters, Bundle bundle) {
		String parentId = parameters.getString("parent");
		if (StringUtils.isNotBlank(parentId)) {
//			Dataset datasetkfgl = Sys.query("wm_kwxxb", " kwid, kwbh, kwmc, kwwz, kfid, qybsdm ",
//					"qybsdm = '10' and kfid=?", " kfid desc ", new Object[] { parentId });
			Bundle b_kwxx = Sys.callModuleService("wm", "wmService_query_kwxx_by_kwmc", parameters);
			List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> kfgl = (List<Map<String, Object>>) b_kwxx.get("kwxx");
			for (Map<String, Object> map : kfgl) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("label", map.get("kwmc"));
				m.put("value", map.get("kwid"));
				returnList.add(m);
			}
			bundle.put("select_kw", returnList.toArray());
		}
		return "json:select_kw";
	}
}
