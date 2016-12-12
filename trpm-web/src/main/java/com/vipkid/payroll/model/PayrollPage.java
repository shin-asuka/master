package com.vipkid.payroll.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("rawtypes")
public class PayrollPage<T> implements Serializable  {
	private static final long serialVersionUID = 1L;
	
   

    private int pageNo = 1; // 当前页码
    private int pageSize = 15; // 页面大小，设置为“-1”表示不进行分页（分页无效）
    private Integer count;// 总记录数，设置为“-1”表示不查询总数

	private int allTotalSalary;
    private List<T> list = new ArrayList<T>(); // 查询结果列表

    public PayrollPage() {
        this.pageSize = -1;
    }

    /**
     * 构造方法
     * 
     * @param request 传递 repage 参数，来记住页码
     * @param response
     */
    public PayrollPage(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, -2);
    }

    /**
     * 构造方法
     * 
     * @param request 传递 repage 参数，来记住页码
     * @param response
     * @param defaultPageSize 默认分页大小，如果传递 -1 则为不分页，返回所有数据
     */
    public PayrollPage(HttpServletRequest request, HttpServletResponse response, int defaultPageSize) {
        String no = request.getParameter("pageNo");
        if (StringUtils.isNumeric(no)) {
            this.setPageNo(Integer.parseInt(no));
        }

        String size = request.getParameter("pageSize");
        if (StringUtils.isNumeric(size)) {
            this.setPageSize(Integer.parseInt(size));
        } else if (defaultPageSize != -2) {
            this.pageSize = defaultPageSize;
        }
    }

    /**
     * 构造方法
     * 
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     */
    public PayrollPage(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0);
    }

    /**
     * 构造方法
     * 
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @param count 数据条数
     */
    public PayrollPage(int pageNo, int pageSize, Integer count) {
        this(pageNo, pageSize, count, new ArrayList<T>());
    }

    /**
     * 构造方法
     * 
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @param count 数据条数
     * @param list 本页数据对象列表
     */
    public PayrollPage(int pageNo, int pageSize, Integer count, List<T> list) {
        this.setCount(count);
        this.setPageNo(pageNo);
        this.pageSize = pageSize;
        this.list = list;
    }

    /**
     * 获取设置总数
     * 
     * @return
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置数据总数
     * 
     * @param count
     */
    public void setCount(Integer count) {
        this.count = count;
        if (pageSize >= count) {
            pageNo = 1;
        }
    }

    /**
     * 获取当前页码
     * 
     * @return
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页码
     * 
     * @param pageNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 获取页面大小
     * 
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置页面大小（最大500）
     * 
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;// > 500 ? 500 : pageSize;
    }

    /**
     * 获取本页数据对象列表
     * 
     * @return List<T>
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 设置本页数据对象列表
     * 
     * @param list
     */
    public PayrollPage setList(List<T> list) {
        this.list = list;
        return this;
    }


	public int getAllTotalSalary() {
		return allTotalSalary;
	}

	public void setAllTotalSalary(int allTotalSalary) {
		this.allTotalSalary = allTotalSalary;
	}

}
