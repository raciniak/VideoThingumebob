/**
 * 
 */
package com.lecturershow.videothingumebob;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 * @author Krzysztof Raciniewski
 * Zbiór metod statycznych, których głównym zadaniem jest zmiana rozdzielczości 
 * pliku wideo o raz zmiana formatu
 */
public class ConvertAndResizeVideo {

	
	/**
     * Funkcja konwertuje plik wideo z podanej lokalizacji na inny plik podany w zmiennej outputFile 
     * @param inputFile ścieżka do pliku wideo, który ma zostać poddany konwersji na inny format
     * @param outputFile ścieżka do już skonwertowanego pliku wideo
     * @param width
     * @param height
     */
    public static void go(String inputFile, String outputFile, int width, int height)
    {
        // CUSTOM LISTENERS
        ConvertVideoListener myVideoListener = new ConvertVideoListener(width,height);
        Resizer resizer = new Resizer(width,height);
        
        // READER
        IMediaReader mediaReader = ToolFactory.makeReader(inputFile);
        mediaReader.addListener(resizer);
        
        // WRITER
        IMediaWriter mediaWriter = ToolFactory.makeWriter(outputFile, mediaReader);
        resizer.addListener(mediaWriter);
        mediaWriter.addListener(myVideoListener);
        
      
        // czytam i dekoduję pakiety z pliku źródłowego i wysyła je do writera
        while( mediaReader.readPacket() == null )
        {
            // pusta pętla
        }       
    }

}