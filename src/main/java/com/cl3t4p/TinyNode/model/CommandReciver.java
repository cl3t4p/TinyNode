package com.cl3t4p.TinyNode.model;

public interface CommandReciver {

  /**
   * Take in a commandrequest, encrypt it with the privatekey and spit it out in base64 format
   *
   * @param request Command request to process
   * @return Encrypted base64 of the command request
   */
  byte[] encryptCommandRequest(CommandRequest request);
}
