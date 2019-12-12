package org.ilastik.ilastik4ij;

import ij.IJ;
import ij.ImagePlus;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import org.ilastik.ilastik4ij.executors.AbstractIlastikExecutor.PixelPredictionType;
import org.ilastik.ilastik4ij.executors.PixelClassification;
import org.scijava.Context;

import java.io.File;
import java.io.IOException;

public class PixelClassificationDemo {
	public static < R extends RealType< R > > void main( String[] args ) throws IOException
	{
		final String ilastikPath = "/Applications/ilastik-1.3.3-OSX.app/Contents/MacOS/ilastik";
		final String inputImagePath = "/Users/tischer/Documents/tobias-kletter/ilastik-test/tubulin_dna_volume.zip";
		final String ilastikProjectPath = "/Users/tischer/Documents/tobias-kletter/ilastik-test/tubulin_dna_volume_pixel_classification.ilp";

		// Open ImageJ
		//
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();


		// Open input image
		//
		final Dataset inputDataset = ij.scifio().datasetIO().open( inputImagePath );
		ij.ui().show( inputDataset );

		// Classify pixels
		//
		final PixelClassification prediction = new PixelClassification(
				new File( ilastikPath ),
				new File( ilastikProjectPath ),
				ij.log(),
				ij.status(),
				4,
				10000 );

		final ImgPlus< R > classifiedPixels =
				(ImgPlus) prediction.classifyPixels(
						inputDataset.getImgPlus(),
						PixelPredictionType.Segmentation );

		ImageJFunctions.show( classifiedPixels, "segmentation" );
	}

}
