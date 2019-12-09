package net.novucs.esd.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/address")
public class AddressLookup {

  private static final String GOOGLE_MAPS_API_KEY =
      "AIzaSyBgLep4XYUU26_O1C5o5NZKF_22w65HOZI";

  private static final String GOOGLE_MAPS_ENDPOINT =
      "https://maps.googleapis.com/maps/api/geocode/json?key="
          + GOOGLE_MAPS_API_KEY + "&sensor=false";

  private static final String LONG_NAME = "long_name";

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getAddressOptions(@QueryParam("pc") String postalCode)
          throws IOException {
    // Setup Postal Code lookup via Google API
    URL postcodeLookup = new URL(GOOGLE_MAPS_ENDPOINT + "&address=" + postalCode);
    JsonObject postcodeResponse = readJsonObject(postcodeLookup);

    // Grab the latitude and longitude
    JsonObject location = postcodeResponse.getJsonArray("results")
            .getJsonObject(0)
            .getJsonObject("geometry")
            .getJsonObject("location");
    String lat = Double.toString(location.getJsonNumber("lat").doubleValue());
    String lng = Double.toString(location.getJsonNumber("lng").doubleValue());

    // Use the latitude and longitude to request additional address information
    URL addressEndpoint = new URL(GOOGLE_MAPS_ENDPOINT + "&latlng=" + lat + "," + lng);
    JsonObject addressResponse = readJsonObject(addressEndpoint);
    
    // Find a valid address component to use
    int componentIndex = 0;
    for (JsonObject jo : addressResponse.getJsonArray("results").getValuesAs(JsonObject.class)) {
      String name = jo.getJsonArray("address_components")
              .getJsonObject(1)
              .getJsonString(LONG_NAME)
              .getString();
      if (!name.matches("[0-9]+")) {
        break;
      }
      componentIndex++;
    }
    
    // Parse the address components for address data
    JsonArray addressComponent = addressResponse.getJsonArray("results")
            .getJsonObject(componentIndex)
            .getJsonArray("address_components");
    
    // Fetch the data from addressEndpoint
    String street = addressComponent
            .getJsonObject(1).getJsonString(LONG_NAME).getString();
    String town = addressComponent
            .getJsonObject(2).getJsonString(LONG_NAME).getString();
    String county = addressComponent
            .getJsonObject(3).getJsonString(LONG_NAME).getString();

    // Concatenate the address data together and return it
    return Json.createObjectBuilder()
            .add("street", street)
            .add("town", town)
            .add("county", county)
            .add("postcode", postalCode.toUpperCase(Locale.ENGLISH))
            .build()
            .toString();
  }

  /**
   * Reads a JSON Object from a Reader.
   *
   * @param lookupUrl The URL to read from
   * @return A JsonObject
   * @throws IOException if an I/O error occurs
   */
  private JsonObject readJsonObject(URL lookupUrl) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(lookupUrl.openStream()));
    JsonObject json;
    try (JsonReader reader = Json.createReader(in)) {
      json = reader.readObject();
    }
    return json;
  }
}
