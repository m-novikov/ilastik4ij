package org.ilastik.ilastik4ij.ui;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import org.scijava.log.LogService;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Vector;

public class IlastikPixelClassificationModel {
    public static final String PROPERTY_ILASTIK_PROJECT_FILE = "ilastikProjectFile";
    public static final String PROPERTY_OUTPUT_TYPE = "outputType";
    public static final String PROPERTY_RAW_INPUT_DATASET_IDX = "rawInputDatasetIdx";
    public static final String PROPERTY_PREDICTION_MASK_DATASET_IDX = "predictionMaskDatasetIdx";

    private final LogService logService;
    private final DatasetService datasetService;
    private final PropertyChangeSupport propertyChangeSupport;

    private String ilastikProjectFile = "";
    private int rawInputDatasetIdx = -1;
    private int predictionMaskDatasetIdx = -1;
    private String outputType = UiConstants.PIXEL_PREDICTION_TYPE_PROBABILITIES;

    public IlastikPixelClassificationModel(LogService logService, DatasetService datasetService) {
        this.logService = logService;
        this.datasetService = datasetService;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public Vector<String> getAvailableDatasets() {
        Vector<String> result = new Vector<>();

        logService.info("TOTAL DATASETS: " + this.datasetService.getDatasets().size());
        for (Dataset ds : this.datasetService.getDatasets()) {
            result.add(ds.getName());
        }

        return result;
    }

    public String getIlastikProjectFile() {
        return this.ilastikProjectFile;
    }

    public void setIlastikProjectFile(String path) {
        String oldValue = path;
        this.ilastikProjectFile = path;
        firePropertyChange(PROPERTY_ILASTIK_PROJECT_FILE, oldValue, path);
    }
    
    public void setRawInputDatasetIdx(int idx) {
        int oldValue = this.rawInputDatasetIdx;
        this.rawInputDatasetIdx = idx;
        firePropertyChange(PROPERTY_ILASTIK_PROJECT_FILE, oldValue, idx);
    }
    
    public int getRawInputDatsetIdx() {
        return this.rawInputDatasetIdx;
    }

    public void setPredictionMaskDatasetIdx(int idx) {
        int oldValue = this.predictionMaskDatasetIdx;
        this.predictionMaskDatasetIdx = idx;
        firePropertyChange(PROPERTY_ILASTIK_PROJECT_FILE, oldValue, idx);
    }

    public int getPredictionMaskDatasetIdx() {
        return this.predictionMaskDatasetIdx;
    }

    public void setOutputType(String type) {
        String oldValue = this.outputType;
        this.outputType = type;
        firePropertyChange(PROPERTY_ILASTIK_PROJECT_FILE, oldValue, type);
    }

    public String getOutputType() {
        return this.outputType;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
