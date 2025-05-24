package me.synn3r.jipsa.core.batch.chunk;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.domain.HomeScheduleResponse;
import me.synn3r.jipsa.core.batch.entity.Home;
import me.synn3r.jipsa.core.batch.entity.HomeMapper;
import me.synn3r.jipsa.core.batch.repository.HomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeResponseWriter implements ItemStreamWriter<HomeScheduleResponse> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final HomeMapper homeMapper;
  private final HomeRepository homeRepository;

  @Override
  public void write(Chunk<? extends HomeScheduleResponse> chunk) throws Exception {
    List<Home> list = new ArrayList<>();

    for (HomeScheduleResponse homeScheduleResponse : chunk) {
      if (homeRepository.existsByManageNumber(
        Long.parseLong(homeScheduleResponse.getHOUSE_MANAGE_NO()))) {
        continue;
      }
      list.add(homeMapper.toEntity(homeScheduleResponse));
      logger.info("house name : {}", homeScheduleResponse.getHOUSE_NM());
    }

    homeRepository.saveAll(list);
    list.clear();
  }
}
