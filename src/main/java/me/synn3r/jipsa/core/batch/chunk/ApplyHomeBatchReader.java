package me.synn3r.jipsa.core.batch.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.domain.HouseResponse;
import me.synn3r.jipsa.core.batch.domain.HouseScheduleResponse;
import me.synn3r.jipsa.core.config.feign.HouseClient;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplyHomeBatchReader implements ItemStreamReader<HouseScheduleResponse> {

  private final HouseClient houseClient;
  private final List<HouseScheduleResponse> scheduleResponses = new ArrayList<>();
  @Value("${dataApi.applyHome.apiKey}")
  private String secretKey;
  private final int PAGE_SIZE = 100;
  private final int INIT_PAGE = 0;
  private int currentPage = INIT_PAGE;
  private boolean isEnd = false;

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    currentPage = INIT_PAGE;
    isEnd = false;
    scheduleResponses.clear();
  }

  @Override
  public void close() throws ItemStreamException {
    scheduleResponses.clear();
  }

  @Override
  public HouseScheduleResponse read() {
     if (scheduleResponses.isEmpty()) {
       if (isEnd) {
         return null;
       }

      fetchNextPage();
      return read();
    }


    return scheduleResponses.removeFirst();
  }

  protected void fetchNextPage() {
    currentPage++;
    houseClient.fetchHome(currentPage, PAGE_SIZE, secretKey);
    HouseResponse response = houseClient.fetchHome(currentPage, PAGE_SIZE, secretKey);
    scheduleResponses.addAll(
      Optional.ofNullable(response.getData()).orElse(Collections.emptyList()));

    if (response.getData().size() < PAGE_SIZE) {
      isEnd = true;
    }
  }
}
