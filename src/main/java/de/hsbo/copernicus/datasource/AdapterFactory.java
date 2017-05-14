/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.datasource;

import java.util.HashMap;
import de.hsbo.copernicus.configuration.*;

/**
 * This Factory provides an easy way to get an adapter to one of the porals that provide Sentinel
 * Products. If you just request any adapter the factory provides any in the order: AWS, CODE_DE,
 * SciHub
 *
 * @author Andreas Wandert
 */
class AdapterFactory {

    private static final HashMap<Integer, AbstractAdapter> ADAPTERS = new HashMap<>();
    private static AdapterFactory instance;
    public static final int AWS = 1;
    public static final int CODEDE = 2;
    public static final int SCIHUB = 3;
    private static final HashMap<String, Integer> ADAPTERMAP;

    static {
        ADAPTERMAP = new HashMap<>();
        ADAPTERMAP.put("aws", AWS);
        ADAPTERMAP.put("codede", CODEDE);
        ADAPTERMAP.put("scihub", SCIHUB);
    }
    private final String[] order;

    private AdapterFactory() {
        Configuration config = ConfigurationReader.getInstance();
        order = config.getAdapterOrder();
    }

    public static AdapterFactory getInstance() {
        if (AdapterFactory.instance == null) {
            AdapterFactory.instance = new AdapterFactory();
            return AdapterFactory.instance;
        }
        return AdapterFactory.instance;
    }

    /**
     * This is the default method to request an adapter
     *
     * @return
     * @returns an adapter which is available online
     */
    public AbstractAdapter getAdapter() throws Exception {
        for (String orderIterate : order) {
            AbstractAdapter temp = AdapterFactory.this.getAdapter(ADAPTERMAP.get(orderIterate));
            if (temp.isOnline()) {
                System.out.println("Using Adapter for "+temp.name);
                return temp;
            }
        }
        throw new Exception("No Portal is reachable, please try again later!");
    }

    /**
     * This method is to request an adapter for a specific portal
     *
     * @param type: name of the expected adapter as an integer constant
     * @return an instance of an adapter of the specified type
     */
    public AbstractAdapter getAdapter(int type) {
        if (ADAPTERS.isEmpty() || !ADAPTERS.containsKey(type)) {
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
            ADAPTERS.put(type, adapter);
            return adapter;

        } else {
            return ADAPTERS.get(type);
        }
    }
}
