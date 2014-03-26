/**
 * 
 */
package com.lecturershow.videothingumebob;

import com.lecturershow.videothingumebob.ExceptionClasses.BadDirectoryLocationException;
import com.lecturershow.videothingumebob.ExceptionClasses.BadFileLocationException;
import com.lecturershow.videothingumebob.ExceptionClasses.BadTypeFileException;

/**
 * @author Krzysztof Raciniewski
 * Wichajster, który ma z pliku video, który jest nagranym kamerą pokazem slajdów
 *
 */
public class MainApp {

    private static final String saveFilePath = "D:\\LecturerShow - nagrania\\gotowe\\output\\";
    private static final String fileName = "D:\\LecturerShow - nagrania\\gotowe\\Presentations\\slajdy_bez_przejsc.avi";
    private static VideoThingumebob thingunebob;
	
	/**
	 * @param args Argumenty programu nie są potrzebne
	 */
	public static void main(String[] args) {
        try{
        	
            thingunebob = new VideoThingumebob(fileName, saveFilePath, "png", 35, 2, 1);
            thingunebob.showStats();
            
            
            // Konwersja pliku wideo na inne formaty
            //thingunebob.convertVideo(args[0], args[1].concat("test.flv"));
            //ConvertAndResizeVideo.go(fileName, saveFilePath + "test320x240.mp4", 320, 240); // QVGA 320×240 (4:3)
            //ConvertAndResizeVideo.go(fileName, saveFilePath + "test420x320.mp4", 480, 320); // HVGA 480×320 (3:2)
            //ConvertAndResizeVideo.go(fileName, saveFilePath + "test640x480.mp4", 640, 480); // VGA  640×480 (4:3)
            //ConvertAndResizeVideo.go(fileName, saveFilePath + "test800x480.mp4", 800, 480); // WVGA 800×480 (5:3)
            //ConvertAndResizeVideo.go(fileName, saveFilePath + "test800x600.mp4", 800, 600); // SVGA 800×600 (4:3)
            //System.out.println("Plik wideo zostal przekonwertowany!");
            
            // Zapisywanie screenów
            // parametr1: zapis obrazów w osobnych plikach
            // parametr2: zapis obrazow w formie PDF 
            thingunebob.getImagesFromMovie();
            
            // Podgląd pliku wideo          
            //thingunebob.playMovie();
            //thingunebob.AddWaterMark("zeZnakiemWodnym.mp4", "/home/raciniak/Pictures/LecturerShow.png", 1.0f);
            
            System.out.println("Program glowny zostal wykonany!");
        }
        catch(BadTypeFileException e)
        {
        	System.out.println(e.getMessage());
        }
        catch(BadDirectoryLocationException e)
        {
        	System.out.println(e.getMessage());
        }
        catch(BadFileLocationException e)
        {
        	System.out.println(e.getMessage());
        }
        catch(RuntimeException e)
        {
            System.out.println(e.getMessage());
        }

	}

}
