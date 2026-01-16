package com.codeit.closet.module.weather.repository;

import com.codeit.closet.common.entity.ForecastKind;
import com.codeit.closet.common.entity.WeatherData;
import com.codeit.closet.common.entity.WeatherRegion;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeatherDataRepository extends JpaRepository<WeatherData, UUID> {

  @Modifying
  @Query("""
          delete from WeatherData w
          where w.weatherRegion = :region
            and w.forecastKind = 'SHORT_FCST'
            and w.forecastAt < :now
            and not exists (
                select 1
                from Feed f
                where f.weather = w
            )
      """)
  void deleteShortFcstOutsideRange(@Param("region") WeatherRegion weatherRegion,
      @Param("now") Instant forecastAtBefore);

  Optional<WeatherData> findByWeatherRegionIdAndForecastAtAndForecastKind(UUID regionId,
      Instant forecastAt, ForecastKind forecastKind);
}
