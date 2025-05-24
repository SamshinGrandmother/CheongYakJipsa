package me.synn3r.jipsa.core.config.feign;

import me.synn3r.jipsa.core.batch.domain.HomeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "homeClient", url = "https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc")
public interface HomeClient {

  @GetMapping("/v1/getAPTLttotPblancDetail")
  HomeResponse fetchHome(@RequestParam(name = "page") int page,
    @RequestParam(name = "perPage") int perPage,
    @RequestParam(name = "serviceKey") String serviceKey);

}
