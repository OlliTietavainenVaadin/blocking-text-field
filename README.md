# BlockingTextField

BlockingTextField is a UI component add-on for Vaadin 7.

## Building and running demo

`mvn clean install`

`cd blocking-text-field-demo`

`mvn jetty:run`

To see the demo, navigate to [http://localhost:8080/]()

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for blocking-text-field-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your blocking-text-field-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the blocking-text-field-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/blocking-text-field-demo/ to see the application.

### Debugging client-side

Debugging client side code in the blocking-text-field-demo project:
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application or by adding ?superdevmode to the URL
  - You can access Java-sources and set breakpoints inside Chrome if you enable source maps from inspector settings.
 
## Release notes

### Version 0.1
* First version
* Allows optionally specifying a minimum and maximum length for the TextField
* Allows optionally blocking unwanted characters
  * Alphanumerics only
  * Limited special characters (currently hardcoded as -+#.,<>|;:_'* 
  * ...or both of the above