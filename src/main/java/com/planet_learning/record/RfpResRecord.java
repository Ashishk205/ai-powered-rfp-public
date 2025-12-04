package com.planet_learning.record;

import java.util.List;
import java.util.Map;

public record RfpResRecord(
		Long rfpId,
		List<VendorRecord> vendors,
		Map<String, Object> parsedUserInput) {
	
	public RfpResRecord(Long rfpId, List<VendorRecord> vendors) {
		this(rfpId, vendors, null);
	}
}
