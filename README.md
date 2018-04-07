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
- `<p:selectOneMenu>` (also with `<p:ajax>`)
- `<p:selectOneRadio>` (also with `<p:ajax>`)
- `<p:selectBooleanCheckbox>`
- `<p:commandButton>` (also with `ajax=false`)
- `<p:commandLink>` (also with `ajax=false`)
- `<p:link>`
