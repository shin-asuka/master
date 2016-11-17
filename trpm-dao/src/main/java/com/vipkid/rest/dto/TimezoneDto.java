package com.vipkid.rest.dto;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class TimezoneDto {

    private String timezone;
    
    private Integer countryId;
    
    private Integer stateId;
    
    private Integer cityId;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;

    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;

    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;

    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;

    }
}
