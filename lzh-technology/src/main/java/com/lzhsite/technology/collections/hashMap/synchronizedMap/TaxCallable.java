package com.lzhsite.technology.collections.hashMap.synchronizedMap;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class TaxCallable implements Callable<BailoutFuture> {
	private static long runTimeInMillis = BailoutMain.TEST_TIME;
	final private static Random generator = BailoutMain.random;
	private long nullCounter, recordsRemoved, newRecordsAdded;
	private int index;
	private String taxPayerId;
	final private List<String> taxPayerList;
 
	final private TaxPayerBailoutDB db;

	public TaxCallable(List<String> taxPayerList, TaxPayerBailoutDB db) {
		this.taxPayerList = taxPayerList;
		this.db = db;
		index = 0;
	}

	@Override
	public BailoutFuture call() throws Exception {
		long iterations = 0L, elapsedTime = 0L;
		long startTime = System.currentTimeMillis();
		double iterationsPerSecond = 0;
		do {
			setTaxPayer();
			iterations++;
			TaxPayerRecord tpr = null;
			// Just in case there 'iterations' is about to overflow
			if (iterations == Long.MAX_VALUE) {
				long elapsed = System.currentTimeMillis() - startTime;
				iterationsPerSecond = iterations / ((double) (elapsed / 1000));
				System.err.println("Iteration counter about to overflow ...");
				System.err.println("Calculating current operations per second ...");
				System.err.println("Iterations per second: " + iterationsPerSecond);
				iterations = 0L;
				startTime = System.currentTimeMillis();
				runTimeInMillis -= elapsed;
			}
			if (iterations % 1001 == 0) {
				tpr = addNewTaxPayer(tpr);
			} else if (iterations % 60195 == 0) {
				tpr = removeTaxPayer(tpr);
			} else {
				tpr = updateTaxPayer(iterations, tpr);
			}
			if (iterations % 1000 == 0) {
				elapsedTime = System.currentTimeMillis() - startTime;
			}
		} while (elapsedTime < runTimeInMillis);
		if (iterations >= 1000) {
			iterationsPerSecond = iterations / ((double) (elapsedTime / 1000));
		}
		BailoutFuture bailoutFuture = new BailoutFuture(iterationsPerSecond, newRecordsAdded, recordsRemoved,
				nullCounter);
		return bailoutFuture;
	}

	private TaxPayerRecord updateTaxPayer(long iterations, TaxPayerRecord tpr) {
		if (iterations % 1001 == 0) {
			tpr = db.get(taxPayerId);
		} else {
			// update a TaxPayer's DB record
			tpr = db.get(taxPayerId);
			if (tpr != null) {
				long tax = generator.nextInt(10) + 15;
				tpr.taxPaid(tax);
			}
		}
		if (tpr == null) {
			nullCounter++;
		}
		return tpr;
	}

	private TaxPayerRecord removeTaxPayer(TaxPayerRecord tpr) {
		// remove a tax payer from DB
		tpr = db.remove(taxPayerId);
		if (tpr != null) {
			// remove record from TaxPayerList
			taxPayerList.remove(index);
			recordsRemoved++;
		}
		return tpr;
	}

	private TaxPayerRecord addNewTaxPayer(TaxPayerRecord tpr) {
		// add a new TaxPayer to the DB
		String tmpTaxPayerId = BailoutMain.getRandomTaxPayerId();
		tpr = BailoutMain.makeTaxPayerRecord();
		TaxPayerRecord old = db.add(tmpTaxPayerId, tpr);
		if (old == null) {
			// add to the (local) list
			taxPayerList.add(tmpTaxPayerId);
			newRecordsAdded++;
		}
		return tpr;
	}

	public void setTaxPayer() {
		if (++index >= taxPayerList.size()) {
			index = 0;
		}
		this.taxPayerId = taxPayerList.get(index);
	}
}