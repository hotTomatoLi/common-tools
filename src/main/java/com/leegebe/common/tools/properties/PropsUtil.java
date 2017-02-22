/*
 * Copyright (c) 2015 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package com.leegebe.common.tools.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *  配置文件加载工具
 */
public class PropsUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName){
        Properties prop = null;
        InputStream is = null;
        try{
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if(is == null){
                throw new FileNotFoundException(fileName + " file is not found");
            }
            prop = new Properties();
            prop.load(is);
        }catch (IOException e){
            LOGGER.error("load properties file failure",e);
            throw new RuntimeException(e);
        }finally {
            if(is != null){
                try{
                     is.close();
                 }catch (IOException e){
                    LOGGER.error("close inputstream failure",e);
                    throw new RuntimeException(e);
                }
            }
        }
        return prop;
    }

    /**
     * 读取字符型属性值
     * @param prop
     * @param key
     * @return
     */
    public static String getString(Properties prop, String key){
        return getString(prop,key,"");
    }

    /**
     * 读取属性值，带有默认字符串
     * @param prop
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Properties prop, String key, String defaultValue){
        String value = defaultValue;
        if(prop.containsKey(key)){
            value = prop.getProperty(key);
        }
        return value;
    }


}
