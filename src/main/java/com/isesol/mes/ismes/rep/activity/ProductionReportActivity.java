package com.isesol.mes.ismes.rep.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

/**
 * 生产报表
 * 
 * @author YangFan
 *
 */
public class ProductionReportActivity {

	private Logger LOGGER = Logger.getLogger(this.getClass());

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfrq = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfyf = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat sdf_yf = new SimpleDateFormat("yyyy-MM");

	/**
	 * 生产日报表 显示界面
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_scrb(Parameters parameters, Bundle bundle) {

		String zzjgbh = parameters.getString("zzjgbh");
		String bbrq = parameters.getString("bbrq");
		
		LOGGER.info("【生产日报表】初始化页面 查询条件： 组织机构：" + zzjgbh + " 日期：" + bbrq );

		Map<String, String> map = new HashMap<String, String>();
		map.put("zzjgbh", zzjgbh);
		map.put("bbrq", bbrq);
		
		if (StringUtils.isNotBlank(zzjgbh)) {
			Parameters parts = new Parameters();
			parts.set("id", zzjgbh);
			Bundle orgBundle = Sys.callModuleService("org", "nameService", parts);
			map.put("zzjgmc", (String) orgBundle.get("name"));
		}
		
		bundle.put("part", map);

		return "rep_scrb";
	}

	/**
	 * 生产日报表 查询数据
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String table_scrb(Parameters parameters, Bundle bundle) {

		String zzjgbh = parameters.getString("zzjgbh");
		String bbrq = null;
		if (!StringUtils.isEmpty(parameters.getString("bbrq"))) {
			try {
//				bbrq = sdf.parse(parameters.getString("bbrq"));

				bbrq = sdfrq.format(sdf.parse(parameters.getString("bbrq")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		LOGGER.info("【生产日报表】表格刷新数据 查询条件： 组织机构：" + zzjgbh + " 日期：" + bbrq);
		
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());

		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();
		if (StringUtils.isNotBlank(zzjgbh)) {
			con = con + " and zzjgbh = ? ";
			val.add(zzjgbh);
		}
		if (bbrq != null) {
			con = con + " and bbrq = ? ";
			val.add(bbrq);
		}

		Dataset datasetscrb = Sys.query("rep_scrb", " bbrq, zzjgbh, gdbh, ljbh, js, jgkssj, jgjjsj, jgsc ", con,
				" zzjgbh desc ", (page - 1) * pageSize, pageSize, val.toArray());
		List<Map<String, Object>> scrb = datasetscrb.getList();
		
		bundle.put("rows", scrb);
		int totalPage = datasetscrb.getTotal() % pageSize == 0 ? datasetscrb.getTotal() / pageSize
				: datasetscrb.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetscrb.getTotal());

		return "json:";
	}

	/**
	 * 生产情况日汇表 显示界面
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_scqkrhb(Parameters parameters, Bundle bundle) {

		String zzjgbh = parameters.getString("scqkrhb_zzjgbh");
		String bbrq = parameters.getString("scqkrhb_bbrq");
		LOGGER.info("【生产情况日汇表】初始化页面 查询条件： 组织机构：" + zzjgbh + " 日期：" + bbrq );

		Map<String, String> map = new HashMap<String, String>();
		map.put("zzjgbh", zzjgbh);
		map.put("bbrq", bbrq);
		if (StringUtils.isNotBlank(zzjgbh)) {
			Parameters parts = new Parameters();
			parts.set("id", zzjgbh);
			Bundle orgBundle = Sys.callModuleService("org", "nameService", parts);
			map.put("zzjgmc", (String) orgBundle.get("name"));
		}
		
		bundle.put("part", map);

		return "rep_scqkrhb";
	}

	/**
	 * 生产情况月汇表 查询数据
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String table_scqkrhb(Parameters parameters, Bundle bundle) {
		String zzjgbh = parameters.getString("zzjgbh");
		
		String  bbrq = null;
		if (!StringUtils.isEmpty(parameters.getString("bbrq"))) {
			try {
//				Date date = sdf.parse(parameters.getString("bbrq"));
				bbrq = sdfrq.format(sdf.parse(parameters.getString("bbrq")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		
		LOGGER.info("【生产情况日汇表】表格刷新数据 查询条件： 组织机构：" + zzjgbh + " 日期：" + bbrq);

		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());

		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();
		if (StringUtils.isNotBlank(zzjgbh)) {
			con = con + " and zzjgbh = ? ";
			val.add(zzjgbh);
		}
		if (bbrq != null) {
			con = con + " and bbrq = ? ";
			val.add(bbrq);
		}

		Dataset datasetscrb = Sys.query("rep_scqkrhb", " zzjgbh, scrwbh, jhjgsl, sjwcsl, zzsl, kcsl ", con,
				" scrwbh desc ", (page - 1) * pageSize, pageSize, val.toArray());
		List<Map<String, Object>> scrb = datasetscrb.getList();
		bundle.put("bbrq", bbrq);
		bundle.put("rows", scrb);
		int totalPage = datasetscrb.getTotal() % pageSize == 0 ? datasetscrb.getTotal() / pageSize
				: datasetscrb.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetscrb.getTotal());

		return "json:";
	}

	/**
	 * 生产情况月汇表 显示界面
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws ParseException 
	 */
	public String query_scqkyhb(Parameters parameters, Bundle bundle) throws ParseException {

		String zzjgbh = parameters.getString("scqkyhb_zzjgbh");
		String bbrq = parameters.getString("scqkyhb_bbrq");
		LOGGER.info("【生产情况月汇表】初始化页面 查询条件： 组织机构：" + zzjgbh + " 日期：" + bbrq );

		Map<String, String> map = new HashMap<String, String>();
		map.put("zzjgbh", zzjgbh);
		map.put("bbrq", bbrq);
		Date bbrqDate = sdf.parse(bbrq);
		map.put("bbyf", sdf_yf.format(bbrqDate));
		
		if (StringUtils.isNotBlank(zzjgbh)) {
			Parameters parts = new Parameters();
			parts.set("id", zzjgbh);
			Bundle orgBundle = Sys.callModuleService("org", "nameService", parts);
			map.put("zzjgmc", (String) orgBundle.get("name"));
		}
		
		bundle.put("part", map);

		return "rep_scqkyhb";
	}

