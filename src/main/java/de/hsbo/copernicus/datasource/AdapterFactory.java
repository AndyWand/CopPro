/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.datasource;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Andreas
 */
public class AdapterFactory {

    /*
    There are three possible values for type:
    aws, scihub, codede
**/
    private static HashSet<String> allowedTyes;
    private HashMap<Integer, Adapter> adapters;
    private static AdapterFactory instance;
    public static final int AWS = 1;
    public static final int CODEDE = 2;
    public static final int SCIHUB = 1;

    private AdapterFactory() {
        adapters = new <Integer, Adapter>HashMap();
    }

    public static AdapterFactory getInstance() {
        if (AdapterFactory.instance == null) {
            AdapterFactory.instance = new AdapterFactory();
        }
        return AdapterFactory.instance;
    }

    /**
     *
     * @returns an adapter which is available online
     */
    public Adapter getAdapter() {

        if (AdapterCodede.getInstance().isOnline()) {
            return AdapterCodede.getInstance();
        } else {
            if (AdapterAws.getInstance().isOnline()) {
                return AdapterAws.getInstance();
            } else {
                if (AdapterScihub.getInstance().isOnline()) {
                    return AdapterScihub.getInstance();
                }
            }
        }
        return null;
    }

    /**
     *
     * @param type: name of the expected adapter
     * @return an instance of an adapter of the specified type
     */
    public Adapter getAdapter(int type) {
        if (allowedTyes.contains(type)) {
            if (adapters.isEmpty() && !adapters.containsKey(type)) {
                Adapter adapter = null;
                switch (type) {
                    case 1:
                        adapter = AdapterAws.getInstance();
                        break;
                    case 2:
                        adapter = AdapterCodede.getInstance();
                        break;
                    case 3:
                        adapter = AdapterScihub.getInstance();
                        break;
                }
                adapters.put(type, adapter);
                return adapter;
            } else {
                return adapters.get(type);
            }
        }
        return null;
    }
}
