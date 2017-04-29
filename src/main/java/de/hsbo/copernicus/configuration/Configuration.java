package de.hsbo.copernicus.configuration;


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
    public static final String SEARCH_GENERAL_STORAGE = "global:productdatastorage";
    /**
     * Settings for datasources
     */
    //AWS Settings
    public static final String SEARCH_AWS_BASEURL = "datasource:aws:baseurl";
    //SciHub settings
    public static final String SEARCH_SCIHUB_BASEURL = "datasource:scihub:baseurl";
    public static final String SEARCH_SCIHUB_ODATAURL = "datasource:scihub:odataurl";
    public static final String SEARCH_SCIHUB_CREDENTIALS = "datasource:scihub:credentials";
    //CODE_DE settings
    public static final String SEARCH_CODEDE_BASEURL = "datasource:codede:baseurl";
    public static final String SEARCH_CODEDE_CREDENTIALS = "datasource:codede:credentials";
    //Sen2Cor
    public static final String SEARCH_SEN2COR = "processing:sen2cor";

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
    private String generalStorage = "";

    /**
     * Settings for datasources
     */
    //AWS Settings
    private String awsBaseurl = "";

    //SciHub settings
    private String sciHubBaseurl = "";
    private String sciHubOdataurl = "";
    private String sciHubCredentials = "";

    //CODE_DE settings
    private String codedeBaseurl = "";
    private String codedeCredentials = "";

    /**
     * Settings for processors
     */
    //SEN2COR settings
    private String SEN2COR = "";

    /**
     * Settings for data delivery
     */
    //WPS
    /**
     *
     * @return
     */
    public String getProductDataStorage() {
        return generalStorage;
    }

    public String getAwsBaseurl() {
        return awsBaseurl;
    }

    public String getGeneralStorage() {
        return generalStorage;
    }

    public String getSciHubBaseurl() {
        return sciHubBaseurl;
    }

    public String getSciHubOdataurl() {
        return sciHubOdataurl;
    }

    public String getSciHubCredentials() {
        return sciHubCredentials;
    }

    public String getCodedeBaseurl() {
        return codedeBaseurl;
    }

    public String getSen2Cor() {
        return SEN2COR;
    }

    public String getCodedeCredentials() {
        return codedeCredentials;
    }
   

    /**
     * consumes the login credentials for Code-DE in plain text formated like :
     * <username>:<password>
     * and stores it as an base64 encrypted string
     *
     * @param codedeCredentials
     */
    public void setCodedeCredentials(String codedeCredentials) {
        //encrypt the credentials in base64 for basic athentication
        codedeCredentials = new String(java.util.Base64.getEncoder().encode(codedeCredentials.getBytes()));
        this.codedeCredentials = codedeCredentials;
    }

    public void setSEN2COR(String SEN2COR) {
        this.SEN2COR = SEN2COR;
    }

    public void setGeneralStorage(String generalStorage) {
        this.generalStorage = generalStorage;
    }

    public void setAwsBaseurl(String awsBaseurl) {
        this.awsBaseurl = awsBaseurl;
    }

    public void setSciHubBaseurl(String SCIHUB_BASEURL) {
        this.sciHubBaseurl = SCIHUB_BASEURL;
    }

    public void setSciHubOdataurl(String SCIHUB_ODATAURL) {
        this.sciHubOdataurl = SCIHUB_ODATAURL;
    }

    /**
     * consumes the login credentials for SciHub in plain text formated like :
     * <username>:<password>
     * and stores it as an base64 encrypted string  
     * 
     * @param SCIHUB_CREDENTIALS
     */
    public void setSciHubCredentials(String SCIHUB_CREDENTIALS) {
        //encrypt the credentials in base64 for basic athentication
        SCIHUB_CREDENTIALS = new String(java.util.Base64.getEncoder().encode(SCIHUB_CREDENTIALS.getBytes()));
        this.sciHubCredentials = SCIHUB_CREDENTIALS;
    }

    public void setCodedeBaseurl(String CODEDE_BASEURL) {
        this.codedeBaseurl = CODEDE_BASEURL;
    }

    public void setSen2Cor(String SEN2COR) {
        this.SEN2COR = SEN2COR;
    }

    public String toString() {
        String result = "";
        //global
        result += "[" + SEARCH_GENERAL_STORAGE + " : " + getGeneralStorage() + "]\n";
        //AWS
        result += "[" + SEARCH_AWS_BASEURL + " : " + getAwsBaseurl() + "]\n";
        //Codede
        result += "[" + SEARCH_CODEDE_BASEURL + " : " + getCodedeBaseurl() + "]\n";
        result += "[" + SEARCH_CODEDE_CREDENTIALS + " : " + getCodedeCredentials()+ "]\n";
        //Scihub
        result += "[" + SEARCH_SCIHUB_BASEURL + " : " + getSciHubBaseurl() + "]\n";
        result += "[" + SEARCH_SCIHUB_ODATAURL + " : " + getSciHubOdataurl() + "]\n";
        result += "[" + SEARCH_SCIHUB_CREDENTIALS + " : " + getSciHubCredentials() + "]\n";
        //Sen2Cor
        result += "[" + SEARCH_SEN2COR + " : " + getSen2Cor() + "]\n";

        return result;
    }

}
