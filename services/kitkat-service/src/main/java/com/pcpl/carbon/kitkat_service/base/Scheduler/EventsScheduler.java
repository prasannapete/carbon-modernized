package com.pcpl.carbon.kitkat_service.base.Scheduler;

import com.pcpl.carbon.kitkat_service.KitkatEvents.Service.KitkatEventsService;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Kitkat.DTO.EventsDataDTO;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class EventsScheduler {
    @Autowired
    KitkatEventsService eventsService;

    @Autowired
    RedisTemplate<String, EventsDataDTO> redisTemplate;

    // Enabled by default (production unchanged). Set KITKAT_EVENTS_SCHEDULER_ENABLED=false
    // to turn this Redis-backed import off for local dev when no Redis is running.
    @Value("${KITKAT_EVENTS_SCHEDULER_ENABLED:true}")
    private boolean schedulerEnabled;

    @Scheduled(fixedDelay = 10000)
    @SchedulerLock(name = "KitKatEventsScheduler")
    public void importEventsFromRedis() {
        if (!schedulerEnabled) {
            return;
        }
        try {
            String key = "kitkat_events";
            ApplicationResponse applicationResponse = eventsService.getObject(key);
            List<EventsDataDTO> eventsDataDTOS = (List<EventsDataDTO>) applicationResponse.getData();
            if(eventsDataDTOS != null && !eventsDataDTOS.isEmpty()) {
                for(EventsDataDTO eventsDataDTO : eventsDataDTOS) {
                    ApplicationResponse applicationResponse1 = eventsService.saveEvents(eventsDataDTO);
                    if(applicationResponse1.isSuccess()){
                        redisTemplate.opsForList().leftPop(key);
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
