package automation;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rendersnake.HtmlCanvas;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cisco.expressions.ExpressionEvaluator;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Function;

public class WebApp {

	HtmlCanvas html = new HtmlCanvas();
	
	public String browserType = "";
	
	DataObject dataObject = new DataObject();

	static WebDriver driver;
	
	public static WebDriver getDriver() {
		return driver;
	}
	
	ExcelDocument excelDocument;
	
	Logger logger = Logger.getRootLogger();
	
	String codeSnippetClass = "";
	
	XPath xPath = XPathFactory.newInstance().newXPath();
	
	ExpressionEvaluator expressionEvalutor = new ExpressionEvaluator();
	
	String winHandleBefore = "";
	
	static String xmlContent;
	
	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	Reporter reporter = new Reporter();
	
	public WebApp() {
		initApp();
	}
	
	public WebApp(String browserType) {
		this.browserType = browserType;
		initApp();
	}
	
	public String getCodeSnippetClass() {
		return codeSnippetClass;
	}

	public void setCodeSnippetClass(String codeSnippetClass) {
		this.codeSnippetClass = codeSnippetClass;
	}
	
	public void initApp() {
//		System.setProperty("webdriver.firefox.bin", "C:\\FF_NEW\\firefox.exe");
		if(browserType.equalsIgnoreCase("FireFox")){
//			FirefoxProfile firefoxProfile = new FirefoxProfile();
//			try {
//				firefoxProfile.addExtension(new File("extra\\firefoxplugins\\firebug-2.0-fx.xpi"));
//				firefoxProfile.addExtension(new File("extra\\firefoxplugins\\firepath-0.9.7-fx.xpi"));
//				firefoxProfile.addExtension(new File("extra\\firefoxplugins\\execute_js-0.2.4-fx+tb.xpi"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////			driver = new FirefoxDriver(firefoxProfile);
			System.setProperty("webdriver.firefox.bin", "C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
		} else if(browserType.equalsIgnoreCase("Html")) {
//			driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17) {
//				@Override
//				public void setJavascriptEnabled(boolean enableJavascript) {
//					// TODO Auto-generated method stub
//					super.setJavascriptEnabled(true);
//				}
//			};
		} else if(browserType.equalsIgnoreCase("PhantomJS")) {
			driver = new PhantomJSDriver();
		} else if(browserType.equalsIgnoreCase("service")) {
			
		} else {
			FirefoxProfile firefoxProfile = new FirefoxProfile();
//			try {
//				firefoxProfile.addExtension(new File("extra\\firefoxplugins\\firebug-2.0-fx.xpi"));
//				firefoxProfile.addExtension(new File("extra\\firefoxplugins\\firepath-0.9.7-fx.xpi"));
//				firefoxProfile.addExtension(new File("extra\\firefoxplugins\\execute_js-0.2.4-fx+tb.xpi"));
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			FirefoxBinary binary = new FirefoxBinary(new File("C:/Program Files (x86)/Mozilla Firefox/firefox.exe"));
			driver = new FirefoxDriver(binary,firefoxProfile);
//			driver = new FirefoxDriver();
			driver.manage().window().maximize();
		}
		excelDocument = new ExcelDocument();
		excelDocument.setDirecotyPath("");
		excelDocument.setWorkingFileName("Automation.xlsx");
		excelDocument.initiateFileInstance();
		excelDocument.selectSheet("Methods");
	}
	
	/**
     * Parsing object recursively
     * @param rootObject
     */
    @SuppressWarnings({ })
	public String parseObject(String pathString,Object rootObject) {
    	String stringInformation = null;
    	@SuppressWarnings("rawtypes")
    	Class cls = rootObject.getClass();
		Field[] fieldList = cls.getDeclaredFields();
		if (cls.getSimpleName().contains("String")) {

		} else {
			for (Field field : fieldList) {
				try {
					
					String objectType = field.getType().toString();
					if(objectType.contains("boolean")) {
						if(field.getName().equalsIgnoreCase(pathString)) {
							boolean booleanValue = (Boolean) field.get(rootObject);
							stringInformation = String.valueOf(booleanValue);
						}
					} else if(objectType.contains("int")) {
						if(field.getName().equalsIgnoreCase(pathString)) {
							int intVlaue = (Integer) field.get(rootObject);
							stringInformation = String.valueOf(intVlaue);
						}
					} else if(objectType.contains("float")) {
						if(field.getName().equalsIgnoreCase(pathString)) {
							float floatValue = (Integer) field.get(rootObject);
							stringInformation = String.valueOf(floatValue);
						}
					}else if(objectType.contains("Optional")) {

						Object optionalObject = field.get(rootObject);
						if(field.getName().equalsIgnoreCase(pathString)) {
							// Apply reflection on this!
							// See the list of methods!
							Method[] methods = optionalObject.getClass().getDeclaredMethods();
							
							boolean isPresentFlag = false;
							for(int count = 0; count<methods.length; count++){
								Method method = methods[count];
								if(isPresentFlag) {
									if(method.getName().contains("getValue")){
										try {
											method.setAccessible(true);
											Object object = (Object) method.invoke(optionalObject, null);
											if(object!=null){
												stringInformation = parseObject(pathString, object);
											}
											break;
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								} else {
									if(method.getName().contains("ispresent")){
										try {
											method.setAccessible(true);
											isPresentFlag = (Boolean) method.invoke(optionalObject, null);
											if(isPresentFlag){
												count = 0;
											} else{
												break;
											}
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
								count++;
							}
						}
						
					} else if (objectType.contains("[Ljava.lang.String")) {
	
						String[] stringObjects = (String[]) field.get(rootObject);
						if(stringObjects != null) {
							int i = 1;
							for(String string:stringObjects) {
								stringInformation = string;
								i++;
							}
						}

					} else if (objectType.contains("[L")) {
						
						Object[] arrayObjects = (Object[]) field
								.get(rootObject);
						if(arrayObjects != null) {
							for (Object object : arrayObjects) {
								stringInformation = parseObject(pathString, object);
							}
						}
						
					} else if (objectType.contains("java.lang.String")) {
						if(field.getName().equalsIgnoreCase(pathString)) {
							String value = (String) field.get(rootObject);
							if(value != null){
								stringInformation = value;
							}
						}
					} else {
						Object object = field.get(rootObject);
						stringInformation = parseObject(pathString,object);
					}
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stringInformation;
    }
	
    /**
     * having actual mechanism
     * <img src="..\ss\\1388472059520.png">
     * @param methodName - used to run excel method
     * @param genericObject
     */
	public void runApplication(String methodName, Object genericObject) {
		
		long milliSecondsBefore = System.currentTimeMillis();
//		HashMap<String, String> hashMap = new HashMap<String, String>();
		try {
			Class codeSnippetCls = Class.forName(codeSnippetClass);
			Object codeSnippetObject = codeSnippetCls.newInstance();
			
			boolean isMethodActivated = false;
			for(Row row:excelDocument.getSheet()) {
				String cellValue = excelDocument.getValueFromExcel(row.getRowNum(), 0);
				if (isMethodActivated && cellValue.length() > 0) {
					break;
				} else if(isMethodActivated) {
					
					String cellActionString = excelDocument.getValueFromExcel(row.getRowNum(), 1);
					if(cellActionString.length() > 0) {
						
						if(cellActionString.equalsIgnoreCase("code")) {
//							System.out.println("code ->");
							String codemethodName = excelDocument.getValueFromExcel(row.getRowNum(), 4);
							if(codemethodName.equalsIgnoreCase("login") && browserType.equalsIgnoreCase("Html")) {
								driver = new HtmlUnitDriver() {
										
										   @Override
										public void setJavascriptEnabled(
												boolean enableJavascript) {
											// TODO Auto-generated method stub
											super.setJavascriptEnabled(true);
										}
									
									   protected WebClient modifyWebClient(WebClient client) {
										     // Does nothing here to be overridden.
										     DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
//										     creds.addNTLMCredentials("anbommak", "Bharathib123", null, -1, null, null);
										     creds.addNTLMCredentials("ckatta", "tmon$123", null, -1, null, null);
										     client.setCredentialsProvider(creds);
										     return client;
										     }
										};
										
//								JavascriptExecutor jse = (JavascriptExecutor) driver;
//								System.out.println(jse.executeScript("return document.readyState == \"complete;\""));
							} else if(browserType.equalsIgnoreCase("PhantomJS") && codemethodName.equalsIgnoreCase("login")) {
								driver = new PhantomJSDriver();
							} else if(browserType.equalsIgnoreCase("Other") && codemethodName.equalsIgnoreCase("login")) {
								FirefoxProfile profile = new FirefoxProfile();
								profile.setPreference("network.automatic-ntlm-auth.trusted-uris", "hmp-lt-01");
								driver = new FirefoxDriver(profile);
								driver.navigate().to("http://ckatta:c!1209A023@hmp-lt-01:8080/hmp/ui/");
							}
							else {
//								System.out.println("into method");
								System.out.println(codemethodName);
								Method method = codeSnippetCls.getDeclaredMethod(codemethodName, null);
								method.invoke(codeSnippetObject);
							}
						}
						else if(cellActionString.equalsIgnoreCase("movewindow")) {
							//Store the current window handle
							winHandleBefore = driver.getWindowHandle();

							//Perform the click operation that opens new window

							//Switch to new window opened
							for(String winHandle : driver.getWindowHandles()){
								System.out.println("window Handle"+winHandle);
							    driver.switchTo().window(winHandle);
							}

//							// Perform the actions on new window
//
//							//Close the new window, if that window no more required
//							driver.close();

							//Switch back to original browser (first window)

//							driver.switchTo().window(winHandleBefore);

							//continue with original browser (first window)
						} else if(cellActionString.equalsIgnoreCase("defaultwindow")) {
							driver.switchTo().window(winHandleBefore);
						} else if(cellActionString.equalsIgnoreCase("alert-ok")) {
							driver.switchTo().alert().accept();
						} else if(cellActionString.startsWith("navigate")) {
							String opType = cellActionString.split("-")[1];
							if(opType.equalsIgnoreCase("back")) {
								driver.navigate().back();
							}
						} else if(cellActionString.equalsIgnoreCase("rallyReplace")) {
//							System.out.println("value"+excelDocument.getValueFromExcel(row.getRowNum(), 3));
//							System.out.println(hashMap.get(excelDocument.getValueFromExcel(row.getRowNum(), 3)));
							String value = DataObject.executionData.remove(excelDocument.getValueFromExcel(row.getRowNum(), 3));
//							System.out.println("output    "+value);
							value = value.replace("(Copy of)", "May 2014");
//							System.out.println(value);
							DataObject.executionData.put(excelDocument.getValueFromExcel(row.getRowNum(), 3), value);
						} else {
							
							String varName = excelDocument.getValueFromExcel(row.getRowNum(), 3);
							String elementIdentifier = parseObject(excelDocument.getValueFromExcel(row.getRowNum(), 3), genericObject);
//							System.out.println(elementIdentifier);
							if(elementIdentifier == null) {
								elementIdentifier = DataObject.executionData.get(excelDocument.getValueFromExcel(row.getRowNum(), 3));
								if(elementIdentifier == null) {
									elementIdentifier = excelDocument.getValueFromExcel(row.getRowNum(), 3);
								}
							}
//							System.out.println(elementIdentifier);
							String elementValue = excelDocument.getValueFromExcel(row.getRowNum(), 4);
							if(elementValue.length() == 0) {
								elementValue = elementIdentifier;
							}
							
							if(elementValue.contains("function:")) {
//								System.out.println(elementValue);
								elementValue = elementValue.replace("function:", "");
								elementValue = expressionEvalutor.evaluateExpression(elementValue);
//								elementValue
							}
							
							DataObject.executionData.put(varName, elementValue);
							String xpathString = excelDocument.getValueFromExcel(row.getRowNum(), 2);
				
							WebElement webElement = null;
							if(xpathString.length() > 0) {
								if(xpathString.contains("@ID@")) {
									
//									int noOfIds = xpathString.lastIndexOf("@ID@", 0);
									String[] xpathSplttedStrings = xpathString.split("@ID@");
									xpathString = "";
									
									for(int i = 0; i<xpathSplttedStrings.length-1; i++) {
										
										String[] elementIdentifierStrings = elementIdentifier.split(", ");
										xpathString += xpathSplttedStrings[i] + elementIdentifierStrings[i];
//										System.out.println(xpathString);
									}
									
									xpathString += xpathSplttedStrings[xpathSplttedStrings.length-1];
								
//									xpathString = xpathString.replace("@ID@", elementIdentifier);
								}
//								System.out.println(xpathString);
								if(cellActionString.equalsIgnoreCase("displayclick")) {
//									System.out.println(driver.getPageSource());
									List<WebElement> webElements = driver.findElements(By.xpath(xpathString));
//									System.out.println(webElements.size());
									for(WebElement webElement2:webElements){
										if(webElement2.isDisplayed()){
											webElement = webElement2;
											break;
										}
									}
								} else if(cellActionString.equalsIgnoreCase("evaluate")) {
									JavascriptExecutor jse = (JavascriptExecutor) driver;
									jse.executeScript(xpathString);
//									Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(driver.getPageSource());
//									XPathExpression xPathExpression = xPath.compile(xpathString);
//									System.out.println(xPathExpression);
//									String evlString = (String) xPathExpression.evaluate(convertToXML(driver.getPageSource()));
//									System.out.println("eval "+evlString);
//									evlString = evlString.trim();
//									DataObject.executionData.put(elementIdentifier, evlString);
//									String expressionValue = String.valueOf(xPath.evaluate(xpathString, driver.getPageSource()));
//									System.out.println(expressionValue);
//									hashMap.put(elementIdentifier, expressionValue);
								} else {
									webElement = returnWebElement(xpathString, "120");
								}
							}
											
							if(cellActionString.startsWith("code")) {
								
							} else if(cellActionString.equalsIgnoreCase("click") || cellActionString.equalsIgnoreCase("displayclick")) {
								Set<Cookie> cookies = driver.manage().getCookies();
								Iterator it = cookies.iterator();
								while(it.hasNext()) {
									Object cookieName = it.next();
//									System.out.println(cookieName);
//									System.out.println();
								}
//								takeScreenShot(webElement);
								webElement.click();
							} else if(cellActionString.equalsIgnoreCase("verify")) {
								
								if(browserType.equalsIgnoreCase("service")) {
									
									System.out.println(xpathEvaluate(xpathString));
									
								} else {
									String verString = webElement.getText();
									if(verString.equalsIgnoreCase(elementValue)) {
										System.out.println("passed");
									} else {
										System.out.println("expected string not present in mail");
									}
								}
								

							} else if(cellActionString.equalsIgnoreCase("exist")){
								
								JavascriptExecutor jse = (JavascriptExecutor) driver;
								
								String xpathExecuteString = "var elementStatus = document.evaluate( '"+xpathString+"', document, null, XPathResult.ANY_TYPE, null );"
										+ "return elementStatus;";
								
								boolean eleStatus = Boolean.valueOf((String) jse.executeScript(xpathExecuteString));
								if(eleStatus) {
									System.out.println("passed");
								} else {
									System.out.println("expected string not present in mail");
								}
								
							} else if (cellActionString.equalsIgnoreCase("get")) {
//								System.out.println("Text::"+webElement.getText());
								System.out.println(elementIdentifier+"---"+webElement.getText());
								DataObject.executionData.put(elementIdentifier, webElement.getText());
//								takeScreenShot(webElement);
							} else if(cellActionString.equalsIgnoreCase("url")) {
								
								milliSecondsBefore = System.currentTimeMillis();
//								html.tr();
//								html.td().content(String.valueOf(milliSecondsBefore));
								driver.get(elementValue);
//								cal.setTimeInMillis(System.currentTimeMillis());
//								DateTime.
//								System.out.println(System.currentTimeMillis());
								
								/**
								 * page load status - start
								 */
								
								Wait<WebDriver> wait = new WebDriverWait(driver, 900);
								 wait.until(new Function<WebDriver, Boolean>() {
								        public Boolean apply(WebDriver driver) {
//								            System.out.println("Current Window State       : "
//								                + String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState")));
								            return String
								                .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
								                .equals("complete");
								        }
								    });
								 
								 /**
								  *  page load status - end
								  */
								 
								 long milliSecondsAfter = System.currentTimeMillis();
//								 reporter.log(String.valueOf(milliSecondsBefore));
//								 reporter.log(String.valueOf(milliSecondsAfter));
//								 reporter.log(String.valueOf(milliSecondsAfter-milliSecondsBefore)); 
//								 html.td().content(String.valueOf(milliSecondsAfter));
//								 html.td().content(String.valueOf(milliSecondsAfter-milliSecondsBefore));
//								 html.td().content("Successfully logged into system");
//								 html._tr();
//								 reporter.log(html.toHtml());
//								 System.out.println(driver.getPageSource());
							} else if(cellActionString.equalsIgnoreCase("send")) {
//								System.out.println("taking screen shot");
//								System.out.println("value "+elementValue);
								if(webElement != null) {
									webElement.click();
									takeScreenShot(webElement);
									webElement.clear();
//									if(webElement.getText().length() > 0) {
//										webElement.clear();
//									} else if(webElement.getAttribute("value") != null && webElement.getAttribute("value").length() > 0) {
//										JavascriptExecutor js = (JavascriptExecutor) driver;
//										js.executeScript("arguments[0].value = '';", webElement, 10);
//									}
//									System.out.println(elementValue);
									elementValue = elementValue.replace("NUM-", "");
									System.out.println(elementValue);
									webElement.sendKeys(elementValue);
								}
								
							} else if(cellActionString.equalsIgnoreCase("setValue")) {
//								System.out.println("IN");
								JavascriptExecutor js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].value = '"+elementValue+"';", webElement, 10);
//								js.executeScript("var ele = document.getElementById(\"uploadedFile\");" +
//										"alert(ele.value);");
							}
							else if(cellActionString.startsWith("mouse-move")) {
//								String opType = cellActionString.split("-")[1];
								Actions actions = new Actions(driver);
								actions.moveToElement(webElement);
								actions.build().perform();
							} else if(cellActionString.startsWith("key")) {
								String keyType = cellActionString.split("-")[1];
								Keys[] keys = Keys.values();
								int keyCount = 0;
								while(keyCount < keys.length) {
									String keyName = keys[keyCount].name();
									if(keyName.matches(keyType)) {
										webElement.sendKeys(keys[keyCount]);
										break;
									}
									keyCount++;
								}
							} else if(cellActionString.equalsIgnoreCase("sleep")) {
								System.out.println("waiting for 10 sec");
								Thread.sleep(10000);
							} else if(cellActionString.equalsIgnoreCase("rightclick")) {
								Actions actions = new Actions(driver);
								actions.contextClick(webElement);
								actions.perform();
							} else if(cellActionString.equalsIgnoreCase("select")) {
								Select select = new Select(webElement);
								boolean optionStatus = false;
								int countI = 0;
								while(!optionStatus && countI <=1000) {
									List<WebElement> options = select.getOptions();
									for(WebElement option:options) {
										String optionText = option.getText();
										System.out.println(optionText);
										if(optionText.equalsIgnoreCase(elementValue)) {
											select.selectByVisibleText(optionText);
											optionStatus = true;
											break;
										}
									}
									countI++;
								}
							} else if(cellActionString.startsWith("attribute")) {
								String attributeValue = webElement.getAttribute(cellActionString.split("-")[1]);
//								System.out.println(elementIdentifier);
//								System.out.println("attributevalue: "+attributeValue);
								if(DataObject.executionData.containsKey(elementIdentifier)) {
									DataObject.executionData.remove(elementIdentifier);
								}
								if(!DataObject.executionData.containsKey(elementIdentifier)) {
									System.out.println("false");
								}
								DataObject.executionData.put(elementIdentifier, attributeValue);
//								System.out.println("check "+hashMap.get(elementIdentifier));
							} else {
								
							}
						}
						
					}
					
				} else if(cellValue.equalsIgnoreCase(methodName)) {
					isMethodActivated = true;
				} 
			}
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			long milliSecondsAfter = System.currentTimeMillis(); 
//			 try {
////				html.td().content(String.valueOf(milliSecondsAfter));
////				 html.td().content(String.valueOf(milliSecondsAfter-milliSecondsBefore));
////				 html.td().content("Exception occured"+e.toString());
////				 html._tr();
////				reporter.log(html.toHtml());
//			 } catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
//			reporter.log(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public WebElement returnWebElement(String xpath, String waitTime) {
		final String xpathString = xpath;
		WebElement selectedElement = null;
		try {
//			selectedElement = (new WebDriverWait(driver, Integer.parseInt(waitTime))
//					.until(new ExpectedCondition<WebElement>() {
//						public WebElement apply(WebDriver d) {
//							return d.findElement(By.xpath(xpathString));
//						}
//					}));
			System.out.println(xpath);
			if(browserType.equalsIgnoreCase("Firefox")) {
			
				int eCount = 0;
				while(true) {
					selectedElement = (new WebDriverWait(driver, Integer.parseInt(waitTime)))
							  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
					System.out.println(selectedElement);
					if(selectedElement.isDisplayed() || eCount >= 5) {
//						takeScreenShot(selectedElement);
						break;
					}
					eCount++;
				}
				
			} else if(browserType.equalsIgnoreCase("Html")) {
				
				selectedElement = (new WebDriverWait(driver, Integer.parseInt(waitTime)))
						  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
				
			} else {
				while(true) {
					selectedElement = (new WebDriverWait(driver, Integer.parseInt(waitTime)))
							  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
//					System.out.println(selectedElement);
					if(selectedElement.isDisplayed()) {
//						takeScreenShot(selectedElement);
						break;
					}
				}
			}
			
			// selectedElement = (new WebDriverWait(driver, waitTime))
			// .until(new ExpectedCondition<WebElement>(){
			// @Override
			// public WebElement apply(WebDriver d) {
			// return d.findElement(by);
			// }});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return selectedElement;
	}
	
	public void takeScreenShot(WebElement element) {
		try {
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			
			if(element != null) {
				Point p = element.getLocation();
				int width = element.getSize().getWidth();
				int height = element.getSize().getHeight();
				Rectangle rectangle = new Rectangle(width, height);
				BufferedImage img = null;
				img = ImageIO.read(scrFile);
				BufferedImage dest = img;
//				BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rectangle.width, rectangle.height);
				ImageIO.write(dest, "png", scrFile);
			}
			String screenshotPath = "ss/"+scrFile.lastModified()+".png";
			FileUtils.copyFile(scrFile, new File("ss/"+scrFile.lastModified()+".png"));
			System.out.println("<img src='../"+screenshotPath+"' width=\"600px;\" height=\"450px;\"/>");
			reporter.log("<img src='../"+screenshotPath+"' width=\"600px;\" height=\"450px;\"/>");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void closeApplication() {
//		driver.close();
		driver.quit();
	}
	
	public Document convertToXML(String inputContent) {
		Document doc = null;
		try 
		{
			System.out.println(inputContent);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        InputSource is = new InputSource(new StringReader(inputContent));
	        System.out.println(is);
	        doc = builder.parse(is);
	        System.out.println(doc);
//		    TransformerFactory tFactory = TransformerFactory.newInstance();
//		    Transformer transformer = tFactory.newTransformer();
//		    StringWriter strWriter = new StringWriter();
//		    transformer.transform(new StreamSource(new StringReader(inputContent)), new StreamResult(strWriter));
//		    String xmlString = strWriter.toString();
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return doc;
	}

	public static String xpathEvaluate(String xpathExpression) {
		
		Object returnObject = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(xpathExpression);
			returnObject = expr.evaluate(doc);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return String.valueOf(returnObject);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebApp webApp = new WebApp();
		GenericObject genericObject = new GenericObject();
//		System.out.println(HMPUITesting.class.getName());
//		webApp.setCodeSnippetClass(HMPUITesting.class.getName());
		webApp.setCodeSnippetClass(HMPUITesting.class.getName());
		webApp.runApplication("Login", genericObject);
		webApp.runApplication("Dashboard", genericObject);
		webApp.runApplication("CreateChildNode", genericObject);
		webApp.runApplication("Workflowapproval", genericObject);
//		webApp.setCodeSnippetClass("HMPUITesting");
//		webApp.runApplication("Dashboard",genericObject);
//		webApp.runApplication("Login", genericObject);
//		webApp.runApplication("Dashboard", genericObject);
//		webApp.runApplication("CreateChildNode", genericObject);
//		webApp.runApplication("Workflowapproval", genericObject);
//		webApp.runApplication("BGNodeCreate", genericObject);
//		webApp.runApplication("itemCreate", genericObject);
//		webApp.runApplication("TestcaseUploading", genericObject);
//		webApp.runApplication("selectsourcetf", genericObject);
//		for(int tc = 4226; tc<= 4234; tc++) {
//			String tcid = "TC"+String.valueOf(tc);
//			genericObject.setTcid(tcid);
//			webApp.runApplication("selectdesttfcopy", genericObject);
//		}
//		webApp.runApplication("selectsourcetfdelete", genericObject);
//		for(int tc = 6017; tc<=6097; tc++) {
//			String tcid = "TC"+String.valueOf(tc);
//			genericObject.setTcid(tcid);
//			webApp.runApplication("selectdelete", genericObject);
////			webApp.runApplication("selectdesttfcopy", genericObject);
//		}
	}

}
