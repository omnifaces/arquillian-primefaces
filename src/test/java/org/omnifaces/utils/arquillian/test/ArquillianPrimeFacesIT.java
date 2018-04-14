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
import java.util.function.Consumer;

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
import org.omnifaces.utils.arquillian.Entropy;
import org.omnifaces.utils.arquillian.test.ArquillianPrimeFacesITBean.Item;
import org.openqa.selenium.Dimension;
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
			.addAsWebResource(packageName + "/statefulWithDialog.xhtml")
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

	@Before
	public void init() {
		browser.manage().window().setSize(new Dimension(1600, 1200)); // Necessary to get the form in the dialog to fit within the viewport.
		ArquillianPrimeFaces.configureTimeouts(browser, Duration.ofSeconds(10));
	}

	protected void open(String page) {
		browser.get(baseURL + page);
	}


	// Elements -------------------------------------------------------------------------------------------------------

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

	@FindBy(id="openDialogButton")
	private WebElement openDialogButton;

	@FindBy(id="openDialogForm:openDialogCommandButton")
	private WebElement openDialogCommandButton;


	// Tests ----------------------------------------------------------------------------------------------------------

	@Test
	public void testStatefulWithCommandButton() {
		openStateful();
		fillInputValuesAndSubmit(commandButton, ArquillianPrimeFaces::clickCommandButton);
	}

	@Test
	public void testStatefulWithCommandButtonWithoutAjax() {
		openStateful();
		fillInputValuesAndSubmit(commandButtonWithoutAjax, ArquillianPrimeFaces::clickCommandButton);
	}

	@Test
	public void testStatefulWithCommandButtonWithRedirect() {
		openStateful();
		fillInputValuesAndSubmit(commandButtonWithRedirect, ArquillianPrimeFaces::clickCommandButtonWithRedirect);
	}

	@Test
	public void testStatefulWithCommandLink() {
		openStateful();
		fillInputValuesAndSubmit(commandLink, ArquillianPrimeFaces::clickCommandLink);
	}

	@Test
	public void testStatefulWithCommandLinkWithoutAjax() {
		openStateful();
		fillInputValuesAndSubmit(commandLinkWithoutAjax, ArquillianPrimeFaces::clickCommandLink);
	}

	@Test
	public void testStatefulWithCommandLinkWithRedirect() {
		openStateful();
		fillInputValuesAndSubmit(commandLinkWithRedirect, ArquillianPrimeFaces::clickCommandLinkWithRedirect);
	}

	@Test
	public void testStatelessWithCommandButton() {
		openStateless();
		fillInputValuesAndSubmit(commandButton, ArquillianPrimeFaces::clickCommandButton);
	}

	@Test
	public void testStatelessWithCommandButtonWithoutAjax() {
		openStateless();
		fillInputValuesAndSubmit(commandButtonWithoutAjax, ArquillianPrimeFaces::clickCommandButton);
	}

	@Test
	public void testStatelessWithCommandButtonWithRedirect() {
		openStateless();
		fillInputValuesAndSubmit(commandButtonWithRedirect, ArquillianPrimeFaces::clickCommandButtonWithRedirect);
	}

	@Test
	public void testStatelessWithCommandLink() {
		openStateless();
		fillInputValuesAndSubmit(commandLink, ArquillianPrimeFaces::clickCommandLink);
	}

	@Test
	public void testStatelessWithCommandLinkWithoutAjax() {
		openStateless();
		fillInputValuesAndSubmit(commandLinkWithoutAjax, ArquillianPrimeFaces::clickCommandLink);
	}

	@Test
	public void testStatelessWithCommandLinkWithRedirect() {
		openStateless();
		fillInputValuesAndSubmit(commandLinkWithRedirect, ArquillianPrimeFaces::clickCommandLinkWithRedirect);
	}

	@Test
	public void testStatefulDialogOpenedByButtonWithCommandButton() {
		openStatefulDialogWithButton();
		fillInputValuesAndSubmit(commandButton, ArquillianPrimeFaces::clickCommandButton);
	}

	@Test
	public void testStatefulDialogOpenedByCommandButtonWithCommandButton() {
		openStatefulDialogWithCommandButton();
		fillInputValuesAndSubmit(commandButton, ArquillianPrimeFaces::clickCommandButton);
	}

	@Test
	public void testValidationErrors() {
		openStateful();
		submit(commandButton, ArquillianPrimeFaces::clickCommandButton);
		assertSubmitInvalid();
	}

	@Test
	public void testPresence() {
		openStateful();
		assertPresence();
	}


	// Testers --------------------------------------------------------------------------------------------------------

	protected void openStateful() {
		open("stateful.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
	}

	protected void openStateless() {
		open("stateless.xhtml");
		ArquillianPrimeFaces.assertStateless(form);
	}

	protected void openStatefulDialogWithButton() {
		open("statefulWithDialog.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		ArquillianPrimeFaces.clickButton(openDialogButton);
	}

	protected void openStatefulDialogWithCommandButton() {
		open("statefulWithDialog.xhtml");
		ArquillianPrimeFaces.assertStateful(form);
		ArquillianPrimeFaces.clickCommandButton(openDialogCommandButton);
	}

	protected void submit(WebElement command, Consumer<WebElement> action) {
		action.accept(command);
	}

	protected void fillInputValuesAndSubmit(WebElement command, Consumer<WebElement> action) {
		Map<String, Serializable> inputValues = fillInputValues();
		inputValues.put("command", command.getAttribute("id"));
		submit(command, action);
		assertSubmitValid(inputValues);
	}

	protected Map<String, Serializable> fillInputValues() {
		Map<String, Serializable> inputValues = new LinkedHashMap<>();

		String inputTextValue = Entropy.getRandomString();
		inputValues.put("inputText", inputTextValue);
		ArquillianPrimeFaces.setInputTextValue(inputText, inputTextValue);

		Number inputNumberValue = Entropy.getRandomNumberBetween(1, 100);
		inputValues.put("inputNumber", inputNumberValue);
		ArquillianPrimeFaces.setInputNumberValue(inputNumber, inputNumberValue);

		Number spinnerValue = Entropy.getRandomNumberBetween(1, 100);
		inputValues.put("spinner", spinnerValue);
		ArquillianPrimeFaces.setSpinnerValue(spinner, spinnerValue);

		Number sliderValue = Entropy.getRandomNumberBetween(1, 100);
		inputValues.put("slider", sliderValue);
		ArquillianPrimeFaces.setSliderValue(slider, sliderValue);

		String autoCompleteValue = Entropy.getRandomEnumValue(Item.class).getLabel();
		inputValues.put("autoComplete", autoCompleteValue);
		String autoCompleteLabel = ArquillianPrimeFaces.setAutoCompleteValue(autoComplete, "query", autoCompleteValue);
		Assert.assertEquals(autoCompleteValue, autoCompleteLabel);

		Item selectOneMenuValue = Entropy.getRandomEnumValue(Item.class);
		inputValues.put("selectOneMenu", selectOneMenuValue);
		String selectOneMenuLabel = ArquillianPrimeFaces.setSelectOneMenuValue(selectOneMenu, selectOneMenuValue);
		Assert.assertEquals(selectOneMenuValue.getLabel(), selectOneMenuLabel);

		Item selectOneRadioValue = Entropy.getRandomEnumValue(Item.class);
		inputValues.put("selectOneRadio", selectOneRadioValue);
		String selectOneRadioLabel = ArquillianPrimeFaces.setSelectOneRadioValue(selectOneRadio, selectOneRadioValue);
		Assert.assertEquals(selectOneRadioValue.getLabel(), selectOneRadioLabel);

		Item selectOneButtonValue = Entropy.getRandomEnumValue(Item.class);
		inputValues.put("selectOneButton", selectOneButtonValue);
		String selectOneButtonLabel = ArquillianPrimeFaces.setSelectOneButtonValue(selectOneButton, selectOneButtonValue);
		Assert.assertEquals(selectOneButtonValue.getLabel(), selectOneButtonLabel);

		boolean selectBooleanCheckboxChecked = true;
		inputValues.put("selectBooleanCheckbox", selectBooleanCheckboxChecked);
		ArquillianPrimeFaces.setSelectBooleanCheckboxChecked(selectBooleanCheckbox, selectBooleanCheckboxChecked);

		return inputValues;
	}


	// Assertions -----------------------------------------------------------------------------------------------------

	protected void assertSubmitValid(Map<String, Serializable> inputValues) {
		ArquillianPrimeFaces.assertValid(inputText);
		ArquillianPrimeFaces.assertValid(inputNumber);
		ArquillianPrimeFaces.assertValid(spinner);
		ArquillianPrimeFaces.assertValid(slider);
		ArquillianPrimeFaces.assertValid(autoComplete);
		ArquillianPrimeFaces.assertValid(selectOneMenu);
		ArquillianPrimeFaces.assertValid(selectOneRadio);
		ArquillianPrimeFaces.assertValid(selectOneButton);
		ArquillianPrimeFaces.assertValid(selectBooleanCheckbox);
		Assert.assertEquals(inputValues.toString(), globalMessages.getText());
	}

	protected void assertSubmitInvalid() {
		ArquillianPrimeFaces.assertInvalid(inputText);
		ArquillianPrimeFaces.assertInvalid(inputNumber);
		ArquillianPrimeFaces.assertInvalid(spinner);
		ArquillianPrimeFaces.assertInvalid(slider);
		ArquillianPrimeFaces.assertInvalid(autoComplete);
		ArquillianPrimeFaces.assertInvalid(selectOneMenu);
		ArquillianPrimeFaces.assertInvalid(selectOneRadio);
		ArquillianPrimeFaces.assertInvalid(selectOneButton);
		// Cannot assert checkbox invalid as required=true has no effect there.
	}

	protected void assertPresence() {
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

}
