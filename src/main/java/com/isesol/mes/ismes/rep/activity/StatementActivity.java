package com.isesol.mes.ismes.rep.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

public class StatementActivity {

	private Logger log4j = Logger.getLogger(this.getClass());
	
	public String show_tjbb(Parameters parameters, Bundle bundle){
		
		return "rep_tjbb";
	}
	
	/**
	 * 选择车间
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String cjSelect(Parameters parameters,Bundle bundle){
		Bundle b = Sys.callModuleService("org", "cjService", parameters);
		if(b == null){
			log4j.info("查询所属车间出现异常，没有返回结果集");
			bundle.put("select_cj", new Object[]{});
			return "json:select_cj";
		}
		List<Map<String,Object>> cjList = (List<Map<String, Object>>) b.get("data");
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : cjList){
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("label", map.get("zzjgmc"));
			m.put("value", map.get("zzjgid"));
			returnList.add(m);
		}
		bundle.put("select_cj", returnList.toArray());
		return "json:select_cj";
	}
}
