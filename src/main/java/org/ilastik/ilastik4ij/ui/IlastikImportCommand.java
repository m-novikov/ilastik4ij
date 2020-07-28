package org.ilastik.ilastik4ij.ui;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.HDF5LinkInformation;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import org.scijava.Initializable;
import org.scijava.ItemVisibility;
import org.scijava.module.MethodCallException;
import org.scijava.ui.UIService;
import org.scijava.widget.ChoiceWidget;
import ij.gui.DialogListener;
import org.json.JSONArray;
import org.json.JSONObject;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import net.imagej.ImgPlus;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.ilastik.ilastik4ij.hdf5.Hdf5DataSetReader;
import org.ilastik.ilastik4ij.util.Hdf5Utils;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


class DatasetInfo {
    public final String name;
    public final HDF5DataSetInformation info;
    public final String axisTags;

    public DatasetInfo(String name, String axisTags, HDF5DataSetInformation info) {
        this.name = name;
        this.info = info;
        this.axisTags = axisTags;
    }
}

class IlastikImportDialog extends GenericDialog {
    private Choice datasetChoice;
    private LogService logService;
    private Vector<DatasetInfo> datasetInfos;

    public IlastikImportDialog(String title, Vector<DatasetInfo> datasets, LogService logService) {
        super(title);
        this.logService = logService;
        this.datasetInfos = datasets;
        List<String> choices = datasets
                .stream()
                .map(e -> Hdf5Utils.dropdownName(e.name, e.info))
                .collect(Collectors.toList());

        String firstChoice = choices.get(0);
        this.addChoice("DatasetName", choices.toArray(new String[0]), firstChoice);
        this.datasetChoice = (Choice)this.getChoices().get(0);
        datasetChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int idx = datasetChoice.getSelectedIndex();
                DatasetInfo info = datasetInfos.get(idx);
                logService.info("SELECTED INDEX" + info.axisTags);
            }
        });
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        super.itemStateChanged(e);
        logService.info(e.toString());
    }
}


@Plugin(type = Command.class, menuPath = "Plugins>ilastik>Import HDF5")
public class IlastikImportCommand extends DynamicCommand implements Initializable  {

    @Parameter
    private LogService logService;

    @Parameter
    private DatasetService datasetService;

    @Parameter
    private StatusService statusService;

    @Parameter(label = "HDF5 File", callback = "datasetChanged")
    private File hdf5File = null;

    @Parameter(label = "Some", choices = {"K", "A"}, callback = "datasetChanged")
    private String hdf5DatasetName;

    @Parameter(label = "Some2", choices = {"K", "A"})
    private String thing;
    
    private void datasetChanged() {
        MutableModuleItem<String> datasetNameItem = getInfo().getMutableInput("hdf5DatasetName", String.class);
        MutableModuleItem<String> thingItem = getInfo().getMutableInput("thing", String.class);
        logService.info("SET STYLE" + datasetNameItem.getChoices().toString());
        List<String> choices = Arrays.asList("Hey", "Hoo");
        datasetNameItem.setChoices(choices);
        thingItem.setChoices(choices);
        datasetNameItem.setValue(this, choices.get(0));
        datasetNameItem.setWidgetStyle(ChoiceWidget.LIST_BOX_STYLE);
        logService.info("SET STYLE" + datasetNameItem.getChoices().toString());
        this.removeInput(thingItem);
        this.addInput(thingItem);
    }


    public void initialize() {
        getInfo();
        final MutableModuleItem<String> thingItem = getInfo().getMutableInput("thing", String.class);
        List<String> choices = Arrays.asList("Hey2", "Hoo2");
        thingItem.setChoices(choices);
                /*
        super.initialize();
        final MutableModuleItem<Dataset> datasetNameItem = getInfo().getMutableInput("hdf5DatasetName", Dataset.class);
        logService.warn("INTI CALLLED" + datasetNameItem.getVisibility());
        datasetNameItem.setWidgetStyle(ChoiceWidget.LIST_BOX_STYLE);

                 */
    }


