/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing.Processors.correction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.getProperty;
import static java.lang.System.out;
import java.util.HashSet;
import static org.openide.util.Exceptions.printStackTrace;

/**
 *
 * @author Andreas Wandert
 */
public class Sen2CorAdapter implements Runnable {

    private static final String BASEQUERY = "L2A_Process";
    //Possible Resolutions
    public static final String RESOLUTION_10 = "10";
    public static final String RESOLUTION_20 = "20";
    public static final String RESOLUTION_60 = "60";

    public static final String USER_GIPP = "--GIP_L2A GIP_L2A";
    public static final String SCENE_CLASSIFICATION_GIPP = "--GIP_L2A_SC GIP_L2A_SC";
    public static final String ATHMOSPERIC_GIPP = "--GIP_L2A_AC GIP_L2A_AC";
    private static final String PROGRESS_FILTER = "Progress[%]";
    private static final String TERMINATION_FILTER = "Progress[%]: 100";
    private HashSet<String> possibleResolutions;
    private HashSet<String> possibleGippSources;

    private String actualGippSource = "";
    private String actualResolution = "60";
    //path to the .SAFE -file
    private File sourceProduct = null;
    private Boolean scOnly = false;

    //corent progress of process in % [0,100]
    private double progress;
    private File resultProduct = null;

    //Parts of filename to replace and replacer
    private final String stringToInsert = "MSIL2A";
    private final String stringToReplace = "MSIL1C";

    //optionanl parameters
    //--sc_only         Performs only the scene classification at 60 or 20m
    //                    resolution
    //--cr_only         Performs only the creation of the L2A product tree, no
    //                    processing
    //--GIP_L2A GIP_L2A         Select the user GIPP
    //--GIP_L2A_SC GIP_L2A_SC   Select the scene classification GIPP
    //--GIP_L2A_AC GIP_L2A_AC   Select the atmospheric correction GIPP
    /**
     *
     * @param file is the fileName to the .SAFE - file of a data product
     * @param resolution GSD meter [10,20,60]
     * @param gippSource
     * @param scOnly Performs only the scene classification at 60 or 20m resolution
     */
    public Sen2CorAdapter(File file, String resolution, String gippSource, Boolean scOnly) {
        initResolutions();
        initGipp();
        if (possibleResolutions.contains(resolution)) {
            actualResolution = resolution;
        }
        if (possibleGippSources.contains(gippSource)) {
            actualGippSource = gippSource;
        }
        sourceProduct = file;
        setResultProduct();
        this.scOnly = scOnly;
    }

    /**
     *
     * @param file
     * @param resolution
     */
    public Sen2CorAdapter(File file, String resolution) {
        initResolutions();
        if (possibleResolutions.contains(resolution)) {
            actualResolution = resolution;
        }
        sourceProduct = file;
        setResultProduct();
    }

    /**
     *
     * @param file
     */
    public Sen2CorAdapter(File file) {
        initResolutions();
        sourceProduct = file;
        setResultProduct();
    }

    /**
     *
     * @param resolution
     */
    public void setResolution(String resolution) {
        if (possibleResolutions.contains(resolution)) {
            actualResolution = resolution;
        }
    }

    private void initGipp() {
        possibleGippSources = new HashSet();
        possibleGippSources.add(USER_GIPP);
        possibleGippSources.add(SCENE_CLASSIFICATION_GIPP);
        possibleGippSources.add(ATHMOSPERIC_GIPP);
    }

    private void initResolutions() {
        possibleResolutions = new HashSet();
        possibleResolutions.add(RESOLUTION_10);
        possibleResolutions.add(RESOLUTION_20);
        possibleResolutions.add(RESOLUTION_60);
    }

    /**
     *
     * @return
     */
    public String getResolution() {
        return actualResolution;
    }

    private void setResultProduct() {
        String fileName = sourceProduct.getAbsolutePath();
        fileName = fileName.replace(stringToReplace, stringToInsert);
        resultProduct = new File(fileName);
    }

    private String defineQuery() {
        String query = BASEQUERY + " ";
        query += "--resolution " + actualResolution + " ";
        if (!actualGippSource.equalsIgnoreCase("")) {
            query += actualGippSource + " ";
        }
        if (scOnly) {
            query += "--sc_only ";
        }
        query += sourceProduct.getAbsolutePath();
        return query;
    }

    /**
     *
     * @param cmd
     * @return error core: 0: OK, 1: something went wrong
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized int execCmd(String cmd) throws java.io.IOException, InterruptedException {
        Process tr = getRuntime().exec(cmd);
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(tr.getInputStream()));
        String s;
        while ((s = stdOut.readLine()) != null && !s.contains(TERMINATION_FILTER)) {
            //set the current progress
            if (s.contains(PROGRESS_FILTER)) {
                String substring = s.split(":")[1];
                progress = parseDouble(substring);
            }
            out.println(s);
            if (s.contains(TERMINATION_FILTER)) {
                progress = 100;
                out.println("Process terminated successfully!");
                return 0;
            }
        }
        return 1;
    }

    @Override
    public void run() {
        if (getProperty("os.name").startsWith("Windows")) {
            try {
                execCmd(defineQuery());
            } catch (IOException | InterruptedException ex) {
                printStackTrace(ex);
            }
        } else {
            if (getProperty("os.name").contains("Linux")) {
                throw new UnsupportedOperationException("OS is not supported yet!");
            }
            throw new UnsupportedOperationException("OS is not supported yet!");
        }
    }

    /**
     * progress reflexes the current state of processing
     *
     * @return double [0,100] in %
     */
    public double getProgress() {
        return this.progress;
    }

    /**
     * retuns flag to determine weather prosess is done
     *
     * @return
     */
    public Boolean isDone() {
        return progress == 100;
    }

    /**
     *
     * @return file pointing to result product of the processing before call this make sure the
     * process has terminated sccessfully by call <code>isDone()<code>
     */
    public File getResult() {
        return resultProduct;
    }
}
