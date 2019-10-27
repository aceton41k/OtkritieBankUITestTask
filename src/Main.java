import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.testng.Assert.assertTrue;

public class Main {

    @Test (priority = 0)
    public void searchGoogle() {
        open("https://www.google.com/");
        $(By.name("q")).setValue("Открытие").pressEnter();
        $(By.xpath("//div/cite[text()='https://www.open.ru']")).shouldHave(appear).click();
    }

    @Test (dependsOnMethods = "searchGoogle")
    public void currencyTestHtml() {
        open("https://www.open.ru/");
        float usdSale = Float.parseFloat($(By.xpath("//td//span[text()='USD']/../../../td[4]/div/span[@class='main-page-exchange__rate']")).getText().replace(",","."));
        float usdBuy = Float.parseFloat($(By.xpath("//td//span[text()='USD']/../../../td[2]/div/span[@class='main-page-exchange__rate']")).getText().replace(",","."));
        float eurSale = Float.parseFloat($(By.xpath("//td//span[text()='EUR']/../../../td[4]/div/span[@class='main-page-exchange__rate']")).getText().replace(",","."));
        float eurBuy = Float.parseFloat($(By.xpath("//td//span[text()='EUR']/../../../td[2]/div/span[@class='main-page-exchange__rate']")).getText().replace(",","."));
        assertTrue(usdSale > usdBuy);
        assertTrue(eurSale > eurBuy);

    }

    // Второй способ. Вместо парсинга html решил распарсить json который был на странице и свравнить сзначения курсов оттуда

    @Test (dependsOnMethods = "searchGoogle")
    public void currencyTestJson() throws ParseException {
        open("https://www.open.ru/");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse($(By.xpath("//div[@data-react-class='MainPageInfo']")).getAttribute("data-react-props"));
        JSONArray usdSale = JsonPath.read(jsonObject, "exchangeCard.exchangeRates[?(@.title=='Курс обмена в интернет-банке')].data[?(@.name=='USD')].sale.value");
        JSONArray usdBuy =  JsonPath.read(jsonObject, "exchangeCard.exchangeRates[?(@.title=='Курс обмена в интернет-банке')].data[?(@.name=='USD')].buy.value");
        JSONArray eurSale = JsonPath.read(jsonObject, "exchangeCard.exchangeRates[?(@.title=='Курс обмена в интернет-банке')].data[?(@.name=='EUR')].sale.value");
        JSONArray eurBuy = JsonPath.read(jsonObject, "exchangeCard.exchangeRates[?(@.title=='Курс обмена в интернет-банке')].data[?(@.name=='USD')].buy.value");
        assertTrue((double)usdSale.get(0) > (double)usdBuy.get(0));
        assertTrue((double)eurSale.get(0) > (double )eurBuy.get(0));
    }
}
