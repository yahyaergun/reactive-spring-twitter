package com.yergun.twitter.util;

import java.time.ZonedDateTime;

public interface TwitterUtils {
    String QUERY_PARAM_FILTER_KEY = "track";
    String QUERY_PARAM_FILTER_DEFAULT_VALUE = "bieber";

    static Long convertZonedDateTimeToEpoch(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toEpochSecond();
    }

}
