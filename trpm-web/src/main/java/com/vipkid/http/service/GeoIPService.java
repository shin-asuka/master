package com.vipkid.http.service;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 地理位置服务
 * 
 * @author zouqinghua
 * @date 2016年11月25日  下午12:56:22
 *
 */
@Service
public class GeoIPService {
	
    private Logger logger = LoggerFactory.getLogger(GeoIPService.class);
    private DatabaseReader reader;

    public GeoIPService() {
        String filePath = PropertyConfigurer.stringValue("geoip.database.file.path");
        logger.info("地理位置数据包路径 filePath = {}",filePath);
        File database = new File(filePath);
        try {
            reader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Country getCountryName(final String ip) {
        CityResponse response = getCityResponse(ip);
        return (response == null) ? null : response.getCountry();
    }

    public City getCity(final String ip) {
        CityResponse response = getCityResponse(ip);
        return (response == null) ? null : response.getCity();
    }

    public Subdivision getSubdivision(final String ip) {
        CityResponse response = getCityResponse(ip);
        return (response == null) ? null : response.getMostSpecificSubdivision();
    }

    public CityResponse getCityResponse(final String ip) {
        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            return null;
        }
        if (reader == null) {
            logger.error("fail to open GeoIP database file");
            return null;
        }
        try {
            return reader.city(ipAddress);
        } catch (IOException e) {
            return null;
        } catch (GeoIp2Exception e) {
            return null;
        }
    }
}
