/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.deliverables;

import org.cgiar.ccafs.deliverables.model.Author;
import org.cgiar.ccafs.deliverables.model.MetadataModel;
import org.cgiar.ccafs.util.DateTypeAdapter;
import org.cgiar.ccafs.util.RestConnectionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dom4j.Element;
import org.json.JSONObject;

public class CGSpaceClientAPI extends MetadataClientApi {

  private final String CGSPACE_HANDLE = "https://cgspace.cgiar.org/rest/handle/{0}";
  private final String HANDLE_URL = "http://hdl.handle.net/";
  private final String HANDLE_HTTPS_URL = "https://hdl.handle.net/";
  private final String CGSPACE_URL = "https://cgspace.cgiar.org/handle/";
  private final String REST_URL = "https://cgspace.cgiar.org/rest/items/{0}/metadata";
  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public CGSpaceClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
    coverterAtrributes.put("description.abstract", "description");
    coverterAtrributes.put("date.issued", "publicationDate");
    coverterAtrributes.put("language.iso", "language");
    coverterAtrributes.put("subject", "keywords");
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = null;
    JSONObject jo = new JSONObject();
    String countries = "";
    this.setDefaultEmptyValues(jo);

    try {
      Element metadata = xmlReaderConnectionUtil.getXmlRestClient(link);
      List<Author> authors = new ArrayList<Author>();
      List<Element> elements = metadata.elements();
      for (Element element : elements) {

        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);

        if (keyValue.equals("contributor.author")) {
          Author author = new Author(value.getStringValue());
          String names[] = author.getFirstName().split(", ");
          if (names.length == 2) {
            author.setFirstName(names[1]);
            author.setLastName(names[0]);
          }
          authors.add(author);
        } else if (keyValue.equals("identifier.status")) {
          if (value.getStringValue().equals("Open Access")) {
            jo.put("openAccess", "true");
          } else {
            jo.put("openAccess", "false");
          }
        } else if (keyValue.equals("identifier.citation")) {
          jo.put("citation", value.getStringValue());
        } else if (keyValue.equals("identifier.uri")) {
          jo.put("handle", value.getStringValue());
        } else if (keyValue.equals("isijournal")) {
          if (value.getStringValue().contains("ISI")) {
            jo.put("ISI", "true");
          } else {
            jo.put("ISI", "false");
          }
        } else {
          if (jo.has(keyValue) && jo.get(keyValue) != null && jo.get(keyValue) != "") {
            jo.put(keyValue, jo.get(keyValue) + "," + value.getStringValue());
          } else {
            jo.put(keyValue, value.getStringValue());
          }
        }
        if (keyValue.equals("coverage.country")) {
          if (countries.isEmpty()) {
            countries += value.getStringValue();
          } else {
            countries += ", " + value.getStringValue();
          }
        }

      }
      jo.put("countries", countries);
      this.setDoi(jo);

      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
      Gson gson = gsonBuilder.create();
      String data = jo.toString();
      for (String key : coverterAtrributes.keySet()) {
        data = data.replace(key, coverterAtrributes.get(key));
      }
      metadataModel = gson.fromJson(data, MetadataModel.class);
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);

    } catch (

    Exception e) {
      e.printStackTrace();
      System.err.println(e.getLocalizedMessage());

    }
    return metadataModel;

  }

  /**
   * with the link get the id and make a connection to get the Metadata id connnection and format into the rest url
   * 
   * @return the link to get the metadata
   */
  @Override
  public String parseLink(String link) {

    // if the link contains http://hdl.handle.net/ we remove it from the link
    if (link.contains(HANDLE_URL)) {
      this.setId(link.replace(HANDLE_URL, ""));
    }
    // if the link contains https://hdl.handle.net/ we remove it from the link
    if (link.contains(HANDLE_HTTPS_URL)) {
      this.setId(link.replace(HANDLE_HTTPS_URL, ""));
    }
    // if the link https://cgspace.cgiar.org/handle/ we remove it from the link
    if (link.contains(CGSPACE_URL)) {
      this.setId(link.replace(CGSPACE_URL, ""));
    }

    String handleUrl = CGSPACE_HANDLE.replace("{0}", this.getId());
    RestConnectionUtil connection = new RestConnectionUtil();
    Element elementHandle = connection.getXmlRestClient(handleUrl);
    this.setId(elementHandle.element("id").getStringValue());
    String linkRest = (REST_URL.replace("{0}", this.getId()));
    return linkRest;
  }
}