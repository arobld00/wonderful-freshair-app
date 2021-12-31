package com.wonderful.freshair.domain

import arrow.core.Either
import arrow.core.computations.option
import com.wonderful.freshair.infrastructure.City
import java.math.BigDecimal
import java.math.RoundingMode

data class AirQualityIndex(val cityName : String, val doubleIndex: Double) {
    val index: BigDecimal = BigDecimal(doubleIndex)
        .setScale(2, RoundingMode.HALF_UP)
}

class CityAirQualityService(
  private val cityGeocodingService: CityGeoCodingService,
  private val airQualityForecastService: AirQualityForecastService
) {
    fun averageIndex(city: City): Either<ApplicationError, AirQualityIndex> = option.eager<AirQualityIndex> {
        val (_, _, coordinates) = cityGeocodingService.getGeoCoordinates(city).bind()
        val airQualityForecasts = airQualityForecastService.getAirQualityForecast(coordinates).bind()
        AirQualityIndex(
            city.name,
            airQualityForecasts.map { forecast -> forecast.index }.average()
        )
    }.toEither { ApplicationError }

}
