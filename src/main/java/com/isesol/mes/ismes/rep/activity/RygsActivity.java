
package com.isesol.mes.ismes.rep.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;
import com.isesol.ismes.platform.module.bean.File;

/**
 * Created on 2016年11月7日
 * <p>
 * Title: [报表生成]_[人员工时]
 * </p>
 * <p>
 * Describing: [查询人员工作工时]
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author {蔡鹤}
 * @email {caihe@neusoft.com}
 * @version 1.0
 */
public class RygsActivity {
	/**
	 * 查询人员工时
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_rygs(Parameters parameters, Bundle bundle) {
		return "rep_rygs";
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
	public String table_rygs(Parameters parameters, Bundle bundle) throws Exception {
		String query_rqstart = parameters.getString("query_rqstart");// --日期---pl_pl_gdb
		String query_rqend = parameters.getString("query_rqend");// --日期---pl_pl_gdb
		String query_yggh = parameters.getString("query_yggh");// --员工工号--user_wh_ryjbxx
		String query_wlbh = parameters.getString("query_wlbh");// --零件图号---mm_mm_wlb
		String query_scph = parameters.getString("query_scph");// --生产批号---pl_pl_gdb
		parameters.set("rqstart", query_rqstart == null ? "" : query_rqstart);
		parameters.set("rqend", query_rqend == null ? "" : query_rqend);
		parameters.set("yggh", query_yggh == null ? "" : query_yggh);
		parameters.set("ljbh", query_wlbh == null ? "" : query_wlbh);
		parameters.set("scph", query_scph == null ? "" : query_scph);

		List<Map<String, Object>> gdb_list = new ArrayList();
		List<Map<String, Object>> ry_list = new ArrayList();
		List<Map<String, Object>> wlb_list = new ArrayList();
		List<Map<String, Object>> gxz_list = new ArrayList();
		List<Map<String, Object>> jgdy_list = new ArrayList();
		// if ("".equals(parameters.get("wlbh"))){
		//
		// }else{
		Bundle b_wlxx = Sys.callModuleService("pm", "pmservice_query_ljxxByljbh", parameters);
		if (null != b_wlxx) {
			wlb_list = (List<Map<String, Object>>) b_wlxx.get("ljxx");
		}
		if (null != wlb_list) {
			if (wlb_list.size() > 0) {
				String val_ljid = "(";
				for (int i = 0; i < wlb_list.size(); i++) {
					if (i != 0) {
						val_ljid = val_ljid + ",";
					}
					val_ljid += "'" + wlb_list.get(i).get("ljid") + "'";
				}
				val_ljid = val_ljid + ")";
				parameters.set("val_ljid", val_ljid);
			}
		}
		// }
		// if ("".equals(parameters.get("yggh"))){
		//
		// }else{

		Bundle b_user = Sys.callModuleService("user", "userInfoByYggh", parameters);
		if (null != b_user) {
			ry_list = (List<Map<String, Object>>) b_user.get("ryxx");
		}
		if (null != ry_list) {
			if (ry_list.size() > 0) {
				String val_ryid = "(";
				for (int i = 0; i < ry_list.size(); i++) {
					if (i != 0) {
						val_ryid = val_ryid + ",";
					}
					val_ryid += "'" + ry_list.get(i).get("yggh") + "'";
				}
				val_ryid = val_ryid + ")";
				parameters.set("val_czry", val_ryid);
			}
		}

		// }

		Bundle b_gxzxx = Sys.callModuleService("pm", "queryGxzxxByGxid_new1", parameters);
		if (null != b_gxzxx) {
			gxz_list = (List<Map<String, Object>>) b_gxzxx.get("gxxx");
		}

		Bundle b_jgdy = Sys.callModuleService("em", "emservice_query_jgdyb", parameters);
		if (null != b_jgdy) {
			jgdy_list = (List<Map<String, Object>>) b_jgdy.get("jgdy");
		}

		Bundle b_gdb = Sys.callModuleService("pl", "plservice_query_GdxxByScph", parameters);
		if (null != b_gdb) {
			gdb_list = (List<Map<String, Object>>) b_gdb.get("gdxx");
		}

		if (null != gdb_list) {

			for (int i = 0; i < gdb_list.size(); i++) {
				for (int j = 0; j < ry_list.size(); j++) {
					if (gdb_list.get(i).get("czry").equals(ry_list.get(j).get("yggh"))) {
						gdb_list.get(i).put("yggh", ry_list.get(j).get("yggh"));// 员工工号
						gdb_list.get(i).put("xm", ry_list.get(j).get("xm"));// 员工姓名
						gdb_list.get(i).put("pbgs", ry_list.get(j).get("gs"));// 排班工时
					}
				}

				for (int j = 0; j < wlb_list.size(); j++) {
					if (gdb_list.get(i).get("ljid").equals(wlb_list.get(j).get("wlid"))) {
						gdb_list.get(i).put("wlgg", wlb_list.get(j).get("wlgg"));// 员工工号
						gdb_list.get(i).put("wlbh", wlb_list.get(j).get("wlbh"));// 员工姓名
					}
				}

				for (int j = 0; j < gxz_list.size(); j++) {
					if (gdb_list.get(i).get("gxid").equals(gxz_list.get(j).get("gxzid"))) {
						gdb_list.get(i).put("gxzmc", gxz_list.get(j).get("gxzmc"));// 工序组名称
					}
				}

				for (int j = 0; j < jgdy_list.size(); j++) {
					if (gdb_list.get(i).get("sbid").equals(jgdy_list.get(j).get("jgdyid"))) {
						gdb_list.get(i).put("jgdymc", jgdy_list.get(j).get("jgdymc"));// 设备组名称
					}
				}

			}
			// 计算工时差异数量等数据
			for (int i = 0; i < gdb_list.size(); i++) {
				if (null != gdb_list.get(i).get("gdybgsl") && null != gdb_list.get(i).get("gdywcsl")) {
					int bgcysl = Integer.parseInt(gdb_list.get(i).get("gdybgsl").toString())
							- Integer.parseInt(gdb_list.get(i).get("gdywcsl").toString());
					gdb_list.get(i).put("bgcysl", bgcysl);// 报工差异数量
				}
				if (null != gdb_list.get(i).get("gdywcsl") && null != gdb_list.get(i).get("jgjp")) {
					int yxgs = Integer.parseInt(gdb_list.get(i).get("gdywcsl").toString())
							* Integer.parseInt(gdb_list.get(i).get("jgjp").toString());
					gdb_list.get(i).put("yxgs", yxgs);// 有效工时
				}
				if (null != gdb_list.get(i).get("yxgs") && null != gdb_list.get(i).get("pbgs")) {
					int ryfh = Integer.parseInt(gdb_list.get(i).get("yxgs").toString())
							/ Integer.parseInt(gdb_list.get(i).get("pbgs").toString());
					gdb_list.get(i).put("ryfh", ryfh + "%");// 人员负荷
				}
			}
		}

		// 如果export_flag为1则进行导出操作，否则就进行查询
		if (null != parameters.get("export_flag") && !"".equals(parameters.get("export_flag"))) {
			bundle.put("rows", gdb_list);
			bundle.put("totalRecord", gdb_list.size());
			int totalPage1 = gdb_list.size() % gdb_list.size() == 0 ? gdb_list.size() / gdb_list.size()
					: gdb_list.size() / gdb_list.size() + 1;
			bundle.put("totalPage", totalPage1);
			bundle.put("currentPage", 1);
			return exportFile(parameters, bundle, gdb_list);
		} else {
			int page = Integer.parseInt(parameters.get("page").toString());
			int pageSize = Integer.parseInt(parameters.get("pageSize").toString());// 每页显示几条
			List<Map<String, Object>> listIndex = new ArrayList();
			int index = 0;
			for (int i = (page - 1) * pageSize; i < pageSize * page; i++) {
				if (i < gdb_list.size()) {
					listIndex.add(index, gdb_list.get(i));
					index++;
				} else {
					break;
				}
			}
			int totalPage = gdb_list.size() % pageSize == 0 ? gdb_list.size() / pageSize
					: gdb_list.size() / pageSize + 1;
			bundle.put("rows", listIndex);
			bundle.put("totalPage", totalPage);// 共几页
			bundle.put("currentPage", page);// 第几页
			bundle.put("totalRecord", gdb_list.size());
			return "json:";
		}
	}

	/**
	 * excel导出
	 */
	public String exportFile(Parameters parameters, Bundle bundle, List<Map<String, Object>> list) {
		InputStream is = null;
		Map setting = new HashMap();
		setting.put("cellTitle", "日期,人员姓名,员工工号,零件规格,零件图号,生产批号,流程卡编号,工序组名称,设备组名称,报工合格数量,报废工费,报废料废,报工差异数量,设备加工节拍,有效工时,排班工时,人员负荷");
		setting.put("cellIndex", "gdscsj,xm,yggh,wlgg,wlbh,pcid,xh,gxzmc,jgdymc,gdywcsl,bfgf,bflf,bgcysl,jgjp,yxgs,pbgs,ryfh");
		for (int i = 0; i < list.size(); i++) {
			list.get(i).remove("zzjgid");
			list.get(i).remove("zjgkssj");
			list.get(i).remove("jgkssj");
			list.get(i).remove("fgdbh");
			list.get(i).remove("ncbgsl");
			list.get(i).remove("gdybgsl");
			list.get(i).remove("gdid");
			list.get(i).remove("czry");
			list.get(i).remove("gdbh");
			list.get(i).remove("jhjssj");
			list.get(i).remove("gxzbs");
			list.get(i).remove("gdztdm");
			list.get(i).remove("sbid");
			list.get(i).remove("dyzt");
			list.get(i).remove("jgwcsj");
			list.get(i).remove("kgzt");
			list.get(i).remove("jgsl");
			list.get(i).remove("ljid");
			list.get(i).remove("tssyx");
			list.get(i).remove("jhkssj");
			list.get(i).remove("gxmc");
			list.get(i).remove("gxzxh");
			list.get(i).remove("gdwcsj");
			list.get(i).remove("gxid");

			
			//gdscsj,xm,yggh,wlgg,wlbh,pcid,xh,gxzmc,jgdymc,gdywcsl,bfgf,bflf,bgcysl,jgjp,yxgs,pbgs,ryfh");
			if (null == list.get(i).get("gdscsj")) {
				list.get(i).put("gdscsj", "");
			}
			if (null == list.get(i).get("xm")) {
				list.get(i).put("xm", "");
			}
			if (null == list.get(i).get("yggh")) {
				list.get(i).put("yggh", "");
			}
			if (null == list.get(i).get("wlgg")) {
				list.get(i).put("wlgg", "");
			}
			if (null == list.get(i).get("wlbh")) {
				list.get(i).put("wlbh", "");
			}
			if (null == list.get(i).get("pcid")) {
				list.get(i).put("pcid", "");
			}
			if (null == list.get(i).get("xh")) {
				list.get(i).put("xh", "");
			}
			if (null == list.get(i).get("gxzmc")) {
				list.get(i).put("gxzmc", "");
			}
			if (null == list.get(i).get("jgdymc")) {
				list.get(i).put("jgdymc", "");
			}
			if (null == list.get(i).get("gdywcsl")) {
				list.get(i).put("gdywcsl", "");
			}
			if (null == list.get(i).get("bfgf")) {
				list.get(i).put("bfgf", "");
			}
			if (null == list.get(i).get("bflf")) {
				list.get(i).put("bflf", "");
			}
			if (null == list.get(i).get("bgcysl")) {
				list.get(i).put("bgcysl", "");
			}
			if (null == list.get(i).get("jgjp")) {
				list.get(i).put("jgjp", "");
			}
			if (null == list.get(i).get("yxgs")) {
				list.get(i).put("yxgs", "");
			}
			if (null == list.get(i).get("pbgs")) {
				list.get(i).put("pbgs", "");
			}
			if (null == list.get(i).get("ryfh")) {
				list.get(i).put("ryfh", "");
			}
		}
		InputStream result = Sys.exportFile("xlsx", is, list, setting);
		File file = null;
		try {
			file = new File("test.xlsx", null, result, "xlsx", Long.valueOf(String.valueOf(result.available())));
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
