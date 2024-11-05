package com.example.bookreadingonline.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper(
      List<Converter<?, ?>> converters,
      List<PropertyMap<?, ?>> propertyMaps
  ) {
    return new ModelMapper() {{
      getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
      converters.forEach(this::addConverter);
      propertyMaps.forEach(this::addMappings);
    }};
  }

}
