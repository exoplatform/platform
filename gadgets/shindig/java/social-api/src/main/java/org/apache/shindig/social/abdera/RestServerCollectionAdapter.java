/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.shindig.social.abdera;

import org.apache.shindig.social.AbstractGadgetData;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.util.BeanXmlConverter;
import org.apache.shindig.gadgets.GadgetToken;

import com.google.inject.Inject;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * handles logic to create feeds, entries etc.
 *
 */
@SuppressWarnings("unchecked")
public abstract class RestServerCollectionAdapter
    extends AbstractCollectionAdapter {
  private static Logger logger =
    Logger.getLogger(RestServerCollectionAdapter.class.getName());

  private  BeanXmlConverter beanXmlConverter;

  private static final String INVALID_FORMAT =
    "Invalid format. only atom/json-c are supported";

  private enum Format {
    JSON("json-c"),
    ATOM("atom");

    private final String displayValue;

    private Format(String displayValue) {
      this.displayValue = displayValue;
    }

    public String getDisplayValue() {
      return displayValue;
    }
  }

  @Inject public void setBeanXmlConverter(final BeanXmlConverter beanXmlConverter) {
    this.beanXmlConverter = beanXmlConverter;
  }

  protected ResponseContext returnFeed(RequestContext request, String title,
      String author, List<Object> listOfObj) {
    Format format = getFormatTypeFromRequest(request);
    if (format == null) {
      return ProviderHelper.badrequest(request, INVALID_FORMAT);
    }

    Feed feed;
    try {
      feed = createFeedBase(request);
    } catch (ResponseContextException e) {
      logger.warning(e.getMessage());
      return null;
    }
    feed.setBaseUri(request.getUri());
    //TODO set these some other way?
    feed.addAuthor(author);
    feed.setTitle(title);
    // TODO updated should be set to the MAX(updated) of all entries
    feed.setUpdated(new Date());
    feed.setId(request.getUri().toString());

    if (listOfObj != null) {
      // make Entries out of the list  of objects returned above
      for (Object obj : listOfObj) {
        // TODO: how is entry id determined. check.
        String entryId = null;
        Date updated = null;
        if (obj instanceof Person) {
          entryId = request.getUri().toString() + "/" + ((Person)obj).getId();
          updated = ((Person)obj).getUpdated();
        } else if (obj instanceof Activity) {
          entryId = request.getUri().toString() + "/" + ((Activity)obj).getId();
          updated = ((Activity)obj).getUpdated();
        }
        Entry entry = fillEntry(request, obj, entryId, updated, format);
        feed.insertEntry(entry);
      }
    }
    return ProviderHelper.returnBase(feed.getDocument(), 200,
        feed.getUpdated())
          .setEntityTag(ProviderHelper.calculateEntityTag(feed));
  }

  private Entry fillEntry(RequestContext request, Object obj,
      String id, Date updated, Format format) {
    // create entry
    Entry entry = request.getAbdera().newEntry();
    entry.setId(id);
    entry.setUpdated(updated);
    // TODO what should this be?
    entry.addAuthor("Author TODO");
    // TODO what should this be?
    if (obj instanceof Person) {
      entry.setTitle((((Person)obj).getName().getUnstructured() != null) ?
          ((Person)obj).getName().getUnstructured() : "title TODO");
    } else if (obj instanceof Activity) {
      entry.setTitle(((Activity)obj).getTitle());
    } else {
      entry.setTitle("title TODO");
    }

    switch (format) {
      case ATOM:
        entry.setContent(beanXmlConverter.convertToXml(obj),
            "application/xml");
        break;
      case JSON:
        entry.setContent(((AbstractGadgetData)obj).toJson().toString(),
            "application/json");
        break;
    }

    // TODO what is this
    //entry.setSource(feed.getAsSource());
    return entry;
  }

  protected ResponseContext returnEntry(RequestContext request, Object obj,
      String entryId, Date updated) {
    if (obj == null) {
      return ProviderHelper.notfound(request);
    }

    Format format = getFormatTypeFromRequest(request);
    if (format == null) {
      return ProviderHelper.badrequest(request, INVALID_FORMAT);
    }

    Entry entry = fillEntry(request, obj, entryId, updated, format);
    return ProviderHelper.returnBase(entry.getDocument(), 200,
        entry.getEdited())
        .setEntityTag(ProviderHelper.calculateEntityTag(entry));
  }

  /**
   * returns the format (jsoc-c or atom) from the RequestContext obj
   * created by Abdera from the URL request.
   *
   * @param request the RequestContext obj from Abdera
   * @return the format
   */
  private Format getFormatTypeFromRequest(RequestContext request) {
    String format = request.getTarget().getParameter("format");
    logger.fine("format = " + format);

    if (format == null ||
        format.equals(Format.JSON.getDisplayValue())) {
      return Format.JSON;
    } else if (format.equals(Format.ATOM.getDisplayValue())) {
      return Format.ATOM;
    } else {
      return null;
    }
  }

  protected GadgetToken getGadgetToken(RequestContext request,
      final String viewerId) {
    // TODO: Replace this with the real thing
    return new GadgetToken() {
      public String toSerialForm() {
        return "";
      }

      public String getOwnerId() {
        return "";
      }

      public String getViewerId() {
        return viewerId;
      }

      public String getAppId() {
        return "";
      }

      public String getDomain() {
        return "";
      }

      public String getAppUrl() {
        return "";
      }

      public long getModuleId() {
        return 0;
      }
    };
  }

  @Override
  public String getAuthor(RequestContext arg0) {
    //  Auto-generated method stub
    return null;
  }

  @Override
  public String getId(RequestContext arg0) {
    //  Auto-generated method stub
    return null;
  }

  public ResponseContext deleteEntry(RequestContext arg0) {
    //  Auto-generated method stub
    return null;
  }

  public ResponseContext postEntry(RequestContext arg0) {
    //  Auto-generated method stub
    return null;
  }

  public ResponseContext putEntry(RequestContext arg0) {
    //  Auto-generated method stub
    return null;
  }

  public String getTitle(RequestContext arg0) {
    //  Auto-generated method stub
    return null;
  }

}
