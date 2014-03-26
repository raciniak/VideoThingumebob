
package com.lecturershow.videothingumebob;

import com.lecturershow.videothingumebob.ExceptionClasses.BadDirectoryLocationException;
import com.lecturershow.videothingumebob.ExceptionClasses.BadFileLocationException;
import com.lecturershow.videothingumebob.ExceptionClasses.BadTypeFileException;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * @author Raciniewski Krzysztof - raciniak@gmail.com
 * @version 1.0
 */
public class VideoThingumebob {
    
    private final String movieFile;
    protected final String picsSaveLocation;
    protected int numStreams;
    protected int videoWidth;
    protected int videoHeight;
    protected int videoFps;
    protected int videoDuration;
    protected int videoFrames;
    protected String format;
    protected int tolleranceOfDifference;
    protected String pdfName;
    protected int intervalTime;
    protected int backBufferSize;
    // Procent pikseli, które będą mogły być pominięte
    private float ommitedPixelPercent;	
    // Ilość pikseli, które mogą zostać pominięte, jeżeli nie pasują
    protected int ommitedPixels;
    
    // Kontener w którym przechowywane bądą strumienie(audio/video) pliku movieFile
    private IContainer container;
    
    /**
     * Konstruktor klasy 
     * @param movieFile ścieżka do pliku wideo
     * @param picsSaveLocation ścieżka do katalogu wynikowego
     * @param format format plików graficznych
     * @param pdfName nazwa pliku pdf(razem z rozszerzeniem)
     * @param tolleranceOfDifference poziom tolerancji na zmianę koloru (0 - 255)
     * @param intervalTime czas w sekundach z jaką ma odbywać się porównywanie obrazów
     * @param ommitedPixelPercent Wartość procentowa, która określa ile procent pikseli całego obrazu może zostać pominięta, kiedy nie są identyczne podczas porównywania
     * @param backBufferSize nazwałem to buforem powrotnym, to ilość obrazów, które będą porównywane z aktualnym, w celu wyeliminowania animowanego przejścia między slajdami
     * @throws BadFileLocationException Wyjątek rzucany, kiedy lokalizacja pliku jest niepoprawna
     * @throws BadDirectoryLocationException Wyjątek rzucany, kiedy likalizacja katalogu jest niepoprawna
     * @throws BadTypeFileException Wyjątek rzucany, kiedy rozszerzenie pliku nie jest obsługiwany
     */
    public VideoThingumebob(String movieFile, String picsSaveLocation, 
                            String format, int tolleranceOfDifference, 
                            int intervalTime, int backBufferSize, float ommitedPixelPercent
                            ) throws BadFileLocationException, BadDirectoryLocationException, BadTypeFileException
    {
    	this.ommitedPixelPercent = ommitedPixelPercent;
    	
    	//
    	// Sprawdzam poprawność wprowadzonych parametrów
    	//
    	File f = new File(movieFile);
    	if( !f.exists() || !f.isFile() )
    	{
    		throw new BadFileLocationException("Zła lokalizacja pliku wejściowego! Lokalizacja musi być bezwzględną ścieżką do pliku wideo.");
    	}
    	
    	f = new File(picsSaveLocation);
    	if( !f.exists() )
    	{
    		System.out.println("Katalog wyjściowy nie istnieje, zostanie on utworzony.");
    		boolean result = f.mkdir();
    		if(result)
    		{
    			System.out.println("Katalog zostal utowzony prawidlowo");
    		}
    		else
    		{
    			throw new BadDirectoryLocationException("Katalog nie mogl zostac utworzony...");
    		}
    	}
    	
    	if( !f.isDirectory())
    	{
    		throw new BadDirectoryLocationException("Zła lokalizacja katalogu wyjściowego! Lokalizacja musi być bezwzględną ścieżką do pliku wideo.");
    	}
    	
    	String type = movieFile.substring(movieFile.length()-3, movieFile.length());
    	if(type.toLowerCase().equals("mts"))
    	{
    		throw new BadTypeFileException("Typ pliku \"."+type+"\" nie jest obslugiwany!");
    	}

        this.movieFile = movieFile;
        this.picsSaveLocation = picsSaveLocation;
        this.format = format;
        this.tolleranceOfDifference = tolleranceOfDifference;
        this.intervalTime = intervalTime;
        this.videoFrames = 0;
        // W backBuffer będzie można przechowywać obrazki pomiędzy interwałem czasowym
        // dlatego nie może on być ani większy, ani równy interwału czsowemu
        if( backBufferSize < intervalTime )
            this.backBufferSize = backBufferSize;
        else
            this.backBufferSize = intervalTime-1;
        
        
        
        // Ta funkcja pobiera potrzebne informacje o filmie i zapisuje je w polach
        getVideoInfo();
        
        System.out.printf("===========================> ILOSC FPS: %d \n\n", this.videoFps);
        System.out.printf("===========================> ILOSC RAMEK: %d \n\n", this.videoFrames);
    }
    
    /**
     * Zapisuje niektóre informacje o pliku do zmiennych(ilość fps, wymiary)
     */
    private void getVideoInfo()
    {
        // Tworzę kontener
        container = IContainer.make();
        
        // Otwieram kontener
        if( container.open(movieFile, IContainer.Type.READ , null) < 0 )
        {
            throw new RuntimeException("Nie można otworzyć pliku " + movieFile);
        }else{
            this.numStreams = container.getNumStreams();
            this.videoDuration =  (int)this.container.getDuration();
            /*
            Przebiegamy przez każdy strumień i czytamy jego informacje
            */
            for( int i = 0; i < numStreams; i++ )
            {
                // Pobieram strumień z kontenera
                IStream stream = container.getStream(i);
                // Pobieram koder, który jest w stanie manipulować strumieniem
                // Jeżeli strumień jest INBOUND, czyli przychodzący, to mamy do
                // czynienia z kodowaniem, w przeciwnym wypadku, czyli jeżeli 
                // jeżeli strumień jest OUTBOUND(wychodzący) mamy do czynienia
                // z dekodowaniem
                IStreamCoder coder = stream.getStreamCoder();        
                // Mając strumien i jego koder/dekoder mogę wypisać o nim informacje
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
                {

                } else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
                {
                    this.videoWidth = coder.getWidth();
                    this.videoHeight = coder.getHeight();
                    
                    this.ommitedPixels = (int)((this.videoWidth*this.videoHeight)*this.ommitedPixelPercent);
                    
                  System.out.println(this.videoWidth + " x " + this.videoHeight + " x " + this.ommitedPixelPercent + " = " + this.ommitedPixels);
                }      
            }
            container.close();
        }        
        
        
        IMediaReader myReader = ToolFactory.makeReader(this.movieFile);
        try
        {
	        FrameCounter listener = new FrameCounter(this);
	        myReader.addListener(listener);
	        
	        while( myReader.readPacket() == null )
	        {
	        	// ----
	        }
        }
        finally
        {
            myReader.close();
        }
        
        this.videoFps = this.videoFrames/(this.videoDuration/1000000);  
        
    }
    
