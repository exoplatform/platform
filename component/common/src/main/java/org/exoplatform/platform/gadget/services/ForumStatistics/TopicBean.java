/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
package org.exoplatform.platform.gadget.services.ForumStatistics;

import java.util.Date;

/**
 * Forums Weekly Statistic Bean.
 * 
 * @author <a href="tungdt@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */
public class TopicBean {

  private String id;
  
  private String owner;                 //author of topic

  private Date   createDate;            // the datetime when create

  private String   title;               // Title of topic

  private Double   voteRating;           // voteRating

  private String link;
  
  private int numberOfUserVoteRating;

  public TopicBean() {
  }
  
  public TopicBean(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }



  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Double getVoteRating() {
    return voteRating;
  }

  public void setVoteRating(Double voteRating) {
    this.voteRating = voteRating;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public int getNumberOfUserVoteRating() {
    return numberOfUserVoteRating;
  }

  public void setNumberOfUserVoteRating(int numberOfUserVoteRating) {
    this.numberOfUserVoteRating = numberOfUserVoteRating;
  }

  
  
}
