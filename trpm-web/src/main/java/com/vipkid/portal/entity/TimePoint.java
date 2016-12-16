package com.vipkid.portal.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static com.vipkid.trpm.util.DateUtils.*;

public class TimePoint {

    /* 格式化的时间点字符串，格式为：12:00 PM */
    private String name;

    /* 时间点毫秒值 */
    private long millis;

    public TimePoint(Date date) {
        this.name = formatTo(date.toInstant(), FMT_HMA_US);
        this.millis = date.getTime();
    }

    public boolean isAm() {
        return StringUtils.contains(name, AM);
    }

    public String getName() {
        return name;
    }

    public long getMillis() {
        return millis;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof TimePoint) {
            final TimePoint timePoint = (TimePoint) object;
            return new EqualsBuilder().append(this.name, timePoint.getName()).append(this.millis, timePoint.getMillis())
                            .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.name).append(this.millis).toHashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
