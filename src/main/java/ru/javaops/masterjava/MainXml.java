package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;


public class MainXml {

    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);


    public static void main(String[] args) throws Exception {
        if(args.length < 2) {
            System.out.println("Wrong data, use projectName as first arg and payload location as second arg");
            System.exit(1);
        }
        URL dataUrl = Resources.getResource(args[1]);
        String projectName = args[0];

        MainXml mainXml = new MainXml();
        Set<User> usersByProjectName =
                mainXml.findUsersByProjectName(projectName, dataUrl);

        mainXml.printUsersInAlphabeticOrder(usersByProjectName);

    }

    public Set<User> findUsersByProjectName(String projectName, URL dataUrl) {

        try(InputStream is = dataUrl.openStream()) {
            Payload payload = JAXB_PARSER.unmarshal(is);
            Project foundProject = payload.getProjects()
                    .getProject()
                    .stream()
                    .filter(project -> projectName.equals(project.getName()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No project with " + projectName + " was found"));

            Set<String> groupNames = foundProject.getGroup().stream()
                    .map(Project.Group::getName)
                    .collect(Collectors.toSet());
            return payload.getUsers().getUser()
                    .stream()
                    .filter(user -> !user.getGroupRefs().isEmpty())
                    .filter(user -> user.getGroupRefs()
                            .stream()
                            .map(gref -> (Project.Group) gref)
                            .map(Project.Group::getName)
                            .anyMatch(groupNames::contains))
                    .collect(Collectors.toSet());
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void printUsersInAlphabeticOrder(Set<User> users) {
        users.stream()
                .map(User::getValue)
                .sorted()
                .forEach(System.out::println);
    }
}