	/**
	 * 生产情况月汇表 查询数据
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String table_scqkyhb(Parameters parameters, Bundle bundle) {
		String zzjgbh = parameters.getString("zzjgbh");
		String bbrq = null;
		if (!StringUtils.isEmpty(parameters.getString("bbrq"))) {
			try {
//				bbrq = sdf.parse(parameters.getString("bbrq"));
				bbrq = sdfyf.format(sdf.parse(parameters.getString("bbrq")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		LOGGER.info("【生产情况月汇表】表格刷新数据 查询条件： 组织机构：" + zzjgbh + " 日期：" + bbrq);

		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());

		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();
		if (StringUtils.isNotBlank(zzjgbh)) {
			con = con + " and zzjgbh = ? ";
			val.add(zzjgbh);
		}
		if (bbrq != null) {
			con = con + " and bbyf = ? ";
			val.add(bbrq);
		}

		
		Dataset datasetscrb = Sys.query("rep_scqkyhb", " zzjgbh, scrwbh, jhjgsl, sjwcsl, zzsl, kcsl ", con,
				" scrwbh desc ", (page - 1) * pageSize, pageSize, val.toArray());
		List<Map<String, Object>> scrb = datasetscrb.getList();
		bundle.put("bbrq", bbrq);
		bundle.put("rows", scrb);
		int totalPage = datasetscrb.getTotal() % pageSize == 0 ? datasetscrb.getTotal() / pageSize
				: datasetscrb.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetscrb.getTotal());

		return "json:";
	}
	
	public  static void main(String [] args ){
		SimpleDateFormat stf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String dateStr = "20160802";
		stf.format(date);
		stf.format(dateStr);
	}
}
