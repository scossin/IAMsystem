# IAMsystem

A general purpose biomedical semantic annotation tool.
It stores your dictionary in a tree data structure and can detect abbreviations (provided by you) and typos (with Levenshtein distance) at the token level. Brat output is available. 

## Getting started

To build it, you will need Java 1.8 (or higher) JDK a recent version of Maven (https://maven.apache.org/download.cgi) and put the `mvn` command on your path. Now you can run `mvn clean install` in the IAMsystem folder to compile the project. 

See how it works with the example (Maven project) using your favorite IDE.
If you notice some problems, please open an issue.

## Reference
Cossin et al. IAM at CLEF eHealth 2018 : Concept Annotation
and Coding in French Death Certificates.  https://arxiv.org/abs/1807.03674

## Acknowledgement
This annotation tool is part of the Drugs Systematized Assessment in real-liFe Environment (DRUGS-SAFE) research platform that is funded by the French Medicines Agency (Agence Nationale de Sécurité du Médicament et des Produits de Santé, ANSM). This platform aims at providing an integrated system allowing the concomitant monitoring of drug use and safety in France.

