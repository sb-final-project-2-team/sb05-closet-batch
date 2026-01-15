package com.codeit.closet.module.weather.repository;

import com.codeit.closet.common.entity.WeatherRegion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRegionRepository extends JpaRepository<WeatherRegion, UUID> {

}
