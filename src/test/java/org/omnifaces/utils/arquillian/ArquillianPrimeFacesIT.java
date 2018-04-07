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
package org.omnifaces.utils.arquillian;

import static java.lang.System.getProperty;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;

import java.net.URL;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
public class ArquillianPrimeFacesIT {

	@Deployment(testable=false)
	public static WebArchive createDeployment() {
		return create(WebArchive.class)
			.addClass(ArquillianPrimeFacesITBean.class)
			.addAsLibrary(create(MavenImporter.class).loadPomFromFile("pom.xml").importBuildOutput().as(JavaArchive.class))
			.addAsLibraries(Maven.resolver().resolve("org.primefaces:primefaces:" + getProperty("test.primefaces.version")).withTransitivity().asFile())
			.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
			.addAsWebResource(ArquillianPrimeFacesIT.class.getSimpleName() + ".xhtml");
	}

	@Drone
	private WebDriver browser;

	@ArquillianResource
	private URL baseURL;

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

	@FindBy(id="form:selectBooleanCheckbox")
	private WebElement selectBooleanCheckbox;

	@FindBy(id="form:commandButton")
	private WebElement commandButton;

	@FindBy(id="inputTextResult")
	private WebElement inputTextResult;

	@FindBy(id="inputNumberResult")
	private WebElement inputNumberResult;

	@FindBy(id="spinnerResult")
	private WebElement spinnerResult;

	@FindBy(id="sliderResult")
	private WebElement sliderResult;

	@FindBy(id="autoCompleteResult")
	private WebElement autoCompleteResult;

	@FindBy(id="selectOneMenuResult")
	private WebElement selectOneMenuResult;

	@FindBy(id="selectOneRadioResult")
	private WebElement selectOneRadioResult;

	@FindBy(id="selectBooleanCheckboxResult")
	private WebElement selectBooleanCheckboxResult;

	@FindBy(id="commandButtonResult")
	private WebElement commandButtonResult;

	@Test
	public void test() {
		browser.get(baseURL + ArquillianPrimeFacesIT.class.getSimpleName() + ".xhtml");

	    ArquillianPrimeFaces.setInputTextValue(inputText, "input");
	    ArquillianPrimeFaces.setInputNumberValue(inputNumber, 1);
	    ArquillianPrimeFaces.setSpinnerValue(spinner, 2);
	    ArquillianPrimeFaces.setSliderValue(slider, 3);
	    ArquillianPrimeFaces.setAutoCompleteValue(autoComplete, "query", "Value 1");
	    ArquillianPrimeFaces.setSelectOneMenuValue(selectOneMenu, "Value 2");
	    ArquillianPrimeFaces.setSelectOneRadioValue(selectOneRadio, "Value 3");
	    ArquillianPrimeFaces.setSelectBooleanCheckboxChecked(selectBooleanCheckbox, true);
	    ArquillianPrimeFaces.clickCommandButton(commandButton);

		Assert.assertEquals("input", inputTextResult.getText());
		Assert.assertEquals("1", inputNumberResult.getText());
		Assert.assertEquals("2", spinnerResult.getText());
		Assert.assertEquals("3", sliderResult.getText());
		Assert.assertEquals("Value 1", autoCompleteResult.getText());
		Assert.assertEquals("Value 2", selectOneMenuResult.getText());
		Assert.assertEquals("Value 3", selectOneRadioResult.getText());
		Assert.assertEquals("true", selectBooleanCheckboxResult.getText());
		Assert.assertEquals("invoked", commandButtonResult.getText());
	}

}
