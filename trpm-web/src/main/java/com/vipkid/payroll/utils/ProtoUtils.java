/**
 * 
 */
package com.vipkid.payroll.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.vipkid.service.neo.grpc.Page;
import com.vipkid.service.neo.grpc.Page.Builder;

/**
 * @author zouqinghua
 * @date 2016年3月16日 上午11:50:15
 *
 */
public class ProtoUtils {

    public static Integer defaultPageSize = 15;

    /**
     * 构建Proto Page 对象
     * 
     * @param request
     * @return
     */
    public static Page buildPage(HttpServletRequest request) {
        Builder build = Page.newBuilder();
        String no = request.getParameter("pageNo");
        Integer pageNo = 1;
        if (StringUtils.isNumeric(no)) {
            pageNo = Integer.parseInt(no);
        }
        build.setPageNo(pageNo);
        String size = request.getParameter("pageSize");
        Integer pageSize = defaultPageSize;
        if (StringUtils.isNumeric(size)) {
            pageSize = Integer.parseInt(size);
        }
        build.setPageSize(pageSize);
        return build.build();
    }

    @SuppressWarnings("rawtypes")
    public static List ListStrToList(String listStr) {
        JSONArray jsonArray = JSONArray.fromObject(listStr);
        List list = Lists.newArrayList(jsonArray.toArray());
        return list;
    }

    /**
     * Page 转换成Proto
     * 
     * @param page
     * @return
     */
    public static Page pageToProto(com.vipkid.payroll.model.Page page) {
        Page pageProto = null;
        if (page != null) {
            if (defaultPageSize == null) {
                defaultPageSize = 15;
            }
            Integer pageNo = page.getPageNo();
            Integer pageSize = page.getPageSize() == 0 ? defaultPageSize : page.getPageSize();
            pageProto = Page.newBuilder().setPageNo(pageNo).setPageSize(pageSize).build();
        }
        return pageProto;
    }

    /**
     * proto 转换成Page
     * 
     * @param pageProto
     * @return
     */
    public static com.vipkid.payroll.model.Page protoToPage(Page pageProto) {
        com.vipkid.payroll.model.Page page = null;
        if (pageProto != null) {
            Integer pageNo = pageProto.getPageNo();
            Integer pageSize = pageProto.getPageSize();
            Integer count = pageProto.getCount();
            page = new com.vipkid.payroll.model.Page(pageNo, pageSize, count);
        }
        return page;
    }
}
