## soapui-retrofit-plugin

A SoapUI plugin that generates [Retrofit](http://square.github.io/retrofit/) Java Interfaces for REST APIs defined in SoapUI.

## Installation

Download the plugin from [sourceforge](https://sourceforge.net/projects/soapui-plugins/files/soapui-retrofit-plugin/)
and copy it into your SoapUI installations /bin/plugins folder. Requires SoapUI 5.+

## Usage

The plugin adds a "Generate Retrofit Interface" action to the REST API popup menu. Select it for any REST API that you have in
your project - no matter how it was created - and use the generated interface with Retrofit to quickly build clients in Java
(both Android and JSE). The dialog has the following options:

- Package : the package of the generated interface
- Name : the class name of the generated interface
- Target Folder : where to write the generated interface
- Prefix Method Name : if selected the name of a methods HTTP Verb will be prefixed to the method name
- Generate Async : if selected the generated methods will use Callbacks instead of returning Response objects (see Retrofit
documentation)
- Use Resource Name : if selected the name of each method will be taken from the name of the Resource in SoapUI (and not
generated from the resource path

Together with the existing RAML and Swagger plugins - and with the REST Discovery functionality in SoapUI 5 - this plugin makes it
extremely easy to generate java clients for more or less any REST API out there. Also - since the RAML plugin lets you browse the APIHub
directory you can import any API from there and then generate a corresponding Retrofit interface.

Have fun!

## Shortcomings

There are a bunch - this being an initial version. If there is interest I am happy to improve the plugin as requested (if
it makes sense that is..).

This is what I know of at this time:
- Only works with raw responses since SoapUI doesn't have any schema information on response messages
- No option to use the RxJava support in Retrofit for async calls
- The method naming algorithm is very crude and will probably generate some strange names for plural nouns, etc.
- Does not support form encoded and multipart requests yet

I'm sure there is more - please report bugs and issues here at GitHub

## Source

This is a very simple plugin - it only has two classes:
- [GenerateRetrofitAction](https://github.com/olensmar/soapui-retrofit-plugin/tree/master/src/main/java/com/smartbear/soapui/plugin/retrofit/actions) : the action class that shows the dialog (Java)
- [RetrofitGenerator](https://github.com/olensmar/soapui-retrofit-plugin/tree/master/src/main/groovy/com.smartbear.soapui.plugin.retrofit/RetrofitGenerator.groovy) : the class that does the actual generation (Groovy)

Feel free to suggest improvements