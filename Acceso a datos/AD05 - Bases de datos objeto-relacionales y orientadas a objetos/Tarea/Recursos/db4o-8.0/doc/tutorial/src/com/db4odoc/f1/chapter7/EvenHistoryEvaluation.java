package com.db4odoc.f1.chapter7;

import com.db4o.query.*;
import com.db4odoc.f1.chapter4.*;

public class EvenHistoryEvaluation implements Evaluation {
  public void evaluate(Candidate candidate) {
    Car car=(Car)candidate.getObject();
    candidate.include(car.getHistory().size() % 2 == 0);
  }
}
