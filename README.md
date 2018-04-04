# arquillian-primefaces
To make Arquillian - Graphene - Selenium - JUnit life easier on PrimeFaces components

### Installation

```XML
<dependency>
    <groupId>org.omnifaces</groupId>
    <artifactId>arquillian-primefaces</artifactId>
    <version>0.1</version>
    <scope>test</scope>
</dependency>
```

### Usage example

```XML
<h:form id="form">
    <p:inputText id="inputText" ... />
    <p:autoComplete id="autoComplete" ... />
    <p:selectOneMenu id="selectOneMenu" ... />
    <p:selectBooleanCheckbox id="selectBooleanCheckbox" ... />
    <p:commandButton id="commandButton" ... />
</h:form>
```

```Java
@FindBy(id="form:inputText")
private WebElement inputText;

@FindBy(id="form:autoComplete")
private WebElement autoComplete;

@FindBy(id="form:selectOneMenu")
private WebElement selectOneMenu;

@FindBy(id="form:selectBooleanCheckbox")
private WebElement selectBooleanCheckbox;

@FindBy(id="form:commandButton")
private WebElement commandButton;

@Test
public void testSomeJSFPageWithPrimeFacesComponents() {
    ArquillianPrimeFaces.setInputTextValue(inputText, "new input text value");
    ArquillianPrimeFaces.setAutoCompleteValue(autoComplete, "search query", "selected value");
    ArquillianPrimeFaces.setSelectOneMenuValue(selectOneMenu, "option value");
    ArquillianPrimeFaces.selectBooleanCheckboxChecked(selectBooleanCheckbox, true);
    ArquillianPrimeFaces.clickCommandButton(commandButton);
    
    // ...
}
```

### Supported PrimeFaces components as of version 0.1

- `<p:inputText>`
- `<p:inputMask>`
- `<p:inputNumber>`
- `<p:spinner>`
- `<p:slider>`
- `<p:autoComplete>`
- `<p:selectOneMenu>`
- `<p:selectOneRadio>`
- `<p:selectBooleanCheckbox>`
- `<p:commandButton>`
- `<p:commandLink>`
- `<p:link>`
