package com.lzhsite.technology.collections.hashMap.synchronizedMap.Impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.lzhsite.technology.collections.hashMap.synchronizedMap.TaxPayerBailoutDB;
import com.lzhsite.technology.collections.hashMap.synchronizedMap.TaxPayerRecord;

public class TaxPayerBailoutDbImpl implements TaxPayerBailoutDB {
	private final Map<String, TaxPayerRecord> db;

	public TaxPayerBailoutDbImpl(int size) {
		db = Collections.synchronizedMap(new HashMap<String, TaxPayerRecord>(size));
	}

	@Override
	public TaxPayerRecord get(String id) {
		return db.get(id);
	}

	@Override
	public TaxPayerRecord add(String id, TaxPayerRecord record) {
		TaxPayerRecord old = db.put(id, record);
		if (old != null) {
			// restore old TaxPayerRecord
			old = db.put(id, old);
		}
		return old;
	}

	@Override
	public TaxPayerRecord remove(String id) {
		return db.remove(id);
	}

	@Override
	public int size() {
		return db.size();
	}
}