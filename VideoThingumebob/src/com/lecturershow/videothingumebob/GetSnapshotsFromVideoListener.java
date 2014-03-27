package com.lecturershow.videothingumebob;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import java.util.Random;

import javax.imageio.ImageIO;

    /**
     * Klasa wykorzystywana w metodzie VideoThingumebob.getImagesFromMovie
     */
    class GetSnapshotsFromVideoListener extends MediaToolAdapter 
    {
        // Klasa nadrzędna VideoThingumebob
        private final VideoThingumebob parent;   
        // Ostatni numer skoku
        private int lastImageNumber;
        // Nazwa pliku
        private int fileName;
        // Random int table
        private int times[];
        private int min;
        private int max;
        
        /**
         * Konstruktor, zapis plików graficznych jak i w formie PDF
         * @param parent Klasa nadrzędna, czyli VideoThingumebob
         */
        public GetSnapshotsFromVideoListener(int n, VideoThingumebob parent )
        {
            this.parent = parent;
            this.lastImageNumber = 1;
            this.fileName = 1;
            this.times = new int[n];
            this.min = Integer.MIN_VALUE;
            this.max = Integer.MAX_VALUE;
            
            Random random = new Random();
            // Zapelniam tablice
            for( int i=0; i < n; i++ )
            {
            	this.times[i] = random.nextInt( this.parent.videoFrames / this.parent.videoFps );
            }
            
            for( int i=0; i < n; i++ )
            {
            	if( this.times[i] < this.min )
            		this.min = this.times[i];
            	if( this.times[i] > this.max )
            		this.max = this.times[i];
            }
            
        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event)
        {
            
                // Pobieramy obraz, który będziemy analizować
                BufferedImage image = event.getImage();
                if( this.min < (float)this.lastImageNumber/this.parent.videoFps && this.max > (float)this.lastImageNumber/this.parent.videoFps )
                {
	                for( int i=0; i < this.times.length; i++ )
	                {
		                if( (float)this.lastImageNumber/this.parent.videoFps == this.times[i] )
		                {
		                    
		                            try {
										ImageIO.write(image, this.parent.format, new File(this.parent.picsSaveLocation+"snapshots/"+this.fileName+"."+parent.format));
									} catch (IOException e) {
										e.printStackTrace();
									}
		                            this.fileName++;
		
		                }      
	                }
                }
 
            
            
            this.lastImageNumber++;
            super.onVideoPicture(event);
        }
        
        
 }