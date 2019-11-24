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

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    super.forward(request, response, "Lookup Data", "address");
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getParameter("action") == null) {
      super.forward(request, response, "Lookup Data", "address");
    } else if (request.getParameter("action").equals("lookup")) {
      String address = getAddressOptions(request.getParameter("postcode"));
      request.setAttribute("addressData", Arrays.asList(address));
      super.forward(request, response, "Lookup Data", "address");
    }
  }
  
  private String getAddressOptions(String postCode) {
    // do something u fuckin mong twat
    return "";
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "AddressLookupServlet";
  }

}
