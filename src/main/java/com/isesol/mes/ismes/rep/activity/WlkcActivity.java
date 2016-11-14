
package com.isesol.mes.ismes.rep.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;
import com.isesol.ismes.platform.module.bean.File;

/**
 * Created on 2016年10月25日
 * <p>
 * Title: [报表生成]_[物料库存]
 * </p>
 * <p>
 * Describing: [查询物料在库情况以及出库定位跟踪]
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author {蔡鹤}
 * @email {caihe@neusoft.com}
 * @version 1.0
 */
public class WlkcActivity {
	/**
	 * 查询物料库存
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_wlkc(Parameters parameters, Bundle bundle) {
		return "rep_wlkc";
	}

	/**
	 * 查询物料库存
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public String table_wlkc(Parameters parameters, Bundle bundle) throws Exception {
		// 查询生产任务
		String query_wlbh = parameters.getString("query_wlbh");// ---wlb
		String query_wlmc = parameters.getString("query_wlmc");// ---wlb
		String query_wllb = parameters.getString("query_wllb");// ---wlb
		String query_kwid = parameters.getString("query_kwid");// ---sjwlkc
		String query_kfid = parameters.getString("query_kfid");// ---sjwlkc
		String query_sbwz = parameters.getString("query_sbwz");// ---sbxxb
		String query_jsz = parameters.getString("query_jsz");// ---mf_gzsdjs
		String query_kczt = parameters.getString("query_kczt");
		String query_jssjstart = parameters.getString("query_jssjstart");// ---mf_gzsdjs
		String query_jssjend = parameters.getString("query_jssjend");// ---mf_gzsdjs
		parameters.set("kwid", query_kwid == null ? "" : query_kwid);
		parameters.set("kfid", query_kfid == null ? "" : query_kfid);
		parameters.set("wllb", query_wllb == null ? "" : query_wllb);
		parameters.set("wlmc", query_wlmc == null ? "" : query_wlmc);
		parameters.set("wlbh", query_wlbh == null ? "" : query_wlbh);
		parameters.set("sbwz", query_sbwz == null ? "" : query_sbwz);
		parameters.set("jsz", query_jsz == null ? "" : query_jsz);
		parameters.set("kczt", query_kczt == null ? "" : query_kczt);
		parameters.set("jssjstart", query_jssjstart == null ? "" : query_jssjstart);
		parameters.set("jssjend", query_jssjend == null ? "" : query_jssjend);
		List<Map<String, Object>> wlxx = new ArrayList();
		List<Map<String, Object>> gzxx = new ArrayList();
		List<Map<String, Object>> gzxx_list = new ArrayList();
		List<Map<String, Object>> sbxx = new ArrayList();
		List<Map<String, Object>> kcxx = new ArrayList();
		List<Map<String, Object>> kcxx_gz = new ArrayList();
		List<Map<String, Object>> kfxx_kfmc = new ArrayList();
		List<Map<String, Object>> kwxx_kwmc = new ArrayList();
		// 判断并查询物料编号、名称、类别等信息by物料信息表
		// if(!"".equals(parameters.get("wlbh"))||!"".equals(parameters.get("wlmc"))||!"".equals(parameters.get("wllb"))){
		// 查询物料编号
		Bundle b_wlxxbh = Sys.callModuleService("mm", "mmservice_wlxx_new", parameters);
		wlxx = (List<Map<String, Object>>) b_wlxxbh.get("wlxx");
		 if (wlxx.size()<=0) {
			 return "json:";
		 }
		if (wlxx.size() > 0) {
			String val_tm = "(";
			for (int i = 0; i < wlxx.size(); i++) {
				if (i != 0) {
					val_tm = val_tm + ",";
				}

				val_tm += "'" + wlxx.get(i).get("wltm") + "'";
			}
			val_tm = val_tm + ")";

			parameters.set("val_tm", val_tm);
		}
		if (wlxx.size() > 0) {
			String val_wlid = "(";
			for (int i = 0; i < wlxx.size(); i++) {
				if (i != 0) {
					val_wlid = val_wlid + ",";
				}

				val_wlid += "'" + wlxx.get(i).get("wlid") + "'";
			}
			val_wlid = val_wlid + ")";

			parameters.set("val_wlid", val_wlid);
		}
		// }

		// 判断并查询库房、库位信息by物料库存表
		// if(!"".equals(parameters.get("kfid"))||!"".equals(parameters.get("kwid"))){
		if ("".equals(parameters.get("wlbh"))  && "".equals(parameters.get("wllb"))
				&& "".equals(parameters.get("kfid"))
				&& "".equals(parameters.get("kwid"))
				&& (!"".equals(parameters.get("jsz")) || !"".equals(parameters.get("jssjstart"))
						|| !"".equals(parameters.get("sbwz")) || !"".equals(parameters.get("jssjend")))) {
		} else {
			// 查询库存信息
			Bundle b_kcxx = Sys.callModuleService("wm", "wmService_query_kcxx", parameters);
			kcxx = (List<Map<String, Object>>) b_kcxx.get("kcxx");
			//-------将工装库存数据拼接到库存数据里start-----------
			// 查询库存信息_工装
			Bundle b_kcxx_gz = Sys.callModuleService("wm", "wmService_query_kcxx_gz", parameters);
			kcxx_gz = (List<Map<String, Object>>) b_kcxx_gz.get("kcxx");
			// 将库存数据与工装表数据拼接一起
			int sizeall = kcxx.size() + kcxx_gz.size();
			int size_kc = kcxx.size();
			int size_gz = kcxx_gz.size();
			int j = 0;
			for (int i = size_kc; i < sizeall; i++) {
				if (null != kcxx_gz.get(j)) {
					Map map = new HashMap();
					for (; j < size_gz;) {
//						map.put("wllb", kcxx_gz.get(j).get("wllb"));
//						map.put("jssj", kcxx_gz.get(j).get("jssj"));
//						map.put("jsz", kcxx_gz.get(j).get("jsz"));
//						map.put("wlmc", kcxx_gz.get(j).get("wlmc"));
//						map.put("wlbh", kcxx_gz.get(j).get("wlbh"));
//						map.put("sysm", kcxx_gz.get(j).get("sysm"));
//						map.put("sbwz", kcxx_gz.get(j).get("sbwz"));
//						map.put("kczt", "1");
						map = kcxx_gz.get(j);
						break;
					}
					j++;
					kcxx.add(i, map);
				}
			}
			//-----------end---------------
			if (kcxx.size() > 0) {
				// 将库存表初始数据状态字段标记为0--未发货
				for (int i = 0; i < kcxx.size(); i++) {
					kcxx.get(i).put("kczt", "0");
				}
			}
		}
		

		// if (gzxx_list.size()<=0) {
		// return "json:";
		// }
		// }
		// 判断并查询领取人、领取时间等信息by工装表
		// if(!"".equals(parameters.get("jsz"))||!"".equals(parameters.get("jssjstart"))||!"".equals(parameters.get("jssjend"))){
		// 查询工装送达信息
		Bundle b_gzxx = Sys.callModuleService("mf", "mfService_query_gzxx_by_wltm", parameters);
		gzxx = (List<Map<String, Object>>) b_gzxx.get("gzxx");
		if (gzxx.size() > 0) {
			String val_tm1 = "(";
			for (int i = 0; i < gzxx.size(); i++) {
				if (i != 0) {
					val_tm1 = val_tm1 + ",";
				}

				val_tm1 += "'" + gzxx.get(i).get("wltm") + "'";
			}
			val_tm1 = val_tm1 + ")";
			parameters.set("val_tm", val_tm1);
		}
		// }
		// 判断并查询设备位置信息by设备信息表
		// if(!"".equals(parameters.get("sbwz"))){
		// 查询设备表信息
		Bundle b_sbxx = Sys.callModuleService("em", "emservice_query_sbxx_by_sbbh", parameters);
		sbxx = (List<Map<String, Object>>) b_sbxx.get("sbxx");
		if (sbxx.size() > 0) {
			String val_sbbh = "(";
			for (int i = 0; i < sbxx.size(); i++) {
				if (i != 0) {
					val_sbbh = val_sbbh + ",";
				}
				val_sbbh += "'" + sbxx.get(i).get("sbbh") + "'";
			}
			val_sbbh = val_sbbh + ")";
			parameters.set("val_sbbh", val_sbbh);
		}
		Bundle b_gzxx1 = Sys.callModuleService("mf", "mfService_query_gzxx_by_sbbh", parameters);
		List<Map<String, Object>> gzxx_tm = (List<Map<String, Object>>) b_gzxx1.get("gzxx");
		if (gzxx_tm.size() > 0) {
			String val_tm2 = "(";
			for (int i = 0; i < gzxx_tm.size(); i++) {
				if (i != 0) {
					val_tm2 = val_tm2 + ",";
				}
				val_tm2 += "'" + gzxx_tm.get(i).get("wltm") + "'";
			}
			val_tm2 = val_tm2 + ")";
			parameters.set("val_tm", val_tm2);
		}
		// }
		// 再次将收集到的查询条件整合并查询接收送达表数据
		Bundle b_gzxx_list = Sys.callModuleService("mf", "mfService_query_gzxx_by_wltm", parameters);
		gzxx_list = (List<Map<String, Object>>) b_gzxx_list.get("gzxx");
		// 通过库存物料条码获取库房id
		String val_kfid = "(";
		for (int i = 0; i < kcxx.size(); i++) {
			if (i != 0) {
				val_kfid = val_kfid + ",";
			}
			val_kfid += "'" + kcxx.get(i).get("kfid") + "'";
		}
		val_kfid = val_kfid + ")";
		parameters.set("val_kfid", val_kfid);
		// 查询库位信息
		Bundle b_kfxxbh = Sys.callModuleService("wm", "wmService_query_kfxx_by_kfid", parameters);
		if (null != b_kfxxbh) {
			kfxx_kfmc = (List<Map<String, Object>>) b_kfxxbh.get("kfxx");
		}
		// 通过库存物料条码获取库房id
		String val_kwid = "(";
		for (int i = 0; i < kcxx.size(); i++) {
			if (i != 0) {
				val_kwid = val_kwid + ",";
			}
			val_kwid += "'" + kcxx.get(i).get("kwid") + "'";
		}
		val_kwid = val_kwid + ")";
		parameters.set("val_kwid", val_kwid);

		// 查询库位信息
		Bundle b_kwxxbh = Sys.callModuleService("wm", "wmService_query_kwxx_by_kwid", parameters);
		if (null != b_kwxxbh) {
			kwxx_kwmc = (List<Map<String, Object>>) b_kwxxbh.get("kwxx");
		}
		// ----------第二次更改------------

		// 通过遍历循环将设备信息插入工装表中
		for (int i = 0; i < kcxx.size(); i++) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (kcxx.get(i).get("wltm").equals(wlxx.get(j).get("wltm"))) {
					kcxx.get(i).put("wlmc", wlxx.get(j).get("wlmc"));// 物料名称
					kcxx.get(i).put("wllb", wlxx.get(j).get("wllbdm"));// 物料类别
					kcxx.get(i).put("wlbh", wlxx.get(j).get("wlbh"));// 物料编号
				}
			}
			for (int j = 0; j < wlxx.size(); j++) {
				if (kcxx.get(i).get("wlid").equals(wlxx.get(j).get("wlid"))) {
					kcxx.get(i).put("wlmc", wlxx.get(j).get("wlmc"));// 物料名称
					kcxx.get(i).put("wllb", wlxx.get(j).get("wllbdm"));// 物料类别
					kcxx.get(i).put("wlbh", wlxx.get(j).get("wlbh"));// 物料编号
				}
			}
		}

		// ------------end---------------
		// 通过遍历循环将物料信息插入工装表中
		for (int i = 0; i < gzxx.size(); i++) {
			for (int j = 0; j < sbxx.size(); j++) {
				if (gzxx.get(i).get("sbbh").equals(sbxx.get(j).get("sbbh"))) {
					gzxx.get(i).put("sbwz", sbxx.get(j).get("sbwz"));// 设备位置
				}
			}
		}
		// 通过遍历循环将物料信息插入工装表中
		for (int i = 0; i < gzxx_list.size(); i++) {
			for (int j = 0; j < sbxx.size(); j++) {
				if (gzxx_list.get(i).get("sbbh").equals(sbxx.get(j).get("sbbh"))) {
					gzxx_list.get(i).put("sbwz", sbxx.get(j).get("sbwz"));// 设备位置
				}
			}
		}
		// 通过遍历循环将设备信息插入工装表中
		for (int i = 0; i < gzxx.size(); i++) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (gzxx.get(i).get("gzbh").equals(wlxx.get(j).get("wlbh"))) {
					gzxx.get(i).put("wlmc", wlxx.get(j).get("wlmc"));// 物料名称
					gzxx.get(i).put("sysm", wlxx.get(j).get("sysm"));// 有效期
				}
			}
		}
		// 通过遍历循环插入外键数据
		for (int i = 0; i < kcxx.size(); i++) {
			// for (int j = 0; j < gzxx.size(); j++) {
			// if (kcxx.get(i).get("wltm").equals(gzxx.get(j).get("wltm"))) {
			// kcxx.get(i).put("wllb", gzxx.get(j).get("wllb"));// 物料大类
			// kcxx.get(i).put("jssj", gzxx.get(j).get("jssj"));// 领用时间
			// kcxx.get(i).put("jsz", gzxx.get(j).get("jsz"));// 领用人
			// kcxx.get(i).put("wlmc", gzxx.get(j).get("wlmc"));// 物料名称
			// kcxx.get(i).put("sysm", gzxx.get(j).get("sysm"));// 有效期
			// kcxx.get(i).put("sbwz", gzxx.get(j).get("sbwz"));// 设备位置
			// kcxx.get(i).put("kczt", "0");// 未发货
			// break;
			// }
			// }
			for (int k = 0; k < wlxx.size(); k++) {
				if (kcxx.get(i).get("wltm").equals(wlxx.get(k).get("wltm"))) {
					kcxx.get(i).put("wlbh", wlxx.get(k).get("wlbh"));// 物料编号
					kcxx.get(i).put("wllb", wlxx.get(k).get("wllbdm"));// 物料编号
				}
			}
			if (null != kfxx_kfmc) {
				for (int l = 0; l < kfxx_kfmc.size(); l++) {
					if (kcxx.get(i).get("kfid").equals(kfxx_kfmc.get(l).get("kfid"))) {
						kcxx.get(i).put("kfmc", kfxx_kfmc.get(l).get("kfmc"));// 库房名称
					}
				}
			}
			if (null != kwxx_kwmc) {
				for (int m = 0; m < kwxx_kwmc.size(); m++) {
					if (kcxx.get(i).get("kwid").equals(kwxx_kwmc.get(m).get("kwid"))) {
						kcxx.get(i).put("kwmc", kwxx_kwmc.get(m).get("kwmc"));// 库位名称
					}
				}
			}
		}

		// 将库存表初始数据状态字段标记为1--已发货
		for (int i = 0; i < gzxx_list.size(); i++) {
			for (int k = 0; k < wlxx.size(); k++) {
				if (gzxx_list.get(i).get("wltm").equals(wlxx.get(k).get("wltm"))) {
					gzxx_list.get(i).put("wlbh", wlxx.get(k).get("wlbh"));// 物料编号
					gzxx_list.get(i).put("wlmc", wlxx.get(k).get("wlmc"));// 物料名称
					gzxx_list.get(i).put("sysm", wlxx.get(k).get("sysm"));// 使用寿命
				}
			}
			gzxx_list.get(i).put("kczt", "1");

		}

		// 将库存数据与接受表数据拼接一起
		int sizeall = kcxx.size() + gzxx_list.size();
		int size_kc = kcxx.size();
		int size_gz = gzxx_list.size();
		int j = 0;
		for (int i = size_kc; i < sizeall; i++) {
			if (null != gzxx_list.get(j)) {
				Map map = new HashMap();
				for (; j < size_gz;) {
					map.put("wllb", gzxx_list.get(j).get("wllb"));
					map.put("jssj", gzxx_list.get(j).get("jssj"));
					map.put("jsz", gzxx_list.get(j).get("jsz"));
					map.put("wlmc", gzxx_list.get(j).get("wlmc"));
					map.put("wlbh", gzxx_list.get(j).get("wlbh"));
					map.put("sysm", gzxx_list.get(j).get("sysm"));
					map.put("sbwz", gzxx_list.get(j).get("sbwz"));
					map.put("kczt", "1");
					break;
				}
				j++;
				kcxx.add(i, map);
			}
		}
		//判断库存状态并进行筛选
		if(!"".equals(parameters.get("kczt"))&&null!=parameters.get("kczt")&&"1".equals(parameters.get("kczt"))){
			List<Integer> list = new ArrayList();
			for(int i=0;i<kcxx.size();i++){
				if(null!=kcxx.get(i).get("kczt")&&!"".equals(kcxx.get(i).get("kczt"))){
					if(kcxx.get(i).get("kczt").equals("0")){//移除状态为出库的条目
						list.add(i);
					}
				}
			}
			for(int i=list.size()-1;i>=0;i--){
				kcxx.remove((int)list.get(i));
			}
		}
		if(!"".equals(parameters.get("kczt"))&&null!=parameters.get("kczt")&&"0".equals(parameters.get("kczt"))){
			List<Integer> list = new ArrayList();
			for(int i=0;i<kcxx.size();i++){
				if(null!=kcxx.get(i).get("kczt")&&!"".equals(kcxx.get(i).get("kczt"))){
					if(kcxx.get(i).get("kczt").equals("1")){//移除状态为出库的条目
						list.add(i);
					}
				}
			}
			for(int i=list.size()-1;i>=0;i--){
				kcxx.remove((int)list.get(i));
			}
		}
		// 如果export_flag为1则进行导出操作，否则就进行查询
		if (null != parameters.get("export_flag") && !"".equals(parameters.get("export_flag"))) {
			bundle.put("rows", kcxx);
			bundle.put("totalRecord", kcxx.size());
			int totalPage1 = kcxx.size() % kcxx.size() == 0 ? kcxx.size() / kcxx.size() : kcxx.size() / kcxx.size() + 1;
			bundle.put("totalPage", totalPage1);
			bundle.put("currentPage", 1);
			return exportFile(parameters, bundle, kcxx);
		} else {
			int page = Integer.parseInt(parameters.get("page").toString());
			int pageSize = Integer.parseInt(parameters.get("pageSize").toString());//每页显示几条
			List<Map<String, Object>> listIndex = new ArrayList();
			int index=0;
			for(int i=(page-1)*pageSize;i<pageSize*page;i++){
					if(i<kcxx.size()){
						listIndex.add(index, kcxx.get(i));
						index++;
					}else{
						break;
					}
			}
			int totalPage = kcxx.size()%pageSize==0?kcxx.size()/pageSize:kcxx.size()/pageSize+1;
			bundle.put("rows", listIndex);
			bundle.put("totalPage", totalPage);//共几页
			bundle.put("currentPage", page);//第几页
			bundle.put("totalRecord", kcxx.size());
			return "json:";
		}
	}

	/**
	 * 选择库房
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kfSelect(Parameters parameters, Bundle bundle) {
		// Dataset datasetkfgl = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz,
		// zzjgid, qybsdm ", "qybsdm = '10' " , " kfid desc ",new Object[] {});
		Bundle b_kfxx = Sys.callModuleService("wm", "wmService_query_kfxx_by_kfmc", parameters);
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> kfgl = (List<Map<String, Object>>) b_kfxx.get("kfxx");
		for (Map<String, Object> map : kfgl) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("label", map.get("kfmc"));
			m.put("value", map.get("kfid"));
			returnList.add(m);
		}
		bundle.put("select_kf", returnList.toArray());
		return "json:select_kf";
	}

	/**
	 * 选择库位
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kwSelect(Parameters parameters, Bundle bundle) {
		String parentId = parameters.getString("parent");
		if (StringUtils.isNotBlank(parentId)) {
			// Dataset datasetkfgl = Sys.query("wm_kwxxb", " kwid, kwbh, kwmc,
			// kwwz, kfid, qybsdm ",
			// "qybsdm = '10' and kfid=?", " kfid desc ", new Object[] {
			// parentId });
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

	/**
	 * excel导出
	 */
	public String exportFile(Parameters parameters, Bundle bundle, List<Map<String, Object>> list) {
		InputStream is = null;
		Map setting = new HashMap();
		setting.put("cellTitle", "物料编号,物料名称,物料类别,库房,库位,设备位置,接收时间,领用人,使用寿命,状态");
		setting.put("cellIndex", "wlbh,wlmc,wllb,kfmc,kwmc,sbwz,jssj,jsz,sysm,kczt");
		for (int i = 0; i < list.size(); i++) {
			list.get(i).remove("czsj");
			list.get(i).remove("wlgg");
			list.get(i).remove("kcsl");
			list.get(i).remove("kfid");
			list.get(i).remove("wlid");
			list.get(i).remove("mplh");
			list.get(i).remove("sjwlkcid");
			list.get(i).remove("kwid");
			list.get(i).remove("wltm");
			list.get(i).remove("pcid");

			if (null != list.get(i).get("wllb")) {
				if (list.get(i).get("wllb").equals("10")) {
					list.get(i).put("wllb", "刀体");
				}
				if (list.get(i).get("wllb").equals("20")) {
					list.get(i).put("wllb", "夹具");
				}
				if (list.get(i).get("wllb").equals("30")) {
					list.get(i).put("wllb", "量具");
				}
				if (list.get(i).get("wllb").equals("40")) {
					list.get(i).put("wllb", "毛坯");
				}
				if (list.get(i).get("wllb").equals("50")) {
					list.get(i).put("wllb", "原材料");
				}
				if (list.get(i).get("wllb").equals("60")) {
					list.get(i).put("wllb", "刀片");
				}
				if (list.get(i).get("wllb").equals("70")) {
					list.get(i).put("wllb", "半成品");
				}
			}
			if (null != list.get(i).get("kczt")) {
				if (list.get(i).get("kczt").equals("0")) {
					list.get(i).put("kczt", "未发货");
				}
				if (list.get(i).get("kczt").equals("1")) {
					list.get(i).put("kczt", "已发货");
				}
			}

			if (null == list.get(i).get("kfmc")) {
				list.get(i).put("kfmc", "");
			}
			if (null == list.get(i).get("kwmc")) {
				list.get(i).put("kwmc", "");
			}
			if (null == list.get(i).get("wlbh")) {
				list.get(i).put("wlbh", "");
			}
			if (null == list.get(i).get("wlmc")) {
				list.get(i).put("wlmc", "");
			}
			if (null == list.get(i).get("wllb")) {
				list.get(i).put("wllb", "");
			}
			if (null == list.get(i).get("sbwz")) {
				list.get(i).put("sbwz", "");
			}
			if (null == list.get(i).get("jssj")) {
				list.get(i).put("jssj", "");
			}
			if (null == list.get(i).get("jsz")) {
				list.get(i).put("jsz", "");
			}
			if (null == list.get(i).get("sysm")) {
				list.get(i).put("sysm", "");
			}
			if (null == list.get(i).get("kczt")) {
				list.get(i).put("kczt", "");
			}
		}
		InputStream result = Sys.exportFile("xlsx", is, list, setting);
		File file = null;
		try {
			file = new File("工装库存查询.xlsx", null, result, "xlsx", Long.valueOf(String.valueOf(result.available())));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			throw new NumberFormatException();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		bundle.put("data", file);
		return "file:data";
	}
}
