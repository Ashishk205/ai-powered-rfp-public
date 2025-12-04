package com.planet_learning.record;

import java.util.List;

public record RfpSelectedVendorsReq(
		Long rfpId,
		List<Long> vendorIds) {

}
