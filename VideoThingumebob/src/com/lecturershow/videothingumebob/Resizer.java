/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lecturershow.videothingumebob;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

/**
 * Zaawansowany listener, którego zadaniem jest zmiana rozmiaru 
 * w strumieniu wideo
 * @author raciniak
 */
public class Resizer extends MediaToolAdapter{

    private int width;
    private int height;
    
    private IVideoResampler videoResampler = null;
    
    Resizer(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
		IVideoPicture pic = event.getPicture();
		if (videoResampler == null) {
			videoResampler = IVideoResampler.make(width, height, pic.getPixelType(), pic.getWidth(), pic
					.getHeight(), pic.getPixelType());
		}
		IVideoPicture out = IVideoPicture.make(pic.getPixelType(), width, height);
		videoResampler.resample(out, pic);
 
		IVideoPictureEvent asc = new VideoPictureEvent(event.getSource(), out, event.getStreamIndex());
		super.onVideoPicture(asc);
		out.delete();
		
	}
    
}