/*
 * Copyright 2018 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.arquillian.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class ArquillianPrimeFacesITBean {

	private String inputText;
	private Integer inputNumber;
	private Integer spinner;
	private Integer slider;
	private String autoComplete;
	private String selectOneMenu;
	private String selectOneRadio;
	private boolean selectBooleanCheckbox;
	private Map<String, String> selectItems;

	@PostConstruct
	public void init() {
		selectItems = new LinkedHashMap<>();
		selectItems.put("", "");
		selectItems.put("Label 1", "Value 1");
		selectItems.put("Label 2", "Value 2");
		selectItems.put("Label 3", "Value 3");
	}

	public List<String> completeMethod(String query) {
		return new ArrayList<>(selectItems.values());
	}

	public void commandButton() {
		addGlobalMessage("commandButton");
	}

	public String commandButtonWithRedirect() {
		addFlashGlobalMessage("commandButtonWithRedirect");
		return getViewIdWithRedirect();
	}

	public void commandLink() {
		addGlobalMessage("commandLink");
	}

	public String commandLinkWithRedirect() {
		addFlashGlobalMessage("commandLinkWithRedirect");
		return getViewIdWithRedirect();
	}

	private void addGlobalMessage(String action) {
		Map<String, Serializable> results = new LinkedHashMap<>();
		results.put("inputText", inputText);
		results.put("inputNumber", inputNumber);
		results.put("spinner", spinner);
		results.put("slider", slider);
		results.put("autoComplete", autoComplete);
		results.put("selectOneMenu", selectOneMenu);
		results.put("selectOneRadio", selectOneRadio);
		results.put("selectBooleanCheckbox", selectBooleanCheckbox);
		results.put("action", action);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(results.toString()));
	}

	private void addFlashGlobalMessage(String globalMessage) {
		addGlobalMessage(globalMessage);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
	}

	private String getViewIdWithRedirect() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true";
	}

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public Integer getInputNumber() {
		return inputNumber;
	}

	public void setInputNumber(Integer inputNumber) {
		this.inputNumber = inputNumber;
	}

	public Integer getSlider() {
		return slider;
	}

	public void setSlider(Integer slider) {
		this.slider = slider;
	}

	public Integer getSpinner() {
		return spinner;
	}

	public void setSpinner(Integer spinner) {
		this.spinner = spinner;
	}

	public String getAutoComplete() {
		return autoComplete;
	}

	public void setAutoComplete(String autoComplete) {
		this.autoComplete = autoComplete;
	}

	public String getSelectOneMenu() {
		return selectOneMenu;
	}

	public void setSelectOneMenu(String selectOneMenu) {
		this.selectOneMenu = selectOneMenu;
	}

	public String getSelectOneRadio() {
		return selectOneRadio;
	}

	public void setSelectOneRadio(String selectOneRadio) {
		this.selectOneRadio = selectOneRadio;
	}

	public Map<String, String> getSelectItems() {
		return selectItems;
	}

	public boolean isSelectBooleanCheckbox() {
		return selectBooleanCheckbox;
	}

	public void setSelectBooleanCheckbox(boolean selectBooleanCheckbox) {
		this.selectBooleanCheckbox = selectBooleanCheckbox;
	}


}
