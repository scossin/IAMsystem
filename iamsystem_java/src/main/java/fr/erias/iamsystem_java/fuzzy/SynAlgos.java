package fr.erias.iamsystem_java.fuzzy;

import java.util.Collection;
import java.util.List;

public class SynAlgos {

  private final List<String> syn;
  private final Collection<String> algos;

  public SynAlgos(List<String> syn, Collection<String> algos) {
    this.syn = syn;
    this.algos = algos;
  }

  public void addAlgo(String algo) {
    this.algos.add(algo);
  }

  public List<String> getSyn() {
    return syn;
  }

  public Collection<String> getAlgos() {
    return algos;
  }
}
