package com.locate.rmds.statistic;

/**
 * Utility to calculate statistics for a cycle.
 */
public class CycleStatistics
{
    String _myName;
    
    public long periodicCount;
    public long periodicRate;
            
    long previousTotalCount;

    long prevMilliTime;
    long currentMilliTime;
    long diffMilliTime;
    long lastMilliTime;

    /**
     * CycleStatistics constructor.
     * 
     * @param tag a tag describing what the statistics are for. Useful for
     *            debugging.
     */
    public CycleStatistics( String tag )
    {
        _myName = tag;

    }
    
    /**
     * Calculates the rate for the cycle. The currentCount along with the
     * cycleDuration (specified in the constructor) will be used to determine
     * the rate.
     * 
     * @param currentTotalCount the current count for the cycle.
     * @param startMilliTime collection start time 
     */
    public void calculatePeriodicRate( long currentTotalCount, long startMilliTime ) 
    {
        if( currentTotalCount == 0 )
            return;
        
        // get current time
        currentMilliTime = System.currentTimeMillis();

        // compute the count for this interval
        periodicCount = currentTotalCount - previousTotalCount;

        // the 1st time, use the collection start time
        // for computing the difference
        if( prevMilliTime == 0)
        {
            diffMilliTime = currentMilliTime - startMilliTime;
        }
        else
        {
            // compute time difference between now and the previous
            // collection
            diffMilliTime = currentMilliTime - prevMilliTime;
        }

        // save for next cycle
        prevMilliTime = currentMilliTime;

        if( periodicCount > 0 )
        {
            // compute the periodic rate
            periodicRate = (periodicCount * 1000L)/diffMilliTime;
        
            // save the time of last collection
            lastMilliTime = currentMilliTime;

            // save for next cycle
            previousTotalCount = currentTotalCount;
        }
        else
        {
            periodicRate = 0;
        }
    }
    
    /**
     * Return the millitime of last interval
     * 
     * @return the last interval millitime
     */
    public long getMilliTimeOfLastInterval()
    {
        return lastMilliTime;
    }
   
    /**
     * Return the total count of last interval
     * cycleCount.
     * 
     * @return the last interval total count
     */
    public long getTotalCountOfLastInterval()
    {
        return previousTotalCount;
    }

    /**
     * Calculates the count difference for this cycle and stores in into
     * cycleCount.
     * 
     * @param currentCount the current count for the cycle.
     */
    public void calculateDifference( int currentCount )
    {
        periodicRate = 0;
        periodicCount = currentCount - previousTotalCount;
        previousTotalCount = currentCount;
    }
 }
