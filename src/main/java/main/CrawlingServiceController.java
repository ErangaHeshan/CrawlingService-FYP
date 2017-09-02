package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import db.VersionDAO;
import model.Url;
import model.Version;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Created by roshanalwis on 8/26/17.
 */

@RestController
public class CrawlingServiceController {
    @RequestMapping(value = "/crawl", method = RequestMethod.POST)
    public ResponseEntity<String> crawl(@RequestBody Url url) {
        /*
            Crawl and save to version repository
         */

        String response = null;

        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_45);

        // Allow to load JavaScripts
        // driver.setJavascriptEnabled(true);

        // Navigate to URL
        driver.get(url.toString());

        // Read page content
        String content = driver.getPageSource();

        // Close driver
        driver.quit();

        // Create version
        Version version = new Version(url.toString(), content);

        // Save version
        VersionDAO versionDAO = new VersionDAO();
        versionDAO.create(version);

        // Convert result in to JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            response = objectMapper.writeValueAsString(version);
        } catch (JsonProcessingException e) {

        }

        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/getVersion", method = RequestMethod.POST)
    public ResponseEntity<String> getVersion(@RequestBody Version version){
        /*
            Get versions from version repository
         */

        String response = null;

        // Load versions
        VersionDAO versionDAO = new VersionDAO();
        List<Version> versionList = versionDAO.read(version);

        // Convert result into JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.writeValueAsString(versionList);
        } catch (JsonProcessingException e) {

        }

        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/getTimeStamp", method = RequestMethod.POST)
    public ResponseEntity<String> getVersionMetaData(@RequestBody Version version){
        /*
            Get timestamps
         */

        String response = null;

        // Load timestamps
        VersionDAO versionDAO = new VersionDAO();
        List<Version> versionList = versionDAO.readTimeStamp(version);

        // Convert result into JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.writeValueAsString(versionList);
        } catch (JsonProcessingException e) {

        }

        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    public ResponseEntity<String> removeVersion(@RequestBody Version version){
        /*
            Remove version from the version repository
         */

        String response = null;

        VersionDAO versionDAO = new VersionDAO();


        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

//    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    public ResponseEntity<String> test(@RequestBody User user) throws IOException {
//
//        RestTemplate restTemplate = new RestTemplate();
//        String response = restTemplate.postForObject("http://localhost:7175/getUser", user, String.class);
//
//        // Result convert to a JSON string
//        ObjectMapper mapper = new ObjectMapper();
//        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, User.class);
//        List<User> userList = mapper.readValue(response, type);
//
//        return new ResponseEntity<String>(response, HttpStatus.OK);
//    }


}