    public void run() {
        if (true) {
            return;
        }
        OpenDialog od = new OpenDialog("Select HDF5 file", "", "");
        final String hdf5FilePath = od.getPath();
        if (hdf5FilePath == null) {
            logService.info("No HDF5 file selected");
            return;
        }

        try (IHDF5Reader reader = HDF5Factory.openForReading(hdf5FilePath)) {
            Vector<DatasetInfo> datasets = findAvailableDatasets(reader, "/");
            /*
            List<String> choices = datasets.entrySet()
                    .stream()
                    .map(e -> Hdf5Utils.dropdownName(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

             */
            IlastikImportDialog gd = new IlastikImportDialog("Select dataset name and axis order", datasets, logService);
            /*
            String firstChoice = choices.get(0);
            gd.addChoice("DatasetName", choices.toArray(new String[0]), firstChoice);
            int rank = datasets.get(Hdf5Utils.parseDataset(firstChoice)).getRank();
            gd.addStringField("AxisOrder", defaultAxisOrder(rank));
            gd.addCheckbox("ApplyLUT", false);
            gd.addDialogListener(new DialogListener() {
                @Override
                public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
                    Vector<TextField> textFields = genericDialog.getStringFields();
                    Vector<Choice> choiceFields = genericDialog.getChoices();
                    Choice ch = choiceFields.get(0);
                    for (TextField field : textFields) {
                        field.setText(ch.getSelectedItem());
                    }
                    return false;
                }
            });
             */
            gd.showDialog();
            if (gd.wasCanceled()) return;

            String datasetName = Hdf5Utils.parseDataset(gd.getNextChoice());
            /*
            rank = datasets.get(datasetName).getRank();
            String axisOrder = gd.getNextString();
            boolean applyLUT = gd.getNextBoolean();
            if (isValidAxisOrder(rank, axisOrder)) {
                loadDataset(hdf5FilePath, datasetName, axisOrder);
                if (applyLUT) {
                    DisplayUtils.applyGlasbeyLUT();
                }
            }
             */
        }
        logService.info("Done loading HDF5 file!");
    }

    private String defaultAxisOrder(int rank) {
        switch (rank) {
            case 5:
                return "tzyxc";
            case 4:
                return "txyc";
            case 3:
                return "zyx";
            default:
                return "xy";
        }
    }

    private <T extends RealType<T> & NativeType<T>> void loadDataset(String hdf5FilePath, String datasetName, String axisOrder) {
        assert hdf5FilePath != null;
        assert datasetName != null;
        assert axisOrder != null;
        axisOrder = axisOrder.toLowerCase();

        Instant start = Instant.now();

        ImgPlus<T> imgPlus = new Hdf5DataSetReader<T>(hdf5FilePath, datasetName,
                axisOrder, logService, statusService).read();
        ImageJFunctions.show(imgPlus);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        logService.info("Loading HDF5 dataset took: " + timeElapsed);
    }

    private Optional<String> parseAxisTags(String jsonString) {
        JSONObject axisObject = new JSONObject(jsonString);
        JSONArray axesArray = axisObject.optJSONArray("axes");
        StringBuilder axisTags = new StringBuilder();

        if (axesArray == null) {
            return Optional.empty();
        }

        for (int i = 0; i < axesArray.length(); i++) {
            JSONObject axisEntry = axesArray.optJSONObject(i);
            if (axisEntry == null) {
                return Optional.empty();
            }
            
            String axisTag = axisEntry.optString("key");

            if (axisTag == null) {
                return Optional.empty();
            }

            axisTags.append(axisTag);
        }

        return Optional.of(axisTags.toString());
    }

    private Vector<DatasetInfo> findAvailableDatasets(IHDF5Reader reader, String path) {
        HDF5LinkInformation link = reader.object().getLinkInformation(path);
        List<HDF5LinkInformation> members = reader.object().getGroupMemberInformation(link.getPath(), true);

        Vector<DatasetInfo> result = new Vector<>();

        for (HDF5LinkInformation info : members) {
            logService.info(info.getPath() + ": " + info.getType());
            switch (info.getType()) {
                case DATASET:
                    String axisTagsJSON = reader.string().getAttr(info.getPath(), "axistags");
                    String axisTags = parseAxisTags(axisTagsJSON).orElse("none");
                    logService.info("Axis Tags: " + axisTags);
                    result.add(new DatasetInfo(info.getPath(), axisTags, reader.object().getDataSetInformation(info.getPath())));
                    break;
                case GROUP:
                    result.addAll(findAvailableDatasets(reader, info.getPath()));
                    break;
            }
        }

        return result;
    }


    private boolean isValidAxisOrder(int rank, String dimensionOrder) {
        if (dimensionOrder.length() != rank) {
            IJ.error(String.format("Incorrect axis order '%s' for dataset of rank %s", dimensionOrder, rank));
            return false;
        }
        return true;
    }
}
