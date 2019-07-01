package org.exoplatform.platform.common.branding;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Logo {
  private String uploadId;

  private long   size;

  private byte[] data;

  private long updatedDate;

  public Logo() {
  }

  public Logo(String uploadId, byte[] data, long size, long updatedDate) {
    this.uploadId = uploadId;
    this.size = size;
    this.data = data;
    this.updatedDate = updatedDate;
  }

  public String getUploadId() {
    return uploadId;
  }

  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public long getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(long updatedDate) {
    this.updatedDate = updatedDate;
  }
}
