package com.codeit.closet.module.weather.repository;

import com.codeit.closet.common.entity.ForecastKind;
import com.codeit.closet.common.entity.WeatherData;
import com.codeit.closet.common.entity.WeatherRegion;
import io.micrometer.observation.ObservationFilter;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, UUID> {

    @Transactional
    @Modifying
    @Query("""
              delete from WeatherData w
              where w.weatherRegion = :region
              and w.forecastKind = 'SHORT_FCST'
              and w.forecastAt < :now
        """)
    void deleteShortFcstOutsideRange(@Param("region") WeatherRegion weatherRegion,
        @Param("now") Instant forecastAtBefore);

    Optional<WeatherData> findByWeatherRegionAndForecastAtAndForecastKind(WeatherRegion region, Instant forecastAt, ForecastKind forecastKind);
}
