package com.lectureshow.videothingumebob;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

/**
 * @author raciniak
 * Klasa dodaje znak wodny do pliku video zwiększając przy tym lub zmniejszając 
 */
public class WaterMark {

    private String inputFilename = null;
    private String outputFilename = null;
    private String imageFilename = null;
    private double mVolume;
    
    
    /**
     * Konstruktor w którym zwiększanie lub zmniejszanie głośności dźwięku będzie brane pod uwagę
     * @param inputFilename Plik wejściowy
     * @param outputFilename Nazwa pliku wyjściowego, zostanie on zapisany w folderze podanym jako output
     * @param imageFilename Bezwzględna ścieżka do pliku graficznego
     * @param mVolume Przez tą wartość zostaną przemnożone wszystkie próbki strumienia audio (1.0f to wartość neutralna)
     */
    public WaterMark(String inputFilename, String outputFilename, String imageFilename, double mVolume)
    {
    	this.inputFilename = inputFilename;
    	this.outputFilename = outputFilename;
    	this.imageFilename = imageFilename;
    	this.mVolume = mVolume;
    }
    
    public void SetWaterMark()
    {
    	// create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        
        // configure it to generate BufferImages
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

        IMediaWriter mediaWriter = 
               ToolFactory.makeWriter(outputFilename, mediaReader);
        
        IMediaTool imageMediaTool = new StaticImageMediaTool(imageFilename);
        IMediaTool audioVolumeMediaTool = new VolumeAdjustMediaTool(this.mVolume);
        
        // create a tool chain:
        // reader -> addStaticImage -> reduceVolume -> writer
        mediaReader.addListener(imageMediaTool);
        imageMediaTool.addListener(audioVolumeMediaTool);
        audioVolumeMediaTool.addListener(mediaWriter);
        
        while (mediaReader.readPacket() == null) ;	
    
    }
    
    private class StaticImageMediaTool extends MediaToolAdapter {
        
        private BufferedImage logoImage;
        
        public StaticImageMediaTool(String imageFile) {
            
            try {
                logoImage = ImageIO.read(new File(imageFile));
            } 
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not open file");
            }
            
        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            
            BufferedImage image = event.getImage();
            
            Graphics2D g = image.createGraphics();
            
            Rectangle2D bounds = new 
              Rectangle2D.Float(0, 0, logoImage.getWidth(), logoImage.getHeight());

            // Pozycjonuje obraz w prawym górnym rogu
            g.translate(image.getHeight() - bounds.getHeight(),0);
            
            g.setBackground(new Color(255,255,255,0));
            g.drawImage(logoImage, 0, 0, null);
            
            // call parent which will pass the video onto next tool in chain
            super.onVideoPicture(event);
            
        }
        
    }
    
    public class VolumeAdjustMediaTool extends MediaToolAdapter {
        
        // the amount to adjust the volume by
        private double mVolume;
        
        public VolumeAdjustMediaTool(double volume) {
            mVolume = volume;
        }

        @Override
        public void onAudioSamples(IAudioSamplesEvent event) {
            
            // get the raw audio bytes and adjust it's value
            ShortBuffer buffer = 
               event.getAudioSamples().getByteBuffer().asShortBuffer();
            
            for (int i = 0; i < buffer.limit(); ++i) {
                buffer.put(i, (short) (buffer.get(i) * mVolume));
            }

            // call parent which will pass the audio onto next tool in chain
            super.onAudioSamples(event);
            
        }
        
    }
}
