/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.amee.client.model.profile;

import com.amee.client.model.base.AmeeConstants;
import java.math.BigDecimal;

/**
 *
 * @author james
 */
public class ReturnValue {

    protected String name;
    protected BigDecimal value;
    protected String unit;
    protected String perUnit;
    protected Boolean defaultvalue;

    public ReturnValue(String _name, String _value, String _unit, String _perUnit,  Boolean _defaultvalue) {
        name = _name;
        value = new BigDecimal(_value);
        value = value.setScale(AmeeConstants.SCALE, AmeeConstants.ROUNDING_MODE);
        if (value.precision() > AmeeConstants.PRECISION) {
            // TODO: do something
        }
        unit = _unit;
        perUnit = _perUnit;
        defaultvalue = _defaultvalue;
    }

    public ReturnValue(String _name, BigDecimal _value, String _unit, String _perUnit, Boolean _defaultvalue) {
        name = _name;
        value = _value;
        unit = _unit;
        perUnit = _perUnit;
        defaultvalue = _defaultvalue;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getPerUnit() {
        return perUnit;
    }

    public Boolean isDefaultValue() {
        return defaultvalue;
    }

}
