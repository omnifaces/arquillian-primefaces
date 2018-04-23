# arquillian-primefaces
To make Arquillian - Graphene - Selenium - JUnit life easier on PrimeFaces components

### Prerequirements

```XML
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.jboss.arquillian.graphene</groupId>
    <artifactId>graphene-webdriver</artifactId>
    <version>2.3.2</version>
    <type>pom</type>
    <scope>test</scope>
</dependency>
```

### Installation

```XML
<dependency>
    <groupId>org.omnifaces</groupId>
    <artifactId>arquillian-primefaces</artifactId>
    <version>0.3</version>
    <scope>test</scope>
</dependency>
```

### Usage example

```XML
<h:form id="form">
    <p:inputText id="inputText" ... />
    <p:inputNumber id="inputNumber" ... />
    <p:spinner id="spinner" ... />
    <p:autoComplete id="autoComplete" ... />
    <p:selectOneMenu id="selectOneMenu" ... />
    <p:selectOneRadio id="selectOneRadio" ... />
    <p:selectBooleanCheckbox id="selectBooleanCheckbox" ... />
    <p:commandButton id="commandButton" ... />
</h:form>
```

```Java
@FindBy(id="form:inputText")
private WebElement inputText;

@FindBy(id="form:inputNumber")
private WebElement inputNumber;

@FindBy(id="form:spinner")
private WebElement spinner;

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

@Test
public void testSomeJSFPageWithPrimeFacesComponents() {
    ArquillianPrimeFaces.setInputTextValue(inputText, "new input text value");
    ArquillianPrimeFaces.setInputNumberValue(inputText, 42);
    ArquillianPrimeFaces.setSpinnerValue(inputText, 7);
    ArquillianPrimeFaces.setAutoCompleteValue(autoComplete, "search query", "option value");
    ArquillianPrimeFaces.setSelectOneMenuValue(selectOneMenu, "option value");
    ArquillianPrimeFaces.setSelectOneRadioValue(selectOneMenu, "option value");
    ArquillianPrimeFaces.setSelectBooleanCheckboxChecked(selectBooleanCheckbox, true);
    ArquillianPrimeFaces.clickCommandButton(commandButton);
    
    // ...
}
```

### Supported PrimeFaces 6.x components as of version 0.3

- `<p:inputText>`
- `<p:inputMask>`
- `<p:inputNumber>`
- `<p:spinner>`
- `<p:slider>`
- `<p:autoComplete>`
- `<p:selectOneMenu>` (also with `<p:ajax>`)
- `<p:selectOneRadio>` (also with `<p:ajax>`)
- `<p:selectOneButton>`
- `<p:selectBooleanCheckbox>`
- `<p:commandButton>` (also with `ajax=false`)
- `<p:commandLink>` (also with `ajax=false`)
- `<p:link>`

NOTE: PrimeFaces 5.x is not necessarily supported. This might be worked on later. Currently, the integration tests run on PrimeFaces 6.0, 6.1 and 6.2.
