package net.novucs.esd.controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.*;

public class AddressLookupServlet extends BaseServlet {

  private static final String GOOGLE_MAPS_API_KEY =
      "AIzaSyBgLep4XYUU26_O1C5o5NZKF_22w65HOZI";
  private static final String GOOGLE_MAPS_ENDPOINT =
      "https://maps.googleapis.com/maps/api/geocode/json?key="
          + GOOGLE_MAPS_API_KEY + "&sensor=false";

  private String lookupAddress(String houseName, String postalCode) throws IOException {
    // Do first lat & long search from post code
    // TODO: Attempt to use MapBox API?
    URL postCodeEndpoint = new URL(GOOGLE_MAPS_ENDPOINT + "&address=" + postalCode);
    InputStreamReader reader = new InputStreamReader(postCodeEndpoint.openStream());
    HashMap<String, String> data = new Gson().fromJson(reader, HashMap.class);

    for (Map.Entry<String, String> address : data.entrySet()) {
      System.out.println("---------------------");
      System.out.println(address.getKey());
      System.out.println(address.getValue());
      System.out.println("---------------------");
      break;
    }

    return "Bruh";

    /*
    // Get data from postCodeEndpoint and convert to JSON Object [?]
    // The below examples are grabbing just from `.results[0]`, we could fetch all
    // but this would then cause multiple requests... possibly? @refactor @review
    String lat = "53.0234732";  // receivedData.results[0].geometry.location.lat
    String lng = "-3.34328743"; // receivedData.results[0].geometry.location.lng

    // Use the above returned lat,lng to get the addresses
    URL addressEndpoint = new URL(GOOGLE_MAPS_ENDPOINT + "&latlng=" + lat + "," + lng);

    // Fetch the data from addressEndpoint
    // addressData.results[0].address_components;
    List<String> address = Arrays.asList("Blah", "Bruh", "Bleh");
    String street = ""; // address[1].long_name
    String town = ""; // address[2].long_name
    String county = ""; // address[3].long_name

    StringJoiner addr = new StringJoiner(", ");
    addr.add(houseName);
    addr.add(street);
    addr.add(town);
    addr.add(county);
    return addr.toString();*/
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
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // This page is limited to logged in members
    /*Session session = super.getSession(request);
    if (session.getUser() == null) {
      response.sendRedirect("login");
      return;
    }*/
    if (request.getParameter("action") == null) {
      super.forward(request, response, "Lookup Data", "address");
    } else if (request.getParameter("action").equals("lookup")) {
      lookupAddress(
          request.getParameter("houseno"),
          request.getParameter("postcode")
      );
      request.setAttribute("addressData", Arrays.asList(
          "1 Bristol Lane",
          "2 Bristol Lane",
          "3 Bristol Lane",
          "4 Bristol Lane",
          "5 Bristol Lane")
      );
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
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }

}
