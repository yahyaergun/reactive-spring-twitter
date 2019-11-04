package com.yergun.twitter.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TwitterUtilsTest {

    @Test
    public void convertZonedDateTimeToEpochWithUTC0TimeZone() throws Exception {
        ZonedDateTime zdt = ZonedDateTime.of(2019, 9, 27,
                12, 45 ,35, 0,
                ZoneId.of("UTC+0"));

        Assertions.assertThat(TwitterUtils.convertZonedDateTimeToEpoch(zdt)).isEqualTo(1569588335);
    }

    @Test
    public void convertZonedDateTimeToEpochWithUTCPlus1TimeZone() throws Exception {
        ZonedDateTime zdt = ZonedDateTime.of(2019, 9, 27,
                12, 45 ,35, 0,
                ZoneId.of("UTC+01"));

        Assertions.assertThat(TwitterUtils.convertZonedDateTimeToEpoch(zdt)).isEqualTo(1569584735);
    }

}
