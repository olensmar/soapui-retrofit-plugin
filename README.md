## soapui-plugin-template

A skeleton maven project containing sample listener, action and factory extensions for SoapUI.

Sample listeners and actions are in both Groovy and Java. The sample factory shows how to create a custom assertion, in
this case one that asserts a response to be valid JSON.

See the corresponding documentation on the SoapUI Website:
- [Extending SoapUI](http://www.soapui.org/Developers-Corner/extending-soapui.html)
- [Custom Factories](http://www.soapui.org/Developers-Corner/custom-factories.html)

## Usage

Clone this repository and use it as a template for your SoapUI plugins. Once cloned you will need to:

- Change the name, groupid and artifactid of the plugin (remember that SoapUI currently requires the name of the plugin jar to end with "plugin.jar")
- Modify sample classes as desired for your plugin
- Remove the sample classes that you won't be needing

Build the plugin with maven:

```
mvn clean install
```

will create a plugin-jar in the target folder; copy it to your SoapUI installations bin/plugins folder.
