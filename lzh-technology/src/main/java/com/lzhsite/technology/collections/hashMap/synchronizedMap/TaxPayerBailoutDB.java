package com.lzhsite.technology.collections.hashMap.synchronizedMap;

public interface TaxPayerBailoutDB {
	static final int NUMBER_OF_RECORDS_DESIRED = 2 * 1000000;

	/**
	 * Get a tax payers record from the database based on his or her id.
	 *
	 * @param id
	 *            - tax payers id
	 * @return tax payers record
	 */
	TaxPayerRecord get(String id);

	/**
	 * Add new tax payers record in the database.
	 *
	 * @param id
	 *            - tax payer's id
	 * @param record
	 *            - tax payer's record
	 * @return taxPayersRecord just added to the database
	 */
	TaxPayerRecord add(String id, TaxPayerRecord record);

	/**
	 * Remove a tax payer's record from the database.
	 *
	 * @param id
	 *            - tax payer's id
	 * @return tax payers record, or null if id not found in database
	 */
	TaxPayerRecord remove(String id);

	/**
	 * Size of the database, i.e. number of records
	 *
	 * @return number of records in the database
	 */
	int size();
}
