package com.lecturershow.videothingumebob;

import com.itextpdf.text.BadElementException;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

    /**
     * Klasa wykorzystywana w metodzie VideoThingumebob.getImagesFromMovie
     */
    class GetImageFromVideoListener extends MediaToolAdapter 
    {
        // Klasa nadrzędna VideoThingumebob
        private final VideoThingumebob parent;   
        // Ostatni numer skoku
        private int lastImageNumber;
        // Tu przechowuję tablicę pikseli poprzedniego obrazu
        private int[][] prevPicture;
        // Tu przechowuję obrazy w postaci BufferedImage
        // będę do niej zapisywał ostatnie 3s filmu przed obrazem w zmiennej 
        // prevPicture, porównując zmianę obrazu co sekunda wyeliminuję problem
        // zapisywania obrazu w trakcie animowanego przejścia(tak mi się wydaje)
        private ArrayList<BufferedImage> backBuffer;
        // Tu przechowuję obrazy do zapisu jako PDF
        private ArrayList<BufferedImage> imagesToPDF;
        // Zapisz osobno pliki graficzne
        private boolean saveImages;
        // Zapisz pliki graficzne do PDF
        private boolean savePdf;
        // Numer indeksu z backBuffer, wykorzystywany w funkcji  isAnimatedTransition
        private int imageToSave;
        // Czasy pojawienia się slajdów
        private ArrayList<String> times;
        
        
        /**
         * Konstruktor, zapis plików graficznych jak i w formie PDF
         * @param parent Klasa nadrzędna, czyli VideoThingumebob
         */
        public GetImageFromVideoListener( VideoThingumebob parent )
        {
            this.parent = parent;
            this.prevPicture = new int[parent.videoHeight][parent.videoWidth];
            this.backBuffer = new ArrayList<BufferedImage>();
            this.imagesToPDF = new ArrayList<BufferedImage>();
            this.times = new ArrayList<String>();
            this.lastImageNumber = 1;
            this.saveImages = true;
            this.savePdf = true;
        }

        /**
         * Konstruktor rozszerzony, możliwość wyboru, czy pliki mają być zapisane 
         * jako PDF, czy jako pliki graficzne lub też w obu formach
         * @param parent Klasa nadrzędna VideoThingumebob
         * @param saveImages true = zapis obrazków w osobnych plikach
         * @param savePdf true = zapis obrazków w formie PDF
         */
        public GetImageFromVideoListener(VideoThingumebob parent, boolean saveImages, boolean savePdf) {
            this.parent = parent;
            this.prevPicture = new int[parent.videoHeight][parent.videoWidth];
            this.backBuffer = new ArrayList<BufferedImage>();
            this.imagesToPDF = new ArrayList<BufferedImage>();
            this.times = new ArrayList<String>();
            this.lastImageNumber = 1;
            this.saveImages = saveImages;
            this.savePdf = savePdf;
        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event)
        {
            int interval = this.parent.videoFps * this.parent.intervalTime;
            // 
            // Analizuje tylko obrazy, które występują co interval sekund
            //
            if( this.lastImageNumber % interval == 0 )
            {
                // Pobieramy obraz, który będziemy analizować
                BufferedImage image = event.getImage();

                String fileName = Integer.toString((int)Math.floor(this.lastImageNumber/this.parent.videoFps));
                // Jeżeli dopiero zaczynam analizę tablica "pixel" jest pusta
                // zapełniam ją i zapisuje obraz w sekundzie interwału czasowego
                // jeżeli interwał będzie ustawiony na 3-5s ominiemy animację wstępną
                if( this.lastImageNumber == interval )
                {
                    // Zapełniam tablicę pikseli
                    this.prevPicture = getPixels(image);
                    // Jeżeli nie mamy animowanego przejścia, to jakiś obraz z bufora 
                    // pasował do aktualnego, dlatego właśnie go zapisuję do pliku i pdf-a
                    fileName = Integer.toString( Integer.parseInt(fileName) - (this.imageToSave+1) );
                    // Jeżeli animacja występuje od samego początku filmu, a interwał jest mały
                    // to dzięki temu pominiemy zapis obrazu w trakcie animacji
                    if( !isAnimatedTransition(getPixels(image)) )
                    {
                        // Jeżeli nie mamy animowanego przejścia, to jakiś obraz z bufora 
                        // pasował do aktualnego, dlatego właśnie go zapisuję do pliku i pdf-a
                        fileName = Integer.toString( Integer.parseInt(fileName) - (this.imageToSave+1) );
                        //String newFileName = Integer.toString(  );
                        // Jeżeli użytkownik chce zapisać obraz do pliku PDF
                        if( this.savePdf )
                            imagesToPDF.add(this.backBuffer.get(imageToSave));
                        
                        // Zapisuję czas pojawienia się slajdu
                        this.times.add(fileName);
                              

                        // Zapisuję obraz w formie pliku graficznego
                        // Nazwa pliku to sekunda z filmu, którą zapisuję
                        if( this.saveImages )
                        {
                            try {
                                ImageIO.write(this.backBuffer.get(imageToSave), this.parent.format, new File(this.parent.picsSaveLocation+fileName+"."+parent.format));
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }               
                        System.out.printf("Zapisałem obraz nr.%s \n", fileName);
                    }
                }else{ 
                    // Jeżeli jest to kolejny obraz
                    // Porównuję tablicę pikseli zapisanych wcześniej
                    if( !SimplePixelsEquals(getPixels(image), this.prevPicture) && !isAnimatedTransition(getPixels(image)) )
                    {
                        // Jeżeli nie mamy animowanego przejścia, to jakiś obraz z bufora 
                        // pasował do aktualnego, dlatego właśnie go zapisuję do pliku i pdf-a
                        fileName = Integer.toString( Integer.parseInt(fileName) - (this.imageToSave+1) );
                        
                        if( this.savePdf )
                            imagesToPDF.add(this.backBuffer.get(imageToSave));
                        
                        // Zapisuję czas pojawienia się slajdu
                        this.times.add(fileName);

                        // Zapisuję obraz
                        if( this.saveImages )
                        {
                            try {
                                System.out.printf("Zapisuje obraz nr.%s\n", fileName);
                                ImageIO.write(this.backBuffer.get(imageToSave), this.parent.format, new File(this.parent.picsSaveLocation+fileName+"."+parent.format));
                            } catch (IOException ex) {
                                Logger.getLogger(VideoThingumebob.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        // Kopiuję tablicę pikseli aktualnego obrazka do "pixel"
                        System.arraycopy(getPixels(image), 0, this.prevPicture, 0, getPixels(image).length);
                        
                    }
                }  
                this.lastImageNumber++;
                
            // Jeżeli to nie jest badana ramka(lastImageNumber % interval != 0)
            }else{

                double rest = this.lastImageNumber/this.parent.videoFps % this.parent.intervalTime;
                
               if( rest >= (this.parent.intervalTime - this.parent.backBufferSize) && 
                           (this.parent.intervalTime - this.parent.backBufferSize) < interval &&
                           (double)lastImageNumber/this.parent.videoFps - (int)lastImageNumber/this.parent.videoFps == 0
                 )
               {
                   BufferedImage image = event.getImage();
                   
                    //
                    // W buforze chcemy przetrzymywać backBuffer elementów
                    // jeżeli tyle elementów tam się znajduje, to znaczy, że
                    // tworzę nowy bufor 
                    //
                    if( this.backBuffer.size() == this.parent.backBufferSize )
                    {
                        this.backBuffer.clear();
                        System.out.println("Czyszcze backBuffer");
                    }
            
                   
                   //System.out.printf("Reszta: %f - Numer obrazu: %d\n",rest, lastImageNumber/this.parent.videoFps);
                   System.out.printf("Zapisuje w backBuff[%d] sekundę %f \n", (int)(this.parent.backBufferSize-rest), (double)lastImageNumber/this.parent.videoFps);
                   try
                   {
                       this.backBuffer.add(image);
                   }catch(Exception ex)
                   {
                       System.out.println(ex.getMessage());
                   }
               }
                this.lastImageNumber++;
            }
            
            super.onVideoPicture(event);
        }
        
        /**
         * Zapełnia tablicę "prevPicture" informacją o kolorach obrazu
         * @param image 
         * @return Zwraca tablicę dwuwymiarową zapełnioną binarnymi wartościami pikseli obrazu
         */
        private int[][] getPixels(BufferedImage image)
        {
            int[][] pixel = new int[this.parent.videoHeight][this.parent.videoWidth];
            
            for( int i = 0; i < this.parent.videoHeight; i++ )
                for( int j = 0; j < this.parent.videoWidth; j++ )
                {
                    pixel[i][j] = image.getRGB(j, i);
                }
            return pixel;
        }

        /**
         * Porównuje dwie tablice reprezentujące poszczególne piksele obrazu
         * piksel po pikselu
         * @param pixel1
         * @param pixel2
         * @return Zwraca true, jeżeli nie ma wyraźnych zmian, false jeżeli jest wyraźna zmiana
         */
        private boolean SimplePixelsEquals(int[][] pixel1, int[][] pixel2) 
        {
            Color col1;
            Color col2;
            // Ilość różnic, które można uznać jako zmianę kolorów itp
            for( int i = 0; i < this.parent.videoHeight; i++ )
            {
                for( int j = 0; j < this.parent.videoWidth; j++)
                {
                    col1 = new Color(pixel1[i][j], false);
                    col2 = new Color(pixel2[i][j], false);
                    
                    int differenceInRed   = abs(col1.getRed() - col2.getRed());
                    int differenceInGreen = abs(col1.getGreen()- col2.getGreen());
                    int differenceInBlue  = abs(col1.getBlue() - col2.getBlue());
                    
                    if( differenceInRed > this.parent.tolleranceOfDifference || 
                        differenceInGreen > this.parent.tolleranceOfDifference || 
                        differenceInBlue > this.parent.tolleranceOfDifference )
                    {
                            /*System.out.printf("Piksele [%d][%d] sie nie zgadzaja (%d,%d,%d) - (%d,%d,%d)\n", i, j, 
                                                                    col1.getRed(), col1.getGreen(), col1.getBlue(), 
                                                                    col2.getRed(), col2.getGreen(), col2.getBlue());*/ 
                            return false;
                    }
                }
            }
            
            return true;
        }
        
        /**
         * Metoda sprawdza, czy zapisywany obraz nie leży w trakcie animowanego przejścia
         * @param pixels tablica pikseli, która będzie porównywana z backBuffer-em
         * @return zwraca true, jeżeli jest to fragment animowanego przejścia
         *         zwraca false, jeżeli nie jest to fragment animowanego przejścia
         */
        private boolean isAnimatedTransition(int[][] pixels) 
        {
            if( this.imageToSave != -1 )
                this.imageToSave = -1;
            
            int count = 0;
            for( int i=this.parent.backBufferSize-1; i >= 0; i-- )
            {
                if( !SimplePixelsEquals(getPixels(this.backBuffer.get(i)), pixels) )
                {
                    System.out.println("Roznia sie!");
                    count++;
                }else{
                    System.out.println("Nie roznia sie!");
                    this.imageToSave = i;
                }
            }
            if( count == this.parent.backBufferSize )
                return true;
            else
                return false;
        }
        
        @Override
        /**
         * Funkcja wykonuje się po zakończeniu czytania pliku Video
         */
        public void onClose(ICloseEvent event)
        {
            if( this.savePdf )
            {
                // Konwertuję ArrayList na tablicę obiektów BufferedImage
                BufferedImage[] ArrayOfImages = this.imagesToPDF.toArray(new BufferedImage[this.imagesToPDF.size()]);
                try {
                    // Zapis obrazów do PDF
                    Pdf.pngs2pdf(ArrayOfImages, this.parent.picsSaveLocation, this.parent.pdfName);
                } catch (BadElementException ex) {
                    Logger.getLogger(VideoThingumebob.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
            	
                // Zapisuję do pliku czasy pojawienia się slajdów
                PrintWriter out = new PrintWriter(this.parent.picsSaveLocation + "times.txt");
                         
                out.println(Integer.toString(this.times.size()));
                for( String t : this.times )
                {
                    out.println(t);
                }
                
                out.close();
                
            } 
            catch (FileNotFoundException ex) 
            {
                System.out.println(ex.getMessage());
            }

            
        }
 }