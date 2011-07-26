/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.base;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AmeeConstants {
    // 999,999,999,999,999,999.999
    public final static int PRECISION = 18;
    public final static int SCALE = 3;
    public final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public final static BigDecimal ZERO = BigDecimal.valueOf(0, SCALE);
}
