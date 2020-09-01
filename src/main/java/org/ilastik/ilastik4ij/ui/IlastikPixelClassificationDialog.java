package org.ilastik.ilastik4ij.ui;

import org.scijava.log.LogService;
import org.scijava.ui.UIService;

import javax.swing.*;
import java.awt.*;

public class IlastikPixelClassificationDialog extends JDialog {
    private final LogService logService;
    private final UIService uiService;
    private final IlastikPixelClassificationModel model;
    
    private final JPanel contentPanel = new JPanel();
    private final JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    private final JLabel ilpPathLabel = new JLabel("Path:");
    private final JTextField ilpPath = new JTextField();
    private final JButton ilpPathBrowse = new JButton("Browse");
    
    private final JLabel projectTypeLabel = new JLabel("Type:");
    private final JComboBox<String>  projectType = new JComboBox<>();
    private final DefaultComboBoxModel<String> projectTypeModel = new DefaultComboBoxModel<>();
    
    private final JLabel rawDataLabel = new JLabel("Raw Data:");
    private final JComboBox<String>  rawData = new JComboBox<>();
    private final DefaultComboBoxModel<String> rawDataModel = new DefaultComboBoxModel<>();
    
    private final JLabel predictionMaskLabel = new JLabel("Prediction Mask:");
    private final JComboBox<String>  predictionMask = new JComboBox<>();
    private final DefaultComboBoxModel<String> predictionMaskModel = new DefaultComboBoxModel<>();

    private final JButton predictBtn = new JButton("Predict");
    private final JButton cancelBtn = new JButton("Cancel");

    private void initializeComponentLayout() {
        getContentPane().setLayout(new BorderLayout());
        GroupLayout layout  = new GroupLayout(contentPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        contentPanel.setLayout(layout);
        getContentPane().add(contentPanel, BorderLayout.PAGE_START);
        getContentPane().add(controlPanel, BorderLayout.PAGE_END);
        controlPanel.add(cancelBtn);
        controlPanel.add(predictBtn);
        ilpPath.setMinimumSize(new Dimension(400, 20));

        layout.setHorizontalGroup(layout
                .createSequentialGroup()
                .addGroup(layout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(ilpPathLabel)
                        .addComponent(projectTypeLabel)
                        .addComponent(rawDataLabel)
                        .addComponent(predictionMaskLabel)
                )
                .addGroup(layout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout
                                .createSequentialGroup()
                                .addComponent(ilpPath)
                                .addComponent(ilpPathBrowse)
                        )
                        .addComponent(projectType)
                        .addComponent(rawData)
                        .addComponent(predictionMask)
                )
        );
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(ilpPathLabel)
                                .addComponent(ilpPath)
                                .addComponent(ilpPathBrowse)
                )
                .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(projectTypeLabel)
                                .addComponent(projectType)
                )
                .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rawDataLabel)
                                .addComponent(rawData)
                )
                .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(predictionMaskLabel)
                                .addComponent(predictionMask)
                )
        );
        layout.linkSize(SwingConstants.VERTICAL, ilpPathBrowse, ilpPath);
        setResizable(true);
        pack();
    }

    public IlastikPixelClassificationDialog(LogService logService, UIService uiService, IlastikPixelClassificationModel model) {
        this.setModalityType(ModalityType.APPLICATION_MODAL);  // Block until dialog is closed
        this.uiService = uiService;
        this.logService = logService;
        this.model = model;
        for (String name : model.getAvailableDatasets()) {
            this.logService.info("DATASET: " + name);
            this.rawDataModel.addElement(name);
        }
        this.initializeComponentLayout();
    }
}
