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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class IFPRIEBraryClientAPI extends MetadataClientApi {

  private final String ID_URL = "id";
  private final String COLLECTION_RUL = "collection";

  private final String REST_URL =
    "https://server15738.contentdm.oclc.org/dmwebservices/index.php?q=dmGetItemInfo/{0}/{1}/json";

  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public IFPRIEBraryClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = new MetadataModel();
    JSONObject jo = new JSONObject();
    try {
      String metadata = xmlReaderConnectionUtil.getJsonRestClient(link);
      jo = new JSONObject(metadata);
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
      Gson gson = gsonBuilder.create();
      this.setDefaultEmptyValues(jo);
      List<Author> authors = new ArrayList<Author>();
      if (jo.has("orcid") && jo.get("orcid") != null) {
        try {
          String authorJson = jo.getString("orcid");
          String authorsJson[] = authorJson.split("; ");
          for (String string : authorsJson) {
            string = string.replace(", ", "{0}");
            String div[] = string.split(" ");
            if (div.length == 2) {
              String firstName = div[1].replace("{0}", ", ");
              Author author = new Author(firstName);
              String names[] = author.getFirstName().split(", ");
              if (names.length == 2) {
                author.setFirstName(names[1]);
                author.setLastName(names[0]);
              }
              author.setOrcidId(div[0]);
              authors.add(author);
            }

          }
        } catch (JSONException e) {
          System.err.println("No authors");
        }
      }

      // get description
      if (jo.has("descri") && jo.get("descri") != null) {
        String description = jo.get("descri").toString();
        if (!description.equals("{}")) {
          jo.put("description", description);
        }
      }

      // get citation
      if (jo.has("full") && jo.get("full") != null) {
        String citation = jo.get("full").toString();
        if (!citation.equals("{}")) {
          jo.put("citation", citation);
        }
      }

      // get publisher
      if (jo.has("publis") && jo.get("publis") != null) {
        String publisher = jo.get("publis").toString();
        if (!publisher.equals("{}")) {
          jo.put("publisher", publisher);
        }
      }

      // get rights
      if (jo.has("cclice") && jo.get("cclice") != null) {
        String rights = jo.get("cclice").toString();
        if (!rights.equals("{}")) {
          jo.put("rights", rights);
        }
      }

      // get access
      if (jo.has("access") && jo.get("access") != null) {
        String access = jo.get("access").toString();
        if (!access.equals("{}") && access.equals("Open Access")) {
          jo.put("openAccess", "true");
        }
        if (!access.equals("{}") && access.equals("Restricted")) {
          jo.put("openAccess", "false");
        }
      }

      // get ISI
      if (jo.has("ifpri") && jo.get("ifpri") != null) {
        String ifpri = jo.get("ifpri").toString();
        if (!ifpri.equals("{}") && ifpri.contains("ISI")) {
          jo.put("ISI", "true");
        }
        if (!ifpri.equals("{}") && !ifpri.contains("ISI")) {
          jo.put("ISI", "false");
        }
      }

      // get Journal
      if (jo.has("series") && jo.get("series") != null) {
        String series = jo.get("series").toString();
        if (!series.equals("{}")) {
          jo.put("journal", series);
        }
      }

      // get language
      if (jo.has("langua") && jo.get("langua") != null) {
        String language = jo.get("langua").toString();
        if (!language.equals("{}")) {
          jo.put("language", language);
        }
      }

      // get language
      if (jo.has("loc") && jo.get("loc") != null) {
        String keywords = jo.get("loc").toString();
        if (!keywords.equals("{}")) {
          jo.put("keywords", keywords);
        }
      }

      this.setDoi(jo);

      String data = jo.toString();
      for (String key : coverterAtrributes.keySet()) {
        data = data.replace(key, coverterAtrributes.get(key));
      }
      // Remove empty doi to avoid Json exception
      data = data.replace("\"doi\":{}", "\"doi\":\"\"");
      metadataModel = gson.fromJson(data, MetadataModel.class);
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);

      // get date
      try {
        if (jo.has("date") && jo.get("date") != null) {
          String dateInString = (String) jo.get("date");
          SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
          Date date = formatter.parse(dateInString);
          metadataModel.setPublicationDate(date);
        }
      } catch (ParseException e) {
        System.err.println("Unparseable date");
      }

    } catch (

    Exception e) {
      e.printStackTrace();
      System.err.println(e.getLocalizedMessage());
      jo = null;
    }

    return metadataModel;
  }

  /**
   * with the link evaluate host extract the ido and format into the rest url
   * 
   * @return the link to get the metadata
   */
  @Override
  public String parseLink(String link) {
    String linkRest = "", id = "", collection = "";

    String[] linkSplits = link.split("/");
    int i = 0;
    for (String linkSplit : linkSplits) {
      if (linkSplit.contains(ID_URL)) {
        id = linkSplits[i + 1];
      }
      if (linkSplit.contains(COLLECTION_RUL)) {
        collection = linkSplits[i + 1];
      }
      i++;
    }

    if (id.isEmpty() || collection.isEmpty()) {
      System.err.println(
        "Missing id or collection for IFPRIEBrary link.There was an error collecting data from the url: " + link);
    } else {
      linkRest = (REST_URL.replace("{0}", collection));
      linkRest = (linkRest.replace("{1}", id));
      this.setId(id);
    }

    return linkRest;
  }


}