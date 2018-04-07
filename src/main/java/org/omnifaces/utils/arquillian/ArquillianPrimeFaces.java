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

import static org.jboss.arquillian.graphene.Graphene.guardAjax;
import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.jboss.arquillian.graphene.Graphene.waitForHttp;
import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.support.ui.ExpectedConditions.attributeContains;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.or;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.io.Serializable;
import java.time.Duration;

import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.arquillian.graphene.proxy.GrapheneProxyInstance;
import org.jboss.arquillian.graphene.spi.configuration.GrapheneConfiguration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class ArquillianPrimeFaces {

	private ArquillianPrimeFaces() {
		throw new AssertionError("This is a utility class.");
	}


	// Configuration ----------------------------------------------------------------------------------------------------------------------

	/**
	 * Allows more flexible programmatic configuration as to wait timeouts.
	 * Graphene#guardXxx() namely doesn't support specifying custom timeouts and defaults to 2~3 seconds which may be too low sometimes.
	 */
	public static void configureTimeouts(WebDriver browser, Duration timeout) {
        while (browser instanceof GrapheneProxyInstance) {
        	browser = ((GrapheneProxyInstance) browser).unwrap();
        }

        GrapheneContext.setContextFor(new GrapheneConfiguration() {
			@Override
			public long getWaitAjaxInterval() {
				return timeout.getSeconds();
			}
			@Override
			public long getWaitGuardInterval() {
				return timeout.getSeconds();
			}
			@Override
			public long getWaitGuiInterval() {
				return timeout.getSeconds();
			}
			@Override
			public long getWaitModelInterval() {
				return timeout.getSeconds();
			}
		}, browser, Default.class);
	}


	// General ----------------------------------------------------------------------------------------------------------------------------

	public static void assertPresent(WebElement element) {
		try {
			assertTrue(element.isDisplayed());
			assertFalse(element.getText(), element.getText().isEmpty());
		}
		catch (NoSuchElementException e) {
			fail(e.toString());
		}
	}

	public static void assertAbsent(WebElement element) {
		try {
			assertFalse(element.isDisplayed());
		}
		catch (NoSuchElementException e) {
			// Expected.
		}
	}


	// Forms ------------------------------------------------------------------------------------------------------------------------------

	public static WebElement getForm(WebElement element) {
		if (element.getTagName().equals("form")) {
			return element;
		}
		else {
			return getForm(getNamingContainer(element));
		}
	}

	private static WebElement getNamingContainer(WebElement element) {
		String clientId = element.getAttribute("id");
		WebElement document = element.findElement(By.xpath("/*"));
		return document.findElement(By.id(clientId.substring(0, clientId.lastIndexOf(':')))); // TODO: get separator character from JS.
	}

	public static String getViewState(WebElement element) {
		return getForm(element).findElement(By.name("javax.faces.ViewState")).getAttribute("value");
	}

	public static void assertStateless(WebElement element) {
		assertEquals("stateless", getViewState(element));
	}

	public static void assertStateful(WebElement element) {
		assertNotEquals("stateless", getViewState(element));
	}


	// Inputs -----------------------------------------------------------------------------------------------------------------------------

	public static boolean isValid(WebElement element) {
		if (element.getAttribute("class").contains("ui-state-default")) { // <p:inputXxx>
			return !element.getAttribute("class").contains("ui-state-error");
		}
		else {
			return isValid(element.findElement(By.cssSelector(".ui-state-default"))); // <p:inputXxx> with special wrapping markup (e.g. inputNumber, spinner, etc)
		}
	}

	public static void assertValid(WebElement element) {
		assertTrue(isValid(element));
	}

	public static void assertInvalid(WebElement element) {
		assertFalse(isValid(element));
	}

	/**
	 * Returns the f:selectItem label associated with new value, may be useful for comparison/logging.
	 */
	public static String setSelectOneMenuValue(WebElement selectOneMenu, Serializable value) {
		String clientId = selectOneMenu.getAttribute("id");
		WebElement document = selectOneMenu.findElement(By.xpath("/*"));
		WebElement input = document.findElement(By.id(clientId + "_input"));
		String itemValue = value.toString();
		String itemLabel = input.findElement(By.cssSelector("option[value='" + itemValue + "']")).getText();
		WebElement selectedOption = input.findElements(By.tagName("option")).stream().filter(o -> input.getText().equals(o.getText())).findFirst().orElse(null);

		if (selectedOption == null || !itemValue.equals(selectedOption.getAttribute("value"))) {
			document.findElement(By.id(clientId + "_label")).click(); // Open panel.
			WebElement panel = document.findElement(By.id(clientId + "_panel"));
			WebElement selectItem = panel.findElement(By.cssSelector(".ui-selectonemenu-item[data-label='" + itemLabel + "']"));

			if (input.getAttribute("onchange") != null) {
				guardAjax(selectItem).click();
			}
			else {
				selectItem.click();
			}
		}

		return itemLabel;
	}

	/**
	 * Returns the f:selectItem label associated with new value, may be useful for comparison/logging.
	 */
	public static String setSelectOneRadioValue(WebElement selectOneRadio, Serializable value) {
		String itemValue = value.toString();
		WebElement input = selectOneRadio.findElement(By.cssSelector("input[value='" + itemValue + "']"));
		String itemLabel = selectOneRadio.findElement(By.cssSelector("label[for='" + input.getAttribute("id") + "']")).getText();
		WebElement selectItem = input.findElement(By.xpath("ancestor::div[contains(@class,'ui-radiobutton')]")).findElement(By.cssSelector(".ui-radiobutton-box"));

		if (!selectItem.getAttribute("class").contains("ui-state-active")) {
			if (input.getAttribute("onchange") != null) {
				guardAjax(selectItem).click();
			}
			else {
				selectItem.click();
			}
		}

		return itemLabel;
	}

	public static void setInputTextValue(WebElement inputText, Serializable value) {
		inputText.clear();
		inputText.sendKeys(value.toString());
	}

	public static void setInputMaskValue(WebElement inputMask, Serializable value) {
		String clientId = inputMask.getAttribute("id");
		executeScript("document.getElementById('" + clientId + "').value='" + value + "'"); // Selenium 3.7.0 bugs here with timing errors on WebElement#sendKeys(), hence JavaScript. TODO: check if it works in a newer Selenium version.
	}

	public static void setInputNumberValue(WebElement inputNumber, Number value) {
		String clientId = inputNumber.getAttribute("id");
		WebElement input = inputNumber.findElement(By.id(clientId + "_input"));
		setInputTextValue(input, String.valueOf(value));
	}

	public static void setSpinnerValue(WebElement spinner, Number value) {
		setInputNumberValue(spinner, value);
	}

	public static void setSliderValue(WebElement slider, Number value) {
		setInputTextValue(slider, value);
	}

	public static void setAutoCompleteQuery(WebElement autoComplete, String query) {
		String clientId = autoComplete.getAttribute("id");
		WebElement input = autoComplete.findElement(By.id(clientId + "_input"));
		setInputTextValue(input, query);
		WebElement document = autoComplete.findElement(By.xpath("/*"));
		WebElement panel = document.findElement(By.id(clientId + "_panel"));
		waitGui().until(or(visibilityOf(panel), attributeContains(input, "class", "ui-state-error")));
	}

	public static void setAutoCompleteValue(WebElement autoComplete, Serializable value) {
		String clientId = autoComplete.getAttribute("id");
		WebElement document = autoComplete.findElement(By.xpath("/*"));
		document.findElement(By.cssSelector("[id='" + clientId + "_panel']")).findElement(By.cssSelector("[data-item-value='" + value + "']")).click(); // Select item.
	}

	public static void setAutoCompleteValue(WebElement autoComplete, String query, Serializable value) {
		setAutoCompleteQuery(autoComplete, query);
		setAutoCompleteValue(autoComplete, value);
	}

	public static void setSelectBooleanCheckboxChecked(WebElement selectBooleanCheckbox, boolean checked) {
		WebElement box = selectBooleanCheckbox.findElement(By.cssSelector(".ui-chkbox-box"));

		if (box.getAttribute("class").contains("ui-state-active")) {
			if (!checked) {
				box.click();
			}
		}
		else if (checked) {
			box.click();
		}
	}


	// Commands ---------------------------------------------------------------------------------------------------------------------------

	public static void clickCommandLink(WebElement commandLink) {
		clickCommandElement(commandLink, false);
	}

	public static void clickCommandLinkWithRedirect(WebElement commandLink) {
		clickCommandElement(commandLink, true);
	}

	public static void clickCommandButton(WebElement commandButton) {
		clickCommandElement(commandButton, false);
	}

	public static void clickCommandButtonWithRedirect(WebElement commandButton) {
		clickCommandElement(commandButton, true);
	}

	private static void clickCommandElement(WebElement command, boolean redirectExpected) {
		waitGui().until(elementToBeClickable(command));

		if (redirectExpected) {
			waitForHttp(command).click();
		}
		else if (command.getAttribute("onclick") != null && command.getAttribute("onclick").contains("PrimeFaces.ab")) {
			guardAjax(command).click();
		}
		else {
			guardHttp(command).click();
		}
	}


	// Links ------------------------------------------------------------------------------------------------------------------------------

	public static void clickLink(WebElement link) {
		waitGui().until(elementToBeClickable(link));

		if ("_blank".equals(link.getAttribute("target"))) {
			link.click();
		}
		else {
			guardHttp(link).click();
		}
	}


	// Helpers ----------------------------------------------------------------------------------------------------------------------------

	private static void executeScript(String script) {
		JavascriptExecutor browser = (JavascriptExecutor) GrapheneContext.getContextFor(Default.class).getWebDriver();
		browser.executeScript(script);
	}

}