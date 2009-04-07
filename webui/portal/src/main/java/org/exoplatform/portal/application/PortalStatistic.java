package org.exoplatform.portal.application;

import org.exoplatform.commons.utils.AtomicPositiveLong;
import org.exoplatform.commons.utils.LongSampler;

public class PortalStatistic {

  /** . */
  private static final int         ONE_SECOND   = 20000;

  private final String             appId;

  private final LongSampler        times        = new LongSampler(1000);

  private final LongSampler        throughput   = new LongSampler(1000);

  private final AtomicPositiveLong maxTime      = new AtomicPositiveLong();

  private final AtomicPositiveLong minTime      = new AtomicPositiveLong();

  // count varible, store number of request
  private volatile long            countRequest = 0;

  public PortalStatistic(String appId) {
    this.appId = appId;
  }

  /**
   * Log the time.
   * 
   * @param timeMillis the time to log in milliseconds
   */
  public void logTime(long timeMillis) {

    //
    times.add(timeMillis);
    
    // add current time to throughput array
    throughput.add(System.currentTimeMillis());

    // if time > max time then put a new max time value
    maxTime.setIfGreater(timeMillis);

    // generate first value for min time
    minTime.setIfLower(timeMillis);

    //
    countRequest++;
  }

  public double getMaxTime() {
    long maxTime = this.maxTime.get();
    if (maxTime == -1) {
      return -1;
    }
    return maxTime;
  }

  public double getMinTime() {
    long minTime = this.minTime.get();
    if (minTime == -1) {
      return -1;
    }
    return minTime;
  }

  public double getAverageTime() {
    return times.average();
  }

  /**
   * Compute the throughput.
   * 
   * @return the throughput
   */
  public double getThroughput() {
    return throughput.countAboveThreshold(System.currentTimeMillis() - ONE_SECOND);
  }

  public long viewCount() {
    return countRequest;
  }
}
