package net.novucs.esd.test;

import static junit.framework.TestCase.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Test;

public class TestGoogleAPI {

  private static final String GOOGLE_MAPS_API_KEY =
      "AIzaSyBgLep4XYUU26_O1C5o5NZKF_22w65HOZI";
  private static final String GOOGLE_MAPS_ENDPOINT =
      "https://maps.googleapis.com/maps/api/geocode/json?key="
          + GOOGLE_MAPS_API_KEY + "&sensor=false";

  @Test
  public void testGoogleJson() throws IOException {
    URL postcodeLookup = new URL(GOOGLE_MAPS_ENDPOINT + "&address=BS16XN");
    JsonObject postcodeResponse = readJsonObject(postcodeLookup);
    JsonObject location = postcodeResponse.getJsonArray("results").getJsonObject(0)
        .getJsonObject("geometry").getJsonObject("location");
    Double lat = location.getJsonNumber("lat").doubleValue();
    Double lng = location.getJsonNumber("lng").doubleValue();

    URL latLngLookup = new URL(
        GOOGLE_MAPS_ENDPOINT + "&latlng=" + lat.toString() + "," + lng.toString());
    JsonObject latLngResponse = readJsonObject(latLngLookup);
    JsonArray components = latLngResponse.getJsonArray("results").getJsonObject(0)
        .getJsonArray("address_components");

    List<String> match = Arrays
        .asList("Smeaton Road", "Bristol", "City of Bristol", "England", "United Kingdom",
            "BS1 6XN");
    Boolean allMatched = true;
    for (int i = 1; i < components.size(); i++) {
      String streetName = components.getJsonObject(i).getString("long_name");
      if (!streetName.equals(match.get(i - 1))) {
        allMatched = false;
      }
    }

    assertTrue("The address should match the specified match requirements", allMatched);
  }

  private JsonObject readJsonObject(URL postcodeLookup) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(postcodeLookup.openStream()));
    JsonObject json;
    try (JsonReader reader = Json.createReader(in)) {
      json = reader.readObject();
    }
    return json;
  }
}
