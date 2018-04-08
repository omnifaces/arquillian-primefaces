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

import static java.lang.System.getProperty;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;

import java.io.Serializable;
import java.net.URL;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.omnifaces.utils.arquillian.ArquillianPrimeFaces;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
public class ArquillianPrimeFacesIT {

	@Deployment(testable=false)
	public static WebArchive createDeployment() {
		String packageName = ArquillianPrimeFacesITBean.class.getPackage().getName();

		return create(WebArchive.class)
			.addClass(ArquillianPrimeFacesITBean.class)
			.addAsLibrary(create(MavenImporter.class).loadPomFromFile("pom.xml").importBuildOutput().as(JavaArchive.class))
			.addAsLibraries(Maven.resolver().resolve("org.primefaces:primefaces:" + getProperty("test.primefaces.version")).withTransitivity().asFile())
			.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
			.addAsWebResource(packageName + "/stateful.xhtml")
			.addAsWebResource(packageName + "/stateless.xhtml")
			.addAsWebResource(packageName + "/form.xhtml");
	}

	@Rule
	public TestRule watcher = new TestWatcher() {
		@Override
		protected void starting(Description description) {
			System.out.println("\nStarting " + description.getMethodName() + " ...");
		}
		@Override
		protected void finished(Description description) {
			System.out.println(description.getMethodName() + " finished!");
		}
	};

	@Drone
	private WebDriver browser;

	@ArquillianResource
	private URL baseURL;

	@FindBy(id="form")
	private WebElement form;

	@FindBy(id="form:inputText")
	private WebElement inputText;

	@FindBy(id="form:inputNumber")
	private WebElement inputNumber;

	@FindBy(id="form:spinner")
	private WebElement spinner;

	@FindBy(id="form:slider")
	private WebElement slider;

	@FindBy(id="form:autoComplete")
	private WebElement autoComplete;

	@FindBy(id="form:selectOneMenu")
	private WebElement selectOneMenu;

	@FindBy(id="form:selectOneRadio")
	private WebElement selectOneRadio;

	@FindBy(id="form:selectOneButton")
	private WebElement selectOneButton;

	@FindBy(id="form:selectBooleanCheckbox")
	private WebElement selectBooleanCheckbox;

	@FindBy(id="form:commandButton")
	private WebElement commandButton;

	@FindBy(id="form:commandButtonWithoutAjax")
	private WebElement commandButtonWithoutAjax;

	@FindBy(id="form:commandButtonWithRedirect")
	private WebElement commandButtonWithRedirect;

	@FindBy(id="form:commandLink")
	private WebElement commandLink;

	@FindBy(id="form:commandLinkWithoutAjax")
	private WebElement commandLinkWithoutAjax;

	@FindBy(id="form:commandLinkWithRedirect")
	private WebElement commandLinkWithRedirect;

	@FindBy(id="form:globalMessages")
	private WebElement globalMessages;

	@FindBy(id="form:absent")
	private WebElement absent;

	@Before
	public void init() {
		ArquillianPrimeFaces.configureTimeouts(browser, Duration.ofSeconds(10));
	}

	@Test
	public void testStatefulWithCommandButton() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandButton(commandButton));
		checkSubmittedValues("commandButton");
	}

	@Test
	public void testStatefulWithCommandButtonWithoutAjax() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandButton(commandButtonWithoutAjax));
		checkSubmittedValues("commandButtonWithoutAjax");
	}

	@Test
	public void testStatefulWithCommandButtonWithRedirect() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandButtonWithRedirect(commandButtonWithRedirect));
		checkSubmittedValues("commandButtonWithRedirect");
	}

	@Test
	public void testStatefulWithCommandLink() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandLink(commandLink));
		checkSubmittedValues("commandLink");
	}

	@Test
	public void testStatefulWithCommandLinkWithoutAjax() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandLink(commandLinkWithoutAjax));
		checkSubmittedValues("commandLinkWithoutAjax");
	}

	@Test
	public void testStatefulWithCommandLinkWithRedirect() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandLinkWithRedirect(commandLinkWithRedirect));
		checkSubmittedValues("commandLinkWithRedirect");
	}

	@Test
	public void testStatelessWithCommandButton() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandButton(commandButton));
		checkSubmittedValues("commandButton");
	}

	@Test
	public void testStatelessWithCommandButtonWithoutAjax() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandButton(commandButtonWithoutAjax));
		checkSubmittedValues("commandButtonWithoutAjax");
	}

	@Test
	public void testStatelessWithCommandButtonWithRedirect() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandButtonWithRedirect(commandButtonWithRedirect));
		checkSubmittedValues("commandButtonWithRedirect");
	}

	@Test
	public void testStatelessWithCommandLink() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandLink(commandLink));
		checkSubmittedValues("commandLink");
	}

	@Test
	public void testStatelessWithCommandLinkWithoutAjax() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandLink(commandLinkWithoutAjax));
		checkSubmittedValues("commandLinkWithoutAjax");
	}

	@Test
	public void testStatelessWithCommandLinkWithRedirect() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
		fillInputValues(() -> ArquillianPrimeFaces.clickCommandLinkWithRedirect(commandLinkWithRedirect));
		checkSubmittedValues("commandLinkWithRedirect");
	}

	@Test
	public void testValidationErrors() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.clickCommandButton(commandButton);
		ArquillianPrimeFaces.assertInvalid(inputText);
		ArquillianPrimeFaces.assertInvalid(inputNumber);
		ArquillianPrimeFaces.assertInvalid(spinner);
		ArquillianPrimeFaces.assertInvalid(slider);
		ArquillianPrimeFaces.assertInvalid(autoComplete);
		ArquillianPrimeFaces.assertInvalid(selectOneMenu);
		ArquillianPrimeFaces.assertInvalid(selectOneRadio);
		ArquillianPrimeFaces.assertInvalid(selectOneButton);
	}

	@Test
	public void testPresence() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertPresent(form);
		ArquillianPrimeFaces.assertPresent(inputText);
		ArquillianPrimeFaces.assertPresent(inputNumber);
		ArquillianPrimeFaces.assertPresent(spinner);
		ArquillianPrimeFaces.assertPresent(slider);
		ArquillianPrimeFaces.assertPresent(autoComplete);
		ArquillianPrimeFaces.assertPresent(selectOneMenu);
		ArquillianPrimeFaces.assertPresent(selectOneRadio);
		ArquillianPrimeFaces.assertPresent(selectOneButton);
		ArquillianPrimeFaces.assertPresent(selectBooleanCheckbox);
		ArquillianPrimeFaces.assertPresent(commandButton);
		ArquillianPrimeFaces.assertPresent(commandButtonWithRedirect);
		ArquillianPrimeFaces.assertPresent(commandLink);
		ArquillianPrimeFaces.assertPresent(commandLinkWithRedirect);
		ArquillianPrimeFaces.assertAbsent(absent);
	}

	private void open(String page) {
		browser.get(baseURL + page);
	}

	private void fillInputValues(Runnable callback) {
		ArquillianPrimeFaces.setInputTextValue(inputText, "input");
		ArquillianPrimeFaces.setInputNumberValue(inputNumber, 1);
		ArquillianPrimeFaces.setSpinnerValue(spinner, 2);
		ArquillianPrimeFaces.setSliderValue(slider, 3);
		String autoCompleteLabel = ArquillianPrimeFaces.setAutoCompleteValue(autoComplete, "query", "Value 1");
		Assert.assertEquals("Value 1", autoCompleteLabel);
		String selectOneMenuLabel = ArquillianPrimeFaces.setSelectOneMenuValue(selectOneMenu, "Value 2");
		Assert.assertEquals("Label 2", selectOneMenuLabel);
		String selectOneRadioLabel = ArquillianPrimeFaces.setSelectOneRadioValue(selectOneRadio, "Value 3");
		Assert.assertEquals("Label 3", selectOneRadioLabel);
		String selectOneButtonLabel = ArquillianPrimeFaces.setSelectOneButtonValue(selectOneButton, "Value 2");
		Assert.assertEquals("Label 2", selectOneButtonLabel);
		ArquillianPrimeFaces.setSelectBooleanCheckboxChecked(selectBooleanCheckbox, true);
		callback.run();
		ArquillianPrimeFaces.assertValid(inputText);
		ArquillianPrimeFaces.assertValid(inputNumber);
		ArquillianPrimeFaces.assertValid(spinner);
		ArquillianPrimeFaces.assertValid(slider);
		ArquillianPrimeFaces.assertValid(autoComplete);
		ArquillianPrimeFaces.assertValid(selectOneMenu);
		ArquillianPrimeFaces.assertValid(selectOneRadio);
		ArquillianPrimeFaces.assertValid(selectOneButton);
	}

	private void checkSubmittedValues(String action) {
		Map<String, Serializable> results = new LinkedHashMap<>();
		results.put("inputText", "input");
		results.put("inputNumber", 1);
		results.put("spinner", 2);
		results.put("slider", 3);
		results.put("autoComplete", "Value 1");
		results.put("selectOneMenu", "Value 2");
		results.put("selectOneRadio", "Value 3");
		results.put("selectOneButton", "Value 2");
		results.put("selectBooleanCheckbox", true);
		results.put("action", action);
		Assert.assertEquals(results.toString(), globalMessages.getText());
	}

}
