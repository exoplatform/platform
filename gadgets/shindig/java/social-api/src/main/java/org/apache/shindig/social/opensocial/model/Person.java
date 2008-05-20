/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.opensocial.model;

import org.apache.shindig.social.AbstractGadgetData;
import org.apache.shindig.social.Mandatory;

import java.util.Date;
import java.util.List;

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Person.Field.html
 *
 */
public class Person extends AbstractGadgetData {

  public static enum Field {
    ABOUT_ME("aboutMe"),
    ACTIVITIES("activities"),
    ADDRESSES("addresses"),
    AGE("age"),
    BODY_TYPE("bodyType"),
    BOOKS("books"),
    CARS("cars"),
    CHILDREN("children"),
    CURRENT_LOCATION("currentLocation"),
    DATE_OF_BIRTH("dateOfBirth"),
    DRINKER("drinker"),
    EMAILS("emails"),
    ETHNICITY("ethnicity"),
    FASHION("fashion"),
    FOOD("food"),
    GENDER("gender"),
    HAPPIEST_WHEN("happiestWhen"),
    HEROES("heroes"),
    HUMOR("humor"),
    ID("id"),
    INTERESTS("interests"),
    JOB_INTERESTS("jobInterests"),
    JOBS("jobs"),
    LANGUAGES_SPOKEN("languagesSpoken"),
    LAST_UPDATED("updated"),
    LIVING_ARRANGEMENT("livingArrangement"),
    LOOKING_FOR("lookingFor"),
    MOVIES("movies"),
    MUSIC("music"),
    NAME("name"),
    NICKNAME("nickname"),
    PETS("pets"),
    PHONE_NUMBERS("phoneNumbers"),
    POLITICAL_VIEWS("politicalViews"),
    PROFILE_SONG("profileSong"),
    PROFILE_URL("profileUrl"),
    PROFILE_VIDEO("profileVideo"),
    QUOTES("quotes"),
    RELATIONSHIP_STATUS("relationshipStatus"),
    RELIGION("religion"),
    ROMANCE("romance"),
    SCARED_OF("scaredOf"),
    SCHOOLS("schools"),
    SEXUAL_ORIENTATION("sexualOrientation"),
    SMOKER("smoker"),
    SPORTS("sports"),
    STATUS("status"),
    TAGS("tags"),
    THUMBNAIL_URL("thumbnailUrl"),
    TIME_ZONE("timeZone"),
    TURN_OFFS("turnOffs"),
    TURN_ONS("turnOns"),
    TV_SHOWS("tvShows"),
    URLS("urls");

    private final String jsonString;

    private Field(String jsonString) {
      this.jsonString = jsonString;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }
  }

  private String aboutMe;
  private List<String> activities;
  private List<Address> addresses;
  private Integer age;
  private BodyType bodyType;
  private List<String> books;
  private List<String> cars;
  private String children;
  private Address currentLocation;
  private Date dateOfBirth;
  private Enum<Enum.Drinker> drinker;
  private List<Email> emails;
  private String ethnicity;
  private String fashion;
  private List<String> food;
  private Enum<Enum.Gender> gender;
  private String happiestWhen;
  private List<String> heroes;
  private String humor;
  private String id;
  private List<String> interests;
  private String jobInterests;
  private List<Organization> jobs;
  private List<String> languagesSpoken;
  private Date updated;
  private String livingArrangement;
  private String lookingFor;
  private List<String> movies;
  private List<String> music;
  private Name name;
  private String nickname;
  private String pets;
  private List<Phone> phoneNumbers;
  private String politicalViews;
  private Url profileSong;
  private String profileUrl;
  private Url profileVideo;
  private List<String> quotes;
  private String relationshipStatus;
  private String religion;
  private String romance;
  private String scaredOf;
  private List<Organization> schools;
  private String sexualOrientation;
  private Enum<Enum.Smoker> smoker;
  private List<String> sports;
  private String status;
  private List<String> tags;
  private String thumbnailUrl;
  private Long timeZone;
  private List<String> turnOffs;
  private List<String> turnOns;
  private List<String> tvShows;
  private List<Url> urls;

  // Note: Not in the opensocial js person object directly
  private boolean isOwner = false;
  private boolean isViewer = false;

  public Person(String id, Name name) {
    this.id = id;
    this.name = name;
  }

