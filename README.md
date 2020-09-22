# IAMsystem

A general purpose biomedical semantic annotation tool.
It stores your dictionary in a tree data structure and can detect abbreviations (provided by you) and typos (with Levenshtein distance) at the token level. Brat output is available. 

## Getting started

```XML
<dependency>
 	<groupId>fr.erias</groupId>
	<artifactId>IAMsystem</artifactId>
	<version>0.1.1</version>
</dependency>
```

To build it, you will need Java 1.8 (or higher) JDK a recent version of Maven (https://maven.apache.org/download.cgi) and put the `mvn` command on your path. Now you can run `mvn clean install` in the IAMsystem folder to compile the project. 

See how it works with the example (Maven project) using your favorite IDE.
If you notice some problems, please open an issue.

## Reference
*    Cossin S, Jouhet V, Mougin F, Diallo G, Thiessard F. IAM at CLEF eHealth 2018: Concept Annotation and Coding in French Death Certificates. https://arxiv.org/abs/1807.03674
*    Cossin S and Jouhet V. IAM at CLEF eHealth 2020: Concept Annotation in Spanish Electronic Health Records.  http://www.dei.unipd.it/~ferro/CLEF-WN-Drafts/CLEF2020/paper_198.pdf

## Demo
*    Detect French UMLS concepts: https://www.erias.fr/detectUMLS/
*    Detect Spanish ICD-10 diagnosis and procedure: https://www.erias.fr/codiesp/




## Acknowledgement
This annotation tool is part of the Drugs Systematized Assessment in real-liFe Environment (DRUGS-SAFE) research platform that is funded by the French Medicines Agency (Agence Nationale de Sécurité du Médicament et des Produits de Santé, ANSM). This platform aims at providing an integrated system allowing the concomitant monitoring of drug use and safety in France.

