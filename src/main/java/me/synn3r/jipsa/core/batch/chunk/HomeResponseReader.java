package me.synn3r.jipsa.core.batch.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.domain.HomeResponse;
import me.synn3r.jipsa.core.batch.domain.HomeScheduleResponse;
import me.synn3r.jipsa.core.config.feign.HomeClient;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeResponseReader implements ItemStreamReader<HomeScheduleResponse> {

  private final HomeClient homeClient;
  private final List<HomeScheduleResponse> scheduleResponses = new ArrayList<>();
  @Value("${dataApi.applyHome.apiKey}")
  private String secretKey;

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    scheduleResponses.clear();
    HomeResponse response = homeClient.fetchHome(1, 10, secretKey);
    scheduleResponses.addAll(
      Optional.ofNullable(response.getData()).orElse(Collections.emptyList()));
  }

  @Override
  public void close() throws ItemStreamException {
    scheduleResponses.clear();
  }

  @Override
  public HomeScheduleResponse read() {
    if (scheduleResponses.isEmpty()) {
      return null;
    }

    return scheduleResponses.removeFirst();
  }
}
