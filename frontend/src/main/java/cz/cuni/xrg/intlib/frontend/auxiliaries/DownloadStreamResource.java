package cz.cuni.xrg.intlib.frontend.auxiliaries;

/**
 * Resource for providing file download to client.
 * TODO: Test FileResource and if it works, delete this class.
 *
 * @author Bogo
 */
import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DownloadStreamResource extends StreamResource {
  
  public static final String MIME_TYPE_TTL = "text/turtle";
  public static final String MIME_TYPE_RDFXML = "application/rdf+xml";

  private final byte[] binaryData;

  private final String filename;

  public DownloadStreamResource(final byte[] binaryData, final String filename, 
        final String mimeType) {
   super(new StreamSource() {

      @Override
      public InputStream getStream() {
        return new ByteArrayInputStream(binaryData);
      }
    }, filename);
    
    this.binaryData = binaryData;
    this.filename = filename;
    
    setMIMEType(mimeType);
  }
  
  @Override
  public DownloadStream getStream() {
    final DownloadStream downloadStream = super.getStream();
    // Set the "attachment" to force save-dialog. Important for IE7 (and probably IE8)
   downloadStream.setParameter("Content-Disposition", "attachment; filename=\"" + filename + "\"");

    // Enable deterministic progressbar for download
    downloadStream.setParameter("Content-Length", Integer.toString(binaryData.length));
    
    return downloadStream;
  }

}
