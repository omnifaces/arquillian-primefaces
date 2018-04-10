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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class ArquillianPrimeFacesITBean {

	public enum Item {
		VALUE1("Label 1"),
		VALUE2("Label 2"),
		VALUE3("Label 3"),
		VALUE4("Label 4"),
		VALUE5("Label 5");

		private final String label;

		private Item(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private String inputText;
	private Integer inputNumber;
	private Integer spinner;
	private Integer slider;
	private String autoComplete;
	private String selectOneMenu;
	private String selectOneRadio;
	private String selectOneButton;
	private boolean selectBooleanCheckbox;
	private Map<String, Item> selectItems;

	@PostConstruct
	public void init() {
		selectItems = new LinkedHashMap<>();
		selectItems.put("", null);
		Arrays.stream(Item.values()).forEach(item -> selectItems.put(item.getLabel(), item));
	}

	public List<String> completeMethod(String query) {
		return new ArrayList<>(selectItems.keySet());
	}

	public void submit() {
		addGlobalMessage();
	}

	public String submitAndRedirect() {
		addFlashGlobalMessage();
		return getViewIdWithRedirect();
	}

	public void commandLink() {
		addGlobalMessage();
	}

	public void commandLinkWithoutAjax() {
		addGlobalMessage();
	}

	public String commandLinkWithRedirect() {
		addFlashGlobalMessage();
		return getViewIdWithRedirect();
	}

	private void addGlobalMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Serializable> results = new LinkedHashMap<>();
		results.put("inputText", inputText);
		results.put("inputNumber", inputNumber);
		results.put("spinner", spinner);
		results.put("slider", slider);
		results.put("autoComplete", autoComplete);
		results.put("selectOneMenu", selectOneMenu);
		results.put("selectOneRadio", selectOneRadio);
		results.put("selectOneButton", selectOneButton);
		results.put("selectBooleanCheckbox", selectBooleanCheckbox);
		results.put("command", UIComponent.getCurrentComponent(context).getClientId(context));
		context.addMessage(null, new FacesMessage(results.toString()));
	}

	private void addFlashGlobalMessage() {
		addGlobalMessage();
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

	public String getSelectOneButton() {
		return selectOneButton;
	}

	public void setSelectOneButton(String selectOneButton) {
		this.selectOneButton = selectOneButton;
	}

	public Map<String, Item> getSelectItems() {
		return selectItems;
	}

	public boolean isSelectBooleanCheckbox() {
		return selectBooleanCheckbox;
	}

	public void setSelectBooleanCheckbox(boolean selectBooleanCheckbox) {
		this.selectBooleanCheckbox = selectBooleanCheckbox;
	}


}
