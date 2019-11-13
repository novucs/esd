package net.novucs.esd.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.StringJoiner;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddressLookupServlet extends BaseServlet {

  private static final String GOOGLE_MAPS_API_KEY =
      "AIzaSyBgLep4XYUU26_O1C5o5NZKF_22w65HOZI";
  private static final String GOOGLE_MAPS_ENDPOINT =
      "https://maps.googleapis.com/maps/api/geocode/json?key="
          + GOOGLE_MAPS_API_KEY + "&sensor=false";

  /**
   * Lookup an Address from a Postal Code and return its full address
   * @param String houseName
   * @param String postalCode
   * @return String
   * @throws IOException
   */
  public String lookupAddress(String houseName, String postalCode) throws IOException {
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

    // Parse the address components for address data
    JsonArray addressComponent = addressResponse.getJsonArray("results")
        .getJsonObject(0)
        .getJsonArray("address_components");

    // Fetch the data from addressEndpoint
    String street = addressComponent
        .getJsonObject(1).getJsonString("long_name").getString();
    String town = addressComponent
        .getJsonObject(2).getJsonString("long_name").getString();
    String county = addressComponent
        .getJsonObject(3).getJsonString("long_name").getString();

    // Concatenate the address data together and return it
    StringJoiner addr = new StringJoiner(", ");
    addr.add(houseName);
    addr.add(street);
    addr.add(town);
    addr.add(county);
    return addr.toString();
  }

  /**
   * Reads a JSON Object from a Reader
   *
   * @param URL postcodeLookup
   * @return JsonObject
   * @throws IOException
   */
  private JsonObject readJsonObject(URL postcodeLookup) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(postcodeLookup.openStream()));
    JsonObject json;
    try (JsonReader reader = Json.createReader(in)) {
      json = reader.readObject();
    }
    return json;
  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest (HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    if (request.getParameter("action") == null) {
      super.forward(request, response, "Lookup Data", "address");
    } else if (request.getParameter("action").equals("lookup")) {
      String address = lookupAddress(request.getParameter("houseno"), request.getParameter("postcode"));
      request.setAttribute("addressData", Arrays.asList(address));
      super.forward(request, response, "Lookup Data", "address");
    }
  }

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet (HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost (HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo () {
    return "Short description";
  }

}
