package com.csvsoft.smark;

import com.csvsoft.smark.ui.SmarkAppBuilderUI;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.server.SpringVaadinServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = SmarkAppBuilderUI.class, productionMode = true)
public  class MyUIServlet extends SpringVaadinServlet {

}