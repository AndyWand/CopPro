/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Andreas Wandert
 */
public class Configuration {

    /**
     * Search strings these are all the tag names ConfigrationReader class as to look for
     */
    /**
     * global settings
     *
     */
    private static final String SEARCH_GENERAL_STORAGE = "global:productdatastorage";
    /**
     * Settings for datasources
     */
    //AWS Settings
    private static final String SEARCH_AWS_BASEURL = "datasource:aws:baseurl";
    //SciHub settings
    private static final String SEARCH_SCIHUB_BASEURL = "datasource:scihub:baseurl";
    public static final String SEARCH_SCIHUB_ODATAURL = "datasource:scihub:odataurl";
    public static final String SEARCH_SCIHUB_CREDENTIALS = "datasource:scihub:credentials";
    //CODE_DE settings
    public static final String SEARCH_CODEDE_BASEURL = "datasource:codede:baseurl";
    /**
     * Settings for processors
     */
    //SEN2COR settings
    /**
     * Settings for data delivery
     */
    //WPS
    /**
     * global settings
     */
    public static final String GENERAL_STORAGE = "";

    /**
     * Settings for datasources
     */
    //AWS Settings
    public static final String AWS_BASEURL = "";

    //SciHub settings
    public static final String SCIHUB_BASEURL = "";
    public static final String SCIHUB_ODATAURL = "";
    public static final String SCIHUB_CREDENTIALS = "";

    //CODE_DE settings
    public static final String CODEDE_BASEURL = "";

    /**
     * Settings for processors
     */
    //SEN2COR settings
    public static final String SEN2COR = "";

    /**
     * Settings for data delivery
     */
    //WPS
}
