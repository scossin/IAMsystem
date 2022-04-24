# IAMsystem

A fast dictionary-based approach for semantic annotation, a.k.a [entity linking](https://en.wikipedia.org/wiki/Entity_linking). Semantic annotation is the process of mapping a sequence of tokens in a document to concepts of a terminology. 
IAMsystem is efficient at annotating documents with large dictionaries (> 300K keywords) with [approximate string matching algorithms](https://en.wikipedia.org/wiki/Approximate_string_matching) (a.k.a fuzzy string matching). 

You provide a list of terms (keywords) you want to detect, you choose and configure some approximate string matching algorithms, IAMsystem does the rest. 
Brat output is available.

## Getting started

Add the dependency to your pom.xml to download it from the Maven Repository:

```XML
<dependency>
 	<groupId>fr.erias</groupId>
	<artifactId>IAMsystem</artifactId>
	<version>1.3.0</version>
</dependency>
```

Quick example:

```java
TermDetector termDetector = new TermDetector();
Term term = new Term("high blood pressure", "I10"); // you can also load a terminology (set of terms) 
termDetector.addTerm(term); 
String document = "The patient has a very highhhh BP.";
// We want IAMsystem to detect: highhhh=high and BP=blood pressure
// To do so, we configure IAMsystem with 2 approximate string matching algorithms:
Abbreviations abbreviations = new Abbreviations();
abbreviations.addAbbreviation("blood pressure", "bp");
StringEncoderSyn soundex = new StringEncoderSyn(new Soundex(), -1);
termDetector.addFuzzyAlgorithm(abbreviations);
termDetector.addFuzzyAlgorithm(soundex);
soundex.addTerm(term, termDetector.getTokenizerNormalizer()); // stores in cache encoded string of each token
DetectOutput detectOutput = termDetector.detect(document);
System.out.println(detectOutput.toString());
// 1 terms detected.
// term number 1:
// 	 label in terminology: 'high blood pressure'
// 	 written exactly like this in the sentence: 'highhhh BP'
// 	 code in terminology: I10
// 	 starting at position:23
// 	 end at position:32
// 	 first token number 5
// 	 last token number 6
```

Have a look at more examples by loading the Maven project in the **example folder** using your favorite IDE.  

## How it works

Like [FlashText](https://github.com/vi3k6i5/flashtext) and [Spacy's phrasematcher](https://spacy.io/api/phrasematcher) algorithms it stores a terminology in a tree data structure (called [a trie](https://en.wikipedia.org/wiki/Trie)) for low memory storage and fast lookup (O(1)): 

<img src="./trie_datastructure.png"/>

IAMsystem handles fuzzy string matching at the token level of a n-gram term. 
In the example below, it detects the term "insuffisance cardiaque aigue" in a document containing "ins cardiaqu aigue": 

<img src="./search_algorithm.png" width="525" height="360"/>

## Approximate string matching
By default, IAMsystem perform exact match only.
The following string matching algorithms are available in IAMsystem:
* [Apache common StringEncoders](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language) (Metaphone, Soundex, Caverphone...)
* Levenshtein distance (by [Lucene](https://lucene.apache.org/))
* Abbreviations (a dictionary must be provided)
* Troncation 
* ClosestSubString

Examples of token matching with different algorithms: 


| token in document    |  token(s) in terminology        |	Approximate string matching algorithm	|
|----------------------|---------------------------------|------------------------------------------|
|   amocssicilllline   | amoxicilline                    |  Soundex                                 |
|   amoicilline        | amoxicilline                    |  Levenshtein (edit distance of 1)        | 
|   amoxicil.          | amoxicilline                    |  Troncation                              | 
|   amoxicillinesssss  | amoxicilline                    |  ClosestSubString                        |
|   bloodpressure      | blood pressure                  |  Levenshtein (edit distance of 1)        | 
|   bp                 | blood pressure                  |  Abbreviations                           |



You can add your own string matching algorithm by implementing the ISynonym interface. 

There are no stemmer because they use language-specific rules.
IAMsystemFR (IAMsystem for French language) is another repository that contains French-specific fuzzy mathing algorithms like a FrenchStemmer and a French Soundex. 


## References
Performance (recall, precision, F-measure) of IAMsystem were evaluated on two information extraction tasks of the [CLEF eHealth initiative](http://www.clef-initiative.eu/). IAMsystem's papers:

*    Cossin S, Jouhet V, Mougin F, Diallo G, Thiessard F. IAM at CLEF eHealth 2018: Concept Annotation and Coding in French Death Certificates. https://arxiv.org/abs/1807.03674 
*    Cossin S and Jouhet V. IAM at CLEF eHealth 2020: Concept Annotation in Spanish Electronic Health Records.  http://www.dei.unipd.it/~ferro/CLEF-WN-Drafts/CLEF2020/paper_198.pdf


Organizers' papers:

* Névéol A, Robert A, Grippo F, Morgand C, Orsi C, Pelikan L, et al. CLEF eHealth 2018 Multilingual Information Extraction Task Overview: ICD10 Coding of Death Certificates in French, Hungarian and Italian. In: CLEF. 2018. http://ceur-ws.org/Vol-2125/invited_paper_18.pdf

*  Miranda-Escalada A, Gonzalez-Agirre A, Armengol-Estapé J, Krallinger M. Overview of automatic clinical coding: annotations, guidelines, and solutions for non-English clinical cases at CodiEsp track of eHealth CLEF 2020. CEUR-WS. 2020; http://ceur-ws.org/Vol-2696/paper_263.pdf


### Release note:

| Version    |                                                                                                        |
|------------|--------------------------------------------------------------------------------------------------------|
|   0.0.1    | First publication of the algorithm (November 2018)                                                     |
|   1.0.0    | First major modification. Change the output object of the detector (December 2020), add TermDetector   |
|   1.2.0    | Re-implement the trie and add a cache mechanism to improve performance                     		      |
|   1.3.0    | Add support to the Apache common StringEncoder library, add Troncation and ClosestSubString algorithms |

## Demo
*    Detect French UMLS concepts: https://www.erias.fr/detectUMLS/
*    Detect Spanish ICD-10 diagnosis and procedure: https://www.erias.fr/codiesp/

## Call it from R
See https://github.com/scossin/RIAMsystem

## Handles context (negation...) with FastContext
See https://github.com/scossin/IAMsystemFastContext


## Acknowledgement
This annotation tool is part of the Drugs Systematized Assessment in real-liFe Environment (DRUGS-SAFE) research platform that is funded by the French Medicines Agency (Agence Nationale de Sécurité du Médicament et des Produits de Santé, ANSM). This platform aims at providing an integrated system allowing the concomitant monitoring of drug use and safety in France.

# Citation 
```
@article{cossin_iam_2018,
	title = {{IAM} at {CLEF} {eHealth} 2018: {Concept} {Annotation} and {Coding} in {French} {Death} {Certificates}},
	shorttitle = {{IAM} at {CLEF} {eHealth} 2018},
	url = {http://arxiv.org/abs/1807.03674},
	urldate = {2018-07-11},
	journal = {arXiv:1807.03674 [cs]},
	author = {Cossin, Sébastien and Jouhet, Vianney and Mougin, Fleur and Diallo, Gayo and Thiessard, Frantz},
	month = jul,
	year = {2018},
	note = {arXiv: 1807.03674},
	keywords = {Computer Science - Computation and Language},
}
```