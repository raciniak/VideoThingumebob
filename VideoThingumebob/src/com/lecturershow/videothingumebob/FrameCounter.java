package com.lecturershow.videothingumebob;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class FrameCounter extends MediaToolAdapter  {
	
	private VideoThingumebob parent;
    
	public FrameCounter( VideoThingumebob parent )
	{
		this.parent = parent;
	}
	
	@Override
    public void onVideoPicture(IVideoPictureEvent event)
    {
		parent.videoFrames++;
		super.onVideoPicture(event);
    }
	
}
