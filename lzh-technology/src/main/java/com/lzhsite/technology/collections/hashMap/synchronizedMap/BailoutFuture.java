package com.lzhsite.technology.collections.hashMap.synchronizedMap;

public class BailoutFuture {
	private double iterationsPerSecond;
	private long recordsAdded, recordsRemoved, nullCounter;
 

	public BailoutFuture(double iterationsPerSecond, long recordsAdded, long recordsRemoved, long nullCounter) {
		this.iterationsPerSecond = iterationsPerSecond;
		this.recordsAdded = recordsAdded;
		this.recordsRemoved = recordsRemoved;
		this.nullCounter = nullCounter;
	}

	public double getIterationsPerSecond() {
		return iterationsPerSecond;
	}

	public long getRecordsAdded() {
		return recordsAdded;
	}

	public long getRecordsRemoved() {
		return recordsRemoved;
	}

	public long getNullCounter() {
		return nullCounter;
	}
}