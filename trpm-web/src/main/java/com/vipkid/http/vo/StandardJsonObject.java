package com.vipkid.http.vo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现描述：QUNAR标准接口的JSON数据返回值
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/17 下午3:20
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StandardJsonObject implements Serializable {


    private static final long serialVersionUID = 1L;


    private static final Boolean CORRECT_RET = Boolean.TRUE;
    private static final Boolean ERR_RET = Boolean.FALSE;

    private static final String DATA_DEFAULT_MESSAGE = "message";
    private static final int DEFAULT_ERR_CODE = 0;


    /** 业务数据 */
    private Map<String,Object> data;

    /** 出错码， 当调用出错时， 此字段是必须的 */
    private Integer errcode;

    /** 出错信息， 输出出错码对应的出错信息 */
    private String errmsg;

    /** 表示调用是否成功，成功为true，失败为false */
    private Boolean ret;



    /**
     * 生成一个标准的正确返回对象，{"ret": true}
     *
     * @return 标准正确返回对象，不带data值
     */
    public static StandardJsonObject newCorrectJsonObject() {
        StandardJsonObject correctJsonObject = new StandardJsonObject();
        correctJsonObject.setRet(StandardJsonObject.CORRECT_RET);
        return correctJsonObject;
    }

    /**
     * 生成一个标准的正确返回DTO，{"ret": true, "data": [{JsonStringOfObject}]}
     *
     * @param object 业务信息<Map>
     * @return
     */
    public static StandardJsonObject newCorrectJsonObject(Object object) {
        StandardJsonObject correctJsonObject = StandardJsonObject.newCorrectJsonObject();
        if (object instanceof Map) {
            correctJsonObject.setData((Map<String,Object>)object);
        } else {
            correctJsonObject.putData(StandardJsonObject.DATA_DEFAULT_MESSAGE, object);
        }

        return correctJsonObject;
    }

    /**
     * 生成一个标准的错误返回DTO，{"ret": false,"errcode": code,"errmsg" : "msg"}
     *
     * @param code
     * @param msg
     * @return
     */
    public static StandardJsonObject newErrorJsonObject(Integer code, String msg) {
        StandardJsonObject errorReturnObject = new StandardJsonObject();
        errorReturnObject.setRet(StandardJsonObject.ERR_RET);
        errorReturnObject.setErrcode(code);
        errorReturnObject.setErrmsg(msg);
        return errorReturnObject;
    }

    /**
     * 生成一个标准的错误返回DTO，{"ret": false,"errmsg" : "msg"}
     *
     * @param msg
     * @return
     */
    public static StandardJsonObject newErrorJsonObject(String msg) {
        return StandardJsonObject.newErrorJsonObject(StandardJsonObject.DEFAULT_ERR_CODE, msg);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StandardJsonObject other = (StandardJsonObject) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (errcode == null) {
            if (other.errcode != null) {
                return false;
            }
        } else if (!errcode.equals(other.errcode)) {
            return false;
        }
        if (errmsg == null) {
            if (other.errmsg != null) {
                return false;
            }
        } else if (!errmsg.equals(other.errmsg)) {
            return false;
        }
        if (ret == null) {
            if (other.ret != null) {
                return false;
            }
        } else if (!ret.equals(other.ret)) {
            return false;
        }
        return true;
    }

    /**
     * @return the data
     */
    public Map<String,Object> getData() {
        return data;
    }

    /**
     * @return the errcode
     */
    public Integer getErrcode() {
        return errcode;
    }

    /** -----------------setter/getter--------------------- **/

    /**
     * @return the errmsg
     */
    public String getErrmsg() {
        return errmsg;
    }

    /**
     * @return the ret
     */
    public Boolean getRet() {
        return ret;
    }

    /**
     * 拿到容器中的key对应的值
     *
     * @param key
     * @return
     */
    public Object getValueDate(String key) {
        if (this.data == null) {
            return null;
        }
        Object firstObject = data.get(key);

        return firstObject;

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (data == null ? 0 : data.hashCode());
        result = prime * result + (errcode == null ? 0 : errcode.hashCode());
        result = prime * result + (errmsg == null ? 0 : errmsg.hashCode());
        result = prime * result + (ret == null ? 0 : ret.hashCode());
        return result;
    }

    /**
     * 使用一个HashMap作为业务数据容器，添加业务数据
     *
     * @param key
     * @param value
     */
    public StandardJsonObject putData(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return this;
        }

        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }

        data.put(key,value);

        return this;
    }

    /**
     * @param data the data to set
     */
    public void setData(Map<String,Object> data) {
        this.data = data;
    }

    /**
     * @param errcode the errcode to set
     */
    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    /**
     * @param errmsg the errmsg to set
     */
    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    /**
     * @param ret the ret to set
     */
    public void setRet(Boolean ret) {
        this.ret = ret;
    }

    /** -----------------equals/hashCode--------------------- **/

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