  public String getAboutMe() {
    return aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public List<String> getActivities() {
    return activities;
  }

  public void setActivities(List<String> activities) {
    this.activities = activities;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public BodyType getBodyType() {
    return bodyType;
  }

  public void setBodyType(BodyType bodyType) {
    this.bodyType = bodyType;
  }

  public List<String> getBooks() {
    return books;
  }

  public void setBooks(List<String> books) {
    this.books = books;
  }

  public List<String> getCars() {
    return cars;
  }

  public void setCars(List<String> cars) {
    this.cars = cars;
  }

  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }

  public Address getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(Address currentLocation) {
    this.currentLocation = currentLocation;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Enum<Enum.Drinker> getDrinker() {
    return this.drinker;
  }

  public void setDrinker(Enum<Enum.Drinker> newDrinker) {
    this.drinker = newDrinker;
  }

  public List<Email> getEmails() {
    return emails;
  }

  public void setEmails(List<Email> emails) {
    this.emails = emails;
  }

  public String getEthnicity() {
    return ethnicity;
  }

  public void setEthnicity(String ethnicity) {
    this.ethnicity = ethnicity;
  }

  public String getFashion() {
    return fashion;
  }

  public void setFashion(String fashion) {
    this.fashion = fashion;
  }

  public List<String> getFood() {
    return food;
  }

  public void setFood(List<String> food) {
    this.food = food;
  }

  public Enum<Enum.Gender> getGender() {
    return this.gender;
  }

  public void setGender(Enum<Enum.Gender> newGender) {
    this.gender = newGender;
  }

  public String getHappiestWhen() {
    return happiestWhen;
  }

  public void setHappiestWhen(String happiestWhen) {
    this.happiestWhen = happiestWhen;
  }

  public List<String> getHeroes() {
    return heroes;
  }

  public void setHeroes(List<String> heroes) {
    this.heroes = heroes;
  }

  public String getHumor() {
    return humor;
  }

  public void setHumor(String humor) {
    this.humor = humor;
  }

  @Mandatory
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getInterests() {
    return interests;
  }

  public void setInterests(List<String> interests) {
    this.interests = interests;
  }

  public String getJobInterests() {
    return jobInterests;
  }

  public void setJobInterests(String jobInterests) {
    this.jobInterests = jobInterests;
  }

  public List<Organization> getJobs() {
    return jobs;
  }

  public void setJobs(List<Organization> jobs) {
    this.jobs = jobs;
  }

  public List<String> getLanguagesSpoken() {
    return languagesSpoken;
  }

  public void setLanguagesSpoken(List<String> languagesSpoken) {
    this.languagesSpoken = languagesSpoken;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public String getLivingArrangement() {
    return livingArrangement;
  }

  public void setLivingArrangement(String livingArrangement) {
    this.livingArrangement = livingArrangement;
  }

  public String getLookingFor() {
    return lookingFor;
  }

  public void setLookingFor(String lookingFor) {
    this.lookingFor = lookingFor;
  }

  public List<String> getMovies() {
    return movies;
  }

  public void setMovies(List<String> movies) {
    this.movies = movies;
  }

  public List<String> getMusic() {
    return music;
  }

  public void setMusic(List<String> music) {
    this.music = music;
  }

  @Mandatory
  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPets() {
    return pets;
  }

  public void setPets(String pets) {
    this.pets = pets;
  }

  public List<Phone> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<Phone> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public String getPoliticalViews() {
    return politicalViews;
  }

  public void setPoliticalViews(String politicalViews) {
    this.politicalViews = politicalViews;
  }

  public Url getProfileSong() {
    return profileSong;
  }

  public void setProfileSong(Url profileSong) {
    this.profileSong = profileSong;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  public void setProfileUrl(String profileUrl) {
    this.profileUrl = profileUrl;
  }

  public Url getProfileVideo() {
    return profileVideo;
  }

  public void setProfileVideo(Url profileVideo) {
    this.profileVideo = profileVideo;
  }

  public List<String> getQuotes() {
    return quotes;
  }

  public void setQuotes(List<String> quotes) {
    this.quotes = quotes;
  }

  public String getRelationshipStatus() {
    return relationshipStatus;
  }

  public void setRelationshipStatus(String relationshipStatus) {
    this.relationshipStatus = relationshipStatus;
  }

  public String getReligion() {
    return religion;
  }

  public void setReligion(String religion) {
    this.religion = religion;
  }

  public String getRomance() {
    return romance;
  }

  public void setRomance(String romance) {
    this.romance = romance;
  }

  public String getScaredOf() {
    return scaredOf;
  }

  public void setScaredOf(String scaredOf) {
    this.scaredOf = scaredOf;
  }

  public List<Organization> getSchools() {
    return schools;
  }

  public void setSchools(List<Organization> schools) {
    this.schools = schools;
  }

  public String getSexualOrientation() {
    return sexualOrientation;
  }

  public void setSexualOrientation(String sexualOrientation) {
    this.sexualOrientation = sexualOrientation;
  }

  public Enum<Enum.Smoker> getSmoker() {
    return this.smoker;
  }

  public void setSmoker(Enum<Enum.Smoker> newSmoker) {
    this.smoker = newSmoker;
  }

  public List<String> getSports() {
    return sports;
  }

  public void setSports(List<String> sports) {
    this.sports = sports;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public Long getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(Long timeZone) {
    this.timeZone = timeZone;
  }

  public List<String> getTurnOffs() {
    return turnOffs;
  }

  public void setTurnOffs(List<String> turnOffs) {
    this.turnOffs = turnOffs;
  }

  public List<String> getTurnOns() {
    return turnOns;
  }

  public void setTurnOns(List<String> turnOns) {
    this.turnOns = turnOns;
  }

  public List<String> getTvShows() {
    return tvShows;
  }

  public void setTvShows(List<String> tvShows) {
    this.tvShows = tvShows;
  }

  public List<Url> getUrls() {
    return urls;
  }

  public void setUrls(List<Url> urls) {
    this.urls = urls;
  }

  public boolean getIsOwner() {
    return isOwner;
  }

  public void setIsOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  public boolean getIsViewer() {
    return isViewer;
  }

  public void setIsViewer(boolean isViewer) {
    this.isViewer = isViewer;
  }
}
