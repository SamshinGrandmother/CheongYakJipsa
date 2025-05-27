package me.synn3r.jipsa.core.batch.chunk;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.domain.HouseScheduleResponse;
import me.synn3r.jipsa.core.batch.entity.House;
import me.synn3r.jipsa.core.batch.entity.HouseMapper;
import me.synn3r.jipsa.core.batch.repository.HouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplyHomeBatchWriter implements ItemStreamWriter<HouseScheduleResponse> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final HouseMapper houseMapper;
  private final HouseRepository houseRepository;

  @Override
  public void write(Chunk<? extends HouseScheduleResponse> chunk){
    List<House> list = new ArrayList<>();

    for (HouseScheduleResponse houseScheduleResponse : chunk) {
      if (houseRepository.existsByManageNumber(
        Long.parseLong(houseScheduleResponse.getHOUSE_MANAGE_NO()))) {
        continue;
      }
      list.add(houseMapper.toEntity(houseScheduleResponse));
      logger.info("house name : {}", houseScheduleResponse.getHOUSE_NM());
    }

    houseRepository.saveAll(list);
    list.clear();
  }
}
