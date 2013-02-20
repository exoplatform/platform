package org.exoplatform.platform.welcomescreens.service;

/**
 * Service used to manage Terms And Conditions
 * @author Clement
 *
 */
public interface TermsAndConditionsService {
  public abstract boolean isTermsAndConditionsChecked();
  public abstract void checkTermsAndConditions();
}
