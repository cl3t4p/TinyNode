package com.cl3t4p.TinyNode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommandRequest {
  private String deviceId;
  private CommandType command;
  private String commandData;

  int calculateByteSize() {
    return Integer.BYTES + commandData.length();
  }
}
