package com.pcpl.carbon.pcplsdk.Kitkat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventsDataDTO implements Serializable {
    private Long id;
    private  int eventType;
    private Object events;
}
