package com.codeit.closet.module.batch.processor;

import com.codeit.closet.common.entity.WeatherData;
import com.codeit.closet.common.entity.WeatherRegion;
import com.codeit.closet.module.weather.client.KmaApiClient;
import com.codeit.closet.module.weather.converter.KmaApiConverter;
import com.codeit.closet.module.weather.dto.KmaApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherUpdateProcessor implements ItemProcessor<WeatherRegion, List<WeatherData>> {

  private final KmaApiClient kmaApiClient;
  private final KmaApiConverter kmaApiConverter;


  @Override
  public List<WeatherData> process(WeatherRegion weatherRegion) {

    KmaApiResponse response = kmaApiClient.getVilageFcst(
        weatherRegion.getX(), weatherRegion.getY());

    return kmaApiConverter.convertVilageFcst(response, weatherRegion);
  }
}
