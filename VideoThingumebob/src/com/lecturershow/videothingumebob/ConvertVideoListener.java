/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lecturershow.videothingumebob;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Zaawansowany listener, którego zadaniem jest konwertowanie obrazów do 
 * wybranego rozmiaru
 * @author Raciniewski Krzysztof - raciniak@gmail.com
 */
public class ConvertVideoListener extends MediaToolAdapter {

    private int width;
    private int height;

    
    ConvertVideoListener(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
	@Override
	public void onAddStream(IAddStreamEvent event) {
		int streamIndex = event.getStreamIndex();
		IStreamCoder streamCoder = event.getSource().getContainer().getStream(streamIndex).getStreamCoder();
		if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) 
		{
			// strumien audio
		} else if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
			streamCoder.setWidth(width);
			streamCoder.setHeight(height);
		}
		super.onAddStream(event);
	}

}