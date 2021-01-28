package ru.javaops.masterjava;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.User;

import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

public class MainXmlTest {

    private String projectName = "topjava";
    private String payload = "payload.xml";

    @Test
    public void mainTest() throws Exception {
        MainXml.main((String[]) Arrays.asList(projectName, payload).toArray());
    }

    @Test
    public void findUsersByProjectNameTest() {
        MainXml mainXml = new MainXml();
        URL url = Resources.getResource(payload);

        Set<User> usersByProjectName =
                mainXml.findUsersByProjectName(projectName, url);
        assertEquals(3, usersByProjectName.size());
    }
}
