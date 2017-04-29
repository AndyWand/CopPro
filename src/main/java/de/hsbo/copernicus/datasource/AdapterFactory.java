/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.datasource;

import java.util.HashMap;

/**
 * This Factory provides an easy way to get an adapter to one of the porals that
 * provide Sentinel Products. If you just request any adapter the factory
 * provides any in the order: AWS, CODE_DE, SciHub
 *
 * @author Andreas Wandert
 */
class AdapterFactory {

    private HashMap<Integer, AbstractAdapter> adapters = new HashMap<>();
    private static AdapterFactory instance;
    public static final int AWS = 1;
    public static final int CODEDE = 2;
    public static final int SCIHUB = 3;

    private AdapterFactory() {

    }

    public static AdapterFactory getInstance() {
        if (AdapterFactory.instance == null) {
            AdapterFactory.instance = new AdapterFactory();
            return AdapterFactory.instance;
        }
        return AdapterFactory.instance;
    }

    /**
     * This is the default method to request an adapter the order is: CODE_DE,
     * AWS, SciHub
     *
     * @return
     * @returns an adapter which is available online
     */
    public AbstractAdapter getAdapter() {

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
     * This method is to request an adapter for a specific portal
     *
     * @param type: name of the expected adapter as an integer constant
     * @return an instance of an adapter of the specified type
     */
    public AbstractAdapter getAdapter(int type) {
        if (adapters.isEmpty() || !adapters.containsKey(type)) {
            AbstractAdapter adapter = null;
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
}
