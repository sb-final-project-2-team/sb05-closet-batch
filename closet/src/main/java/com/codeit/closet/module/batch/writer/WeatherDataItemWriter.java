package com.codeit.closet.module.batch.writer;

import com.codeit.closet.common.entity.ForecastKind;
import com.codeit.closet.common.entity.WeatherData;
import com.codeit.closet.common.entity.WeatherRegion;
import com.codeit.closet.module.weather.repository.WeatherDataRepository;
import com.codeit.closet.module.weather.repository.WeatherRegionRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WeatherDataItemWriter implements ItemWriter<List<WeatherData>> {

  private final WeatherDataRepository weatherDataRepository;
  private final WeatherRegionRepository weatherRegionRepository;

  @Override
  @Transactional
  public void write(Chunk<? extends List<WeatherData>> items) {

    for (List<WeatherData> weatherDataList : items) {
      if (weatherDataList.isEmpty()) {
        continue;
      }

      WeatherRegion region = weatherDataList.get(0).getWeatherRegion();

      Instant now = Instant.now()
          .atZone(ZoneId.systemDefault())
          .toLocalDate()
          .atStartOfDay(ZoneId.systemDefault())
          .toInstant();

      weatherDataRepository.deleteShortFcstOutsideRange(
          region,
          now
      );

      List<WeatherData> mergedList = weatherDataList.stream()
          .filter(w -> w.getForecastKind() == ForecastKind.SHORT_FCST)
          .map(newData ->
              weatherDataRepository
                  .findByWeatherRegionAndForecastAtAndForecastKind(
                      region,
                      newData.getForecastAt(),
                      newData.getForecastKind()
                  )
                  .map(existing -> {
                    existing.updateFrom(newData);
                    return existing;
                  })
                  .orElseGet(() -> {
                    newData.updateWeatherRegion(region);
                    return newData;
                  })
          )
          .toList();
      weatherDataRepository.saveAll(mergedList);

      region.updateLastCollectedAt();
      weatherRegionRepository.save(region);
    }
  }


}
