package fr.erias.IAMsystem.brat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import com.pengyifan.brat.BratDocument;
import com.pengyifan.brat.BratEntity;

import fr.erias.IAMsystem.ct.CT;

/**
 * A candidate term with a type to be exported to Brat
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class CTbrat extends CT {

	/**
	 * "The entities section defines the types for the things that can be marked in text as types"
	 */
	public String bratType;
	
	public CTbrat(String candidateTermString, String[] candidateTokensArray, int startPosition, int endPosition, String type) {
		super(candidateTermString, candidateTokensArray, startPosition, endPosition);
		// TODO Auto-generated constructor stub
		this.bratType = type;
	}
	
	/**
	 * A brat output for a candidateTerm 
	 * @param candidateTerm A candidateTerm
	 * @param bratType "The entities section defines the types for the things that can be marked in text as types"
	 */
	public CTbrat(CT candidateTerm, String bratType) {
		super(candidateTerm);
		// TODO Auto-generated constructor stub
		this.bratType = bratType;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getType() {
		return(bratType);
	}
	
	/**
	 * Get a BratEntity
	 * @return A BratEntity that can be exported to a ann File
	 */
	public BratEntity getBratEntity () {
		BratEntity bratEntity = new BratEntity();
		String id = "T" + Integer.toString(getStartPosition()) + Integer.toString(getEndPosition());
		bratEntity.addSpan(getStartPosition(),getEndPosition() + 1); // important + 1 for Brat
		bratEntity.setId(id);
		bratEntity.setText(getCandidateTermString());
		bratEntity.setType(this.bratType);
		return bratEntity;
	}
	
	/**
	 * Write a set of CandidateTerm to Brat
	 * @param file A annFile
	 * @param setCTbrat A set of CTbrat
	 * @throws IOException If the file can't be found
	 */
	public static void writeCT(File file, Set<CTbrat> setCTbrat) throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		BratDocumentWriter bratDocumentWriter = new BratDocumentWriter(fileWriter);
		BratDocument doc = new BratDocument();
		for (CTbrat candidateTermType : setCTbrat) {
			doc.addAnnotation(candidateTermType.getBratEntity());
		}
		bratDocumentWriter.write(doc);
		bratDocumentWriter.close();
		fileWriter.close();
	}

}
