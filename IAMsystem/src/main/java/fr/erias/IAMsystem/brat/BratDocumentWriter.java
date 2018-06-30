package fr.erias.IAMsystem.brat;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.pengyifan.brat.BratAnnotation;
import com.pengyifan.brat.BratDocument;

/**
 * Taken from : https://github.com/yfpeng/pengyifan-brat
 * 
 * Writes the brat document to an output stream.
 *
 * @author "Yifan Peng"
 * @since 1.0.0
 */
public class BratDocumentWriter implements Closeable {

  private BufferedWriter writer;

  /**
   * Creates a brat document writer.
   *
   * @param writer a writer
   */
  public BratDocumentWriter(Writer writer) {
    this.writer = new BufferedWriter(writer);
  }

  private <E extends BratAnnotation> void write(Collection<E> annotations)
      throws IOException {
    for (BratAnnotation annotation : annotations) {
      writer.write(annotation.toString());
      writer.newLine();
    }
  }

  /**
   * Writes the brat document.
   *
   * @param doc the brat document
   * @throws IOException If an I/O error occurs
   */
  public void write(BratDocument doc)
      throws IOException {
    write(doc.getEntities());
    write(doc.getRelations());
    write(doc.getEvents());
    write(doc.getAttributes());
    write(doc.getEquivRelations());
    write(doc.getNotes());
  }

  @Override
  public void close()
      throws IOException {
    writer.flush();
    writer.close();
  }
}
