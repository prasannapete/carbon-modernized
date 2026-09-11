package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.ShellEvents;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShellEventsDataDTO implements Serializable {
  private Long id;
  private  int eventType;
  private Object shellEvents;
}
