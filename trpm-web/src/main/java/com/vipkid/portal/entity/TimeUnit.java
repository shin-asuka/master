package com.vipkid.portal.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TimeUnit {

    /* 格式化的时间标题，格式为：12:00 PM */
    private String title;

    /* 时间毫秒值 */
    private long millis;

    public TimeUnit(String title, long millis) {
        this.title = title;
        this.millis = millis;
    }

    public String getTitle() {
        return title;
    }

    public long getMillis() {
        return millis;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof TimeUnit) {
            final TimeUnit other = (TimeUnit) object;
            return new EqualsBuilder().append(this.title, other.getTitle()).append(this.millis, other.getMillis())
                            .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.title).append(this.millis).toHashCode();
    }

}