    /**
     * Wypisuje wszystkie możliwe do odczytania informacje o pliku audio/video
     */
    public void showStats()
    {      
    	  /**
    	   * Takes a media container (file) as the first argument, opens it, and tells you what's inside the container.
    	   * @param args Must contain one string which represents a filename
    	   */
    	    
    	    // Create a Xuggler container object
    	    IContainer container = IContainer.make();
    	    
    	    // Open up the container
    	    if (container.open(this.movieFile, IContainer.Type.READ, null) < 0)
    	      throw new IllegalArgumentException("could not open file: " + this.movieFile);
    	    
    	    // query how many streams the call to open found
    	    int numStreams = container.getNumStreams();
    	    System.out.printf("file \"%s\": %d stream%s; \n",
    	        this.movieFile,
    	        numStreams,
    	        numStreams == 1 ? "" : "s");
    	    System.out.printf("duration (ms): %s; \n", container.getDuration() == Global.NO_PTS ? "unknown" : "" + container.getDuration()/1000);
    	    System.out.printf("start time (ms): %s; \n", container.getStartTime() == Global.NO_PTS ? "unknown" : "" + container.getStartTime()/1000);
    	    System.out.printf("file size (bytes): %d; \n", container.getFileSize());
    	    System.out.printf("bit rate: %d; \n", container.getBitRate());
    	    System.out.printf("\n\n");

    	    // and iterate through the streams to print their meta data
    	    for(int i = 0; i < numStreams; i++)
    	    {
    	      // Find the stream object
    	      IStream stream = container.getStream(i);
    	      // Get the pre-configured decoder that can decode this stream;
    	      IStreamCoder coder = stream.getStreamCoder();
    	      
    	      // and now print out the meta data.
    	      System.out.printf("stream %d: \n",    i);
    	      System.out.printf("type: %s; \n",     coder.getCodecType());
    	      System.out.printf("codec: %s; \n",    coder.getCodecID());
    	      System.out.printf("duration: %s; \n", stream.getDuration() == Global.NO_PTS ? "unknown" : "" + stream.getDuration());
    	      System.out.printf("start time: %s; \n", container.getStartTime() == Global.NO_PTS ? "unknown" : "" + stream.getStartTime());
    	      System.out.printf("language: %s; \n", stream.getLanguage() == null ? "unknown" : stream.getLanguage());
    	      System.out.printf("timebase: %d/%d; \n", stream.getTimeBase().getNumerator(), stream.getTimeBase().getDenominator());
    	      System.out.printf("coder tb: %d/%d; \n", coder.getTimeBase().getNumerator(), coder.getTimeBase().getDenominator());
    	      
    	      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
    	      {
    	        System.out.printf("sample rate: %d; \n", coder.getSampleRate());
    	        System.out.printf("channels: %d; \n",    coder.getChannels());
    	        System.out.printf("format: %s \n",        coder.getSampleFormat());
    	      } else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
    	      {
    	        System.out.printf("width: %d; \n",  coder.getWidth());
    	        System.out.printf("height: %d; \n", coder.getHeight());
    	        System.out.printf("format: %s; \n", coder.getPixelType());
    	        System.out.printf("frame-rate: %5.2f; \n", coder.getFrameRate().getDouble());
    	      }
    	      System.out.printf("\n\n");
    	    }
    	    container.close();
    }
    
    /**
     * Funkcja realizująca główną ideę naszego modełka, czyli wyłapywanie slajdów z pliku 
     * video podanego jako parametr konstruktora.
     */
    public void getImagesFromMovie()
    {
    	System.out.println("Zaczynam przetwarzanie");
        // Tworzę media reader
        IMediaReader mediaReader = ToolFactory.makeReader(this.movieFile);
        
        // Konfigurowanie media readera do generowania obrazów w formie BufferImages
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        
        IMediaTool imageMediaTool = new GetImageFromVideoListener(this);
        mediaReader.addListener(imageMediaTool);
        
        // Przebiegam przez cały plik wideo
        while( mediaReader.readPacket() == null );
        
        mediaReader.close();
    }
    
    /**
     * Funkcja odtwarzająca film w oknie typu JFrame( przestarzale funkcje! )
     */
    public void playMovie()
    {
    	DecodeAndPlayAudioAndVideo.Play(this.movieFile);
    }
    
    /**
     * Funkcja dodaje znak wodny do pliku wideo
     * @param fileName Nazwa pliku pod jakim zostane zapisane strumienie
     * @param image Ścieżka bezwzględna do obrazu (najlepiej w formacie PNG)
     * @param mVolume Poziom głośności filmu (1.0f) wartość neutralna
     */
    public void AddWaterMark(String fileName, String image, double mVolume)
    {
    	WaterMark waterMark = new WaterMark(this.movieFile, this.picsSaveLocation+fileName, image, mVolume );
    	waterMark.SetWaterMark();
    }
    
}