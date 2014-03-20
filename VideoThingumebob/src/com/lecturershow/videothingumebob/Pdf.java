package com.lecturershow.videothingumebob;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Przed uzyciem dodac do classpath biblioteke itextpdf (dolaczona)
 *
 * @author Bartosz Lach / lachu@mat.umk.pl
 */
public class Pdf {

    public static void pngs2pdf(BufferedImage[] slajdy, String savePdfPath, String pdfName) throws BadElementException 
    {
        if( slajdy.length > 0 )
        {
        Document doc = new Document(PageSize.A4.rotate(), 0, 0, 0, 0); //format: A4 Landscape
            try {
                PdfWriter.getInstance(doc, new FileOutputStream(savePdfPath.concat(pdfName))); //zmienic sciezke
                doc.open();

                    for (int i = 0; i < slajdy.length; i++) {
                        if (i != 0) {
                            doc.newPage();
                        }
                        Image obr = Image.getInstance(slajdy[i], null);
                        obr.scaleAbsolute(841.89f, 595.28f); //dopasowanie rozmiaru obrazka do A4
                        doc.add(obr);
                    }
                    doc.close();

            } catch (DocumentException ex) {
                Logger.getLogger(Pdf.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pdf.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}