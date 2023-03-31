# IAMsystem

A Java implementation of IAMsystem algorithm, a fast dictionary-based approach for semantic annotation, a.k.a entity linking.


## Installation

Add the dependency to your pom.xml:

```XML
<dependency>
 	<groupId>fr.erias</groupId>
	<artifactId>IAMsystem</artifactId>
	<version>2.2.0</version>
</dependency>
```

## Usage
You provide a list of keywords you want to detect in a document,
you can add and combine abbreviations, normalization methods (lemmatization, stemming) and approximate string matching algorithms,
IAMsystem algorithm performs the semantic annotation.

See the [documentation](https://iamsystem-python.readthedocs.io/en/latest/) for the configuration details. Although the documentation is for the [python implementation](https://github.com/scossin/iamsystem_python), the Java implementation offers the same functionalities and uses the same variable names.

### Quick example

```java
Matcher matcher = new MatcherBuilder()
		.keywords("North America", "South America")
		.stopwords("and")
		.abbreviations("amer", "America")
		.levenshtein(5, 1, Algorithm.TRANSPOSITION)
		.w(2)
		.build();
List<IAnnotation> annots = matcher.annot("Northh and south Amer.");
for (IAnnotation annot : annots) {
	System.out.println(annot);
}
// Northh Amer	0 6;17 21	North America
// south Amer	11 21	South America
```

## Algorithm
The algorithm was developed in the context of a [PhD thesis](https://theses.hal.science/tel-03857962/).
It proposes a solution to quickly annotate documents using a large dictionary (> 300K keywords) and fuzzy matching algorithms.
No string distance algorithm is implemented in this package, it imports and leverages external libraries. 
Its algorithmic complexity is *O(n(log(m)))* with n the number of tokens in a document and m the size of the dictionary.
The formalization of the algorithm is available in this [paper](https://ceur-ws.org/Vol-3202/livingner-paper11.pdf).

It has participated in several semantic annotation competitions in the medical field where it has obtained satisfactory results,
for example by obtaining the best results in the [Codiesp shared task](https://temu.bsc.es/codiesp/index.php/2019/09/19/awards/).
A dictionary-based model can achieve close performance to a transformer-based model when the task is simple or when the training set is small.
Its main advantage is its speed, which allows a baseline to be generated quickly.
### How it works

Like [FlashText](https://github.com/vi3k6i5/flashtext) and [Spacy's phrasematcher](https://spacy.io/api/phrasematcher) algorithms it stores a terminology in a tree data structure (called [a trie](https://en.wikipedia.org/wiki/Trie)) for low memory storage and fast lookup. 

<img src="./trie_datastructure.png"/>

IAMsystem handles fuzzy string matching at the token level of a n-gram term. 
In the example below, it detects the term "insuffisance cardiaque aigue" in a document containing "ins cardiaqu aigue": 

<img src="./search_algorithm.png" width="525" height="360"/>

### Approximate string matching
By default, IAMsystem performs exact match only.
The following string matching algorithms are available in IAMsystem:
* [Apache common StringEncoders](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language) (Metaphone, Soundex, Caverphone...)
* Levenshtein distance (by https://github.com/universal-automata/liblevenshtein-java)
* Abbreviations (a dictionary must be provided)
* Truncation 
* ClosestSubString
* Regex

You can also add your own fuzzy matching algorithm. 
Examples of token matching with different algorithms: 


| token in document    |  token(s) in terminology        |	Approximate string matching algorithm	|
|----------------------|---------------------------------|------------------------------------------|
|   amocssicilllline   | amoxicilline                    |  Soundex                                 |
|   amoicilline        | amoxicilline                    |  Levenshtein (edit distance of 1)        | 
|   amoxicil.          | amoxicilline                    |  Truncation                              | 
|   amoxicillinesssss  | amoxicilline                    |  ClosestSubString                        | 
|   bp                 | blood pressure                  |  Abbreviations                           |


## References
Performance (recall, precision, F-measure) of IAMsystem were evaluated on two information extraction tasks of the [CLEF eHealth initiative](http://www.clef-initiative.eu/). IAMsystem's papers:

*    Cossin S, Jouhet V, Mougin F, Diallo G, Thiessard F. IAM at CLEF eHealth 2018: Concept Annotation and Coding in French Death Certificates. https://arxiv.org/abs/1807.03674 
*    Cossin S and Jouhet V. IAM at CLEF eHealth 2020: Concept Annotation in Spanish Electronic Health Records. http://ceur-ws.org/Vol-2696/paper_198.pdf
* Cossin S, Diallo G, Jouhet V. IAM at IberLEF 2022: NER of Species Mentions. CEUR workshop proceedings [Internet]. sept 2022. https://ceur-ws.org/Vol-3202/livingner-paper11.pdf


Organizers' papers:

* Névéol A, Robert A, Grippo F, Morgand C, Orsi C, Pelikan L, et al. CLEF eHealth 2018 Multilingual Information Extraction Task Overview: ICD10 Coding of Death Certificates in French, Hungarian and Italian. In: CLEF. 2018. http://ceur-ws.org/Vol-2125/invited_paper_18.pdf

*  Miranda-Escalada A, Gonzalez-Agirre A, Armengol-Estapé J, Krallinger M. Overview of automatic clinical coding: annotations, guidelines, and solutions for non-English clinical cases at CodiEsp track of eHealth CLEF 2020. CEUR-WS. 2020; http://ceur-ws.org/Vol-2696/paper_263.pdf

* A. Miranda-Escalada, E. Farré-Maduell, S. Lima-López, D. Estrada, L. Gascó, M. Krallinger, Mention detection, normalization & classification of species, pathogens, humans and food in clinical documents: Overview of livingner shared task and resources, Procesamiento del Lenguaje Natural (2022)

### Release note:

| Version    |                                                                                                        |
|------------|--------------------------------------------------------------------------------------------------------|
|   0.0.1    | First publication of the algorithm (November 2018)                                                     |
|   1.0.0    | First major modification. Change the output object of the detector (December 2020), add TermDetector   |
|   1.2.0    | Re-implement the trie and add a cache mechanism to improve performance                     		      |
|   1.3.0    | Add support to the Apache common StringEncoder library, add Troncation and ClosestSubString algorithms |
|   2.1.0    | Complete re-write of the library to be in sync with the Python implementation and its documentation. The core algorithm, aka the matching strategy, changed to the *Window Strategy* which allows detection of discontinuous sequences of tokens in a document. The strategy used in previous versions (<2.1.0) is called the *NoOverlap* strategy.|
|   2.2.0    | Fix [issue 18](https://github.com/scossin/iamsystem_python/issues/18): create multiple annotations when a keyword is repeated in the same window.|




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
