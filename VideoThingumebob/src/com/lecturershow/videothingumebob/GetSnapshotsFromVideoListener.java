package com.lectureshow.videothingumebob;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import java.awt.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;

import java.util.Random;

import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

/**
 * Klasa wykorzystywana w metodzie VideoThingumebob.getImagesFromMovie
 */
class GetSnapshotsFromVideoListener extends MediaToolAdapter {

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
     *
     * @param parent Klasa nadrzędna, czyli VideoThingumebob
     */
    public GetSnapshotsFromVideoListener(int n, VideoThingumebob parent) {
        this.parent = parent;
        this.lastImageNumber = 1;
        this.fileName = 1;
        this.times = new int[n];
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MIN_VALUE;

        Random random = new Random();
        // Zapelniam tablice
        for (int i = 0; i < n; i++) {
            this.times[i] = random.nextInt(abs(this.parent.videoFrames / this.parent.videoFps));
        }

        for (int i = 0; i < n; i++) {
            if (this.times[i] < this.min) {
                this.min = this.times[i];
            }
            if (this.times[i] > this.max) {
                this.max = this.times[i];
            }
        }

    }

    @Override
    public void onVideoPicture(IVideoPictureEvent event) {
        float imageNumber = (float) this.lastImageNumber / this.parent.videoFps;

        BufferedImage image = event.getImage();
        image = Scalr.resize(image, Scalr.Mode.FIT_EXACT, 640, 360);
        
    if (this.min <= imageNumber && this.max >= imageNumber

    
        ) {
            for (int i = 0; i < this.times.length; i++) {
            if ((float) this.lastImageNumber / this.parent.videoFps == this.times[i]) {
                try {
                    ImageIO.write(image, this.parent.format, new File(this.parent.picsSaveLocation + "snapshots/" + this.fileName + "." + parent.format));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.fileName++;
            }
        }
    }

     
    this.lastImageNumber

    ++;
        super.onVideoPicture(event);
}

}
