package fr.erias.IAMsystem.example;

import java.io.IOException;
import java.io.PrintWriter;

import com.pengyifan.brat.BratDocument;

import fr.erias.IAMsystem.brat.BratDocumentWriter;
import fr.erias.IAMsystem.brat.CTbrat;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;

public class Ex4_BratOutput {

	public static void main(String[] args) throws IOException {
		TermDetector termDetector = new TermDetector();
		termDetector.addTerm("high blood pressure", "I10");
		String document = "The patient has a high blood pressure";
		DetectOutput detectOutput = termDetector.detect(document);
		
		// Brat output:
		String bratType = "diseases"; // depends on the configuration of your brat
		BratDocumentWriter bratDocumentWriter = new BratDocumentWriter(new PrintWriter(System.out));
		BratDocument doc = new BratDocument();
		for (CTcode codes : detectOutput.getCTcodes()) {
			CTbrat ctbrat = new CTbrat(codes, bratType);
			doc.addAnnotation(ctbrat.getBratEntity());
		}
		bratDocumentWriter.write(doc);
		bratDocumentWriter.close();
//		T1	diseases 18 37	high blood pressure
	}
}
