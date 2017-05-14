package de.hsbo.copernicus.configuration;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Andreas Wandert
 */
public class ConfigTest {
    public static void main (String[] args){
    
    Configuration conf = ConfigurationReader.getInstance();
        //System.out.println("conf is empty: "+conf.getGeneralStorage().isEmpty() );
        System.out.println(conf.getGeneralStorage());
        System.out.println(conf);
    }
}
