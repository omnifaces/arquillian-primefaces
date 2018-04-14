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
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Bauke Scholtz
 */
public final class ArquillianPrimeFaces {

	private ArquillianPrimeFaces() {
		throw new AssertionError("This is a utility class.");
	}


	// Configuration ----------------------------------------------------------------------------------------------------------------------

	/**
	 * Allows more flexible programmatic configuration as to wait timeouts.
	 * Graphene#guardXxx() namely doesn't support specifying custom timeouts and defaults to 2~3 seconds which may be too low sometimes.
	 * Best place to run this is a {@link Before} annotated method.
	 * @param browser The browser.
	 * @param timeout The timeout.
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

	/**
	 * Assert that given element is displayed.
	 * @param element The element to be checked.
	 */
	public static void assertPresent(WebElement element) {
		try {
			assertTrue(element.isDisplayed());
		}
		catch (NoSuchElementException e) {
			fail(e.toString());
		}
	}

	/**
	 * Assert that given element is not displayed.
	 * @param element The element to be checked.
	 */
	public static void assertAbsent(WebElement element) {
		try {
			assertFalse(element.isDisplayed());
		}
		catch (NoSuchElementException e) {
			// Expected.
		}
	}


	// Forms ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the parent form element associated with given form-based element.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 * @return The parent form element.
	 */
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

	/**
	 * Returns the view state hidden input element associated with given form-based element.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 * @return The view state hidden input element associated with given form-based element.
	 */
	public static String getViewState(WebElement element) {
		return getForm(element).findElement(By.name("javax.faces.ViewState")).getAttribute("value");
	}

	/**
	 * Assert that the given form-based element is in a stateless view.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 */
	public static void assertStateless(WebElement element) {
		assertEquals("stateless", getViewState(element));
	}

	/**
	 * Assert that the given form-based element is in a stateful view.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 */
	public static void assertStateful(WebElement element) {
		assertNotEquals("stateless", getViewState(element));
	}


	// Inputs -----------------------------------------------------------------------------------------------------------------------------

	/**
	 * Returns whether given form-based element is valid.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 * @return Whether given form-based element is valid.
	 */
	public static boolean isValid(WebElement element) {
		if (element.getAttribute("class").contains("ui-state-error")) {
			return false;
		}
		else if (element.getAttribute("class").contains("ui-state-default")) { // <p:inputXxx>
			return true;
		}
		else {
			return isValid(element.findElement(By.cssSelector(".ui-state-default"))); // <p:inputXxx> with special wrapping markup (e.g. inputNumber, spinner, etc)
		}
	}

	/**
	 * Assert that the given form-based element is valid.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 */
	public static void assertValid(WebElement element) {
		assertTrue("Must be valid: " + element, isValid(element));
	}

	/**
	 * Assert that the given form-based element is invalid.
	 * @param element The form-based element, e.g. form/input/select/textarea/button, anything which is inside a form.
	 */
	public static void assertInvalid(WebElement element) {
		assertFalse("Must be invalid: " + element, isValid(element));
	}

	/**
	 * Set the selected value of a p:selectOneMenu.
	 * @param selectOneMenu The element representing the p:selectOneMenu.
	 * @param value The selected value. This must match the f:selectItem value (not label!).
	 * @return The f:selectItem label associated with the selected value, may be useful for comparison/logging.
	 */
	public static String setSelectOneMenuValue(WebElement selectOneMenu, Serializable value) {
		String clientId = selectOneMenu.getAttribute("id");
		WebElement document = selectOneMenu.findElement(By.xpath("/*"));
		WebElement input = document.findElement(By.id(clientId + "_input"));
		String itemValue = value.toString();
		String itemLabel = executeScript("return $(document.getElementById('" + input.getAttribute("id") + "')).find('option[value=\"" + itemValue + "\"]').text()"); // getText() doesn't work as option is hidden. It's needed because ui-selectonemenu-item doesn't have a data-value.
		document.findElement(By.id(clientId + "_label")).click(); // Open panel.
		WebElement panel = document.findElement(By.id(clientId + "_panel"));
		WebElement selectItem = panel.findElement(By.cssSelector(".ui-selectonemenu-item[data-label='" + itemLabel + "']"));

		if (input.getAttribute("onchange") != null) {
			guardAjax(selectItem).click();
		}
		else {
			selectItem.click();
		}

		return itemLabel;
	}

	/**
	 * Set the selected value of a p:selectOneRadio.
	 * @param selectOneRadio The element representing the p:selectOneRadio.
	 * @param value The selected value. This must match the f:selectItem value (not label!).
	 * @return The f:selectItem label associated with the selected value, may be useful for comparison/logging.
	 */
	public static String setSelectOneRadioValue(WebElement selectOneRadio, Serializable value) {
		String itemValue = value.toString();
		WebElement input = selectOneRadio.findElement(By.cssSelector("input[value='" + itemValue + "']"));
		String itemLabel = selectOneRadio.findElement(By.cssSelector("label[for='" + input.getAttribute("id") + "']")).getText();
		WebElement findElement = input.findElement(By.xpath("ancestor::div[contains(@class,'ui-radiobutton')]"));
		WebElement selectItem = findElement.findElement(By.cssSelector(".ui-radiobutton-box"));

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

	/**
	 * Set the selected value of a p:selectOneButton.
	 * @param selectOneButton The element representing the p:selectOneButton.
	 * @param value The selected value. This must match the f:selectItem value (not label!).
	 * @return The f:selectItem label associated with the selected value, may be useful for comparison/logging.
	 */
	public static String setSelectOneButtonValue(WebElement selectOneButton, Serializable value) {
		String itemValue = value.toString();
		WebElement input = selectOneButton.findElement(By.cssSelector("input[value='" + itemValue + "']"));
		WebElement selectItem = input.findElement(By.xpath("ancestor::div[contains(@class,'ui-state-default')]"));
		String itemLabel = selectItem.findElement(By.cssSelector(".ui-button-text")).getText();

		if (!selectItem.getAttribute("class").contains("ui-state-active")) {
			selectItem.click();
		}

		return itemLabel;
	}

	/**
	 * Set the value of a p:inputText.
	 * @param inputText The element representing the p:inputText.
	 * @param value The input value.
	 */
	public static void setInputTextValue(WebElement inputText, Serializable value) {
		inputText.clear();
		inputText.sendKeys(value.toString());
	}

	/**
	 * Set the value of a p:inputMask.
	 * @param inputMask The element representing the p:inputMask.
	 * @param value The input value.
	 */
	public static void setInputMaskValue(WebElement inputMask, Serializable value) {
		String clientId = inputMask.getAttribute("id");
		executeScript("document.getElementById('" + clientId + "').value='" + value + "'"); // Selenium 3.7.0 bugs here with timing errors on WebElement#sendKeys(), hence JavaScript. TODO: check if it works in a newer Selenium version.
	}

	/**
	 * Set the value of a p:inputNumber.
	 * @param inputNumber The element representing the p:inputNumber.
	 * @param value The input value.
	 */
	public static void setInputNumberValue(WebElement inputNumber, Number value) {
		String clientId = inputNumber.getAttribute("id");
		WebElement input = inputNumber.findElement(By.id(clientId + "_input"));
		setInputTextValue(input, String.valueOf(value));
	}

	/**
	 * Set the value of a p:spinner.
	 * @param spinner The element representing the p:spinner.
	 * @param value The input value.
	 */
	public static void setSpinnerValue(WebElement spinner, Number value) {
		setInputNumberValue(spinner, value);
	}

	/**
	 * Set the value of a p:inputXxx having a p:slider.
	 * @param slider The element representing the p:inputXxx having a p:slider.
	 * @param value The input value.
	 */
	public static void setSliderValue(WebElement slider, Number value) {
		setInputTextValue(slider, value);
	}

	/**
	 * Set the query of a p:autoComplete.
	 * This should return the available items which you can then select via {@link #setAutoCompleteValue(WebElement, Serializable)}.
	 * Alternatively, you can also use {@link #setAutoCompleteValue(WebElement, String, Serializable)} which does both in a single call.
	 * @param autoComplete The element representing the p:autoComplete.
	 * @param query The query to run auto complete for.
	 */
	public static void setAutoCompleteQuery(WebElement autoComplete, String query) {
		String clientId = autoComplete.getAttribute("id");
		WebElement input = autoComplete.findElement(By.id(clientId + "_input"));
		setInputTextValue(input, query);
		WebElement document = autoComplete.findElement(By.xpath("/*"));
		WebElement panel = document.findElement(By.id(clientId + "_panel"));
		waitGui().until(or(visibilityOf(panel), attributeContains(input, "class", "ui-state-error")));
	}

	/**
	 * Set the selected value of a p:autoComplete.
	 * This should match one of the available items as returned by {@link #setAutoCompleteQuery(WebElement, String)}.
	 * Alternatively, you can also use {@link #setAutoCompleteValue(WebElement, String, Serializable)} which does both in a single call.
	 * @param autoComplete The element representing the p:autoComplete.
	 * @param value The selected value. This must match the item value (not label!).
	 * @return The label associated with the selected value, may be useful for comparison/logging.
	 */
	public static String setAutoCompleteValue(WebElement autoComplete, Serializable value) {
		String clientId = autoComplete.getAttribute("id");
		WebElement document = autoComplete.findElement(By.xpath("/*"));
		WebElement panel = document.findElement(By.id(clientId + "_panel"));
		WebElement selectItem = panel.findElement(By.cssSelector("[data-item-value='" + value + "']"));
		String itemLabel = selectItem.getAttribute("data-item-label");
		selectItem.click();
		return itemLabel;
	}

	/**
	 * Set the query and then the selected value of a p:autoComplete.
	 * @param autoComplete The element representing the p:autoComplete.
	 * @param query The query to run auto complete for.
	 * @param value The selected value. This must match the item value (not label!).
	 * @return The label associated with the selected value, may be useful for comparison/logging.
	 */
	public static String setAutoCompleteValue(WebElement autoComplete, String query, Serializable value) {
		setAutoCompleteQuery(autoComplete, query);
		return setAutoCompleteValue(autoComplete, value);
	}

	/**
	 * Set the checked state of a p:selectBooleanCheckbox.
	 * @param selectBooleanCheckbox The element representing the p:selectBooleanCheckbox.
	 * @param checked The checked state.
	 */
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

	/**
	 * Click a p:commandLink.
	 * @param commandLink The element representing the p:commandLink.
	 */
	public static void clickCommandLink(WebElement commandLink) {
		clickCommandElement(commandLink, false);
	}

	/**
	 * Click a p:commandLink which is expected to perform a HTTP redirect.
	 * @param commandLink The element representing the p:commandLink.
	 */
	public static void clickCommandLinkWithRedirect(WebElement commandLink) {
		clickCommandElement(commandLink, true);
	}

	/**
	 * Click a p:commandButton.
	 * @param commandButton The element representing the p:commandButton.
	 */
	public static void clickCommandButton(WebElement commandButton) {
		clickCommandElement(commandButton, false);
	}

	/**
	 * Click a p:commandButton which is expected to perform a HTTP redirect.
	 * @param commandButton The element representing the p:commandButton.
	 */
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

	/**
	 * Click a p:link.
	 * @param link The element representing the p:link.
	 */
	public static void clickLink(WebElement link) {
		waitGui().until(elementToBeClickable(link));

		if ("_blank".equals(link.getAttribute("target"))) {
			link.click();
		}
		else {
			guardHttp(link).click();
		}
	}

	/**
	 * Click a p:button.
	 * @param button The element representing the p:button.
	 */
	public static void clickButton(WebElement button) {
		waitGui().until(elementToBeClickable(button));
		String onclick = button.getAttribute("onclick");

		if (!onclick.startsWith("window.open") || onclick.endsWith("'_blank')")) {
			button.click();
		}
		else {
			guardHttp(button).click();
		}
	}


	// Helpers ----------------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private static <T> T executeScript(String script) {
		JavascriptExecutor browser = (JavascriptExecutor) GrapheneContext.getContextFor(Default.class).getWebDriver();
		return (T) browser.executeScript(script);
	}

}