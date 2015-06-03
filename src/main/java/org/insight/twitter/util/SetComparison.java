package org.insight.twitter.util;

import java.util.HashSet;
import java.util.Set;

public class SetComparison<T> {
  final Set<T> addedToSet = new HashSet<T>();
  final Set<T> removedFromSet = new HashSet<T>();

  private final Set<T> intersection;
  private final Set<T> union;
  private final int sizeS1;
  private final int sizeS2;

  public SetComparison(Set<T> s1, Set<T> s2) {
    sizeS1 = s1.size();
    sizeS2 = s2.size();
    union = new HashSet<T>(s1);
    union.addAll(s2);
    intersection = new HashSet<T>(s1);
    intersection.retainAll(s2);
  }

  public double getJaccard() {
    if (union.isEmpty()) {
      return 0;
    }
    return ((double) getIntersectionSize()) / getUnionSize();
  }

  public double getPrecision() {
    if (sizeS2 == 0) {
      return 0;
    }
    return ((double) getIntersectionSize()) / sizeS2;
  }

  public double getRecall() {
    if (sizeS1 == 0) {
      return 0;
    }
    return ((double) getIntersectionSize()) / sizeS1;
  }

  public double getF1Score() {
    double denom = getPrecision() + getRecall();
    if (denom == 0) {
      return 0;
    }
    double numer = 2.0 * getPrecision() * getRecall();
    return numer / denom;
  }

  public Set<T> getIntersection() {
    return intersection;
  }

  public int getIntersectionSize() {
    return intersection.size();
  }

  public Set<T> getUnion() {
    return union;
  }

  public int getUnionSize() {
    return union.size();
  }

  public void setAddition(T setElement) {
    addedToSet.add(setElement);
  }

  public void setRemoval(T setItem) {
    removedFromSet.add(setItem);
  }

  public Set<T> additions() {
    return addedToSet;
  }

  public Set<T> removals() {
    return removedFromSet;
  }

  public boolean isEmpty() {
    return addedToSet.isEmpty() && removedFromSet.isEmpty();
  }

  /**
   * Compute the changes between the two sets.
   * @param previous   the previous set
   * @param next       the current set
   * @return the changes
   */
  public static final <T> SetComparison<T> findChanges(final Set<T> previous, final Set<T> next) {
    SetComparison<T> compare = new SetComparison<T>(previous, next);
    // Check for added
    for (T setElement : next) {
      if (!previous.contains(setElement)) {
        compare.setAddition(setElement);
      }
    }
    // Check for removed
    for (T setElement : previous) {
      if (!next.contains(setElement)) {
        compare.setRemoval(setElement);
      }
    }
    return compare;
  }
}
