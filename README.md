## soapui-retrofit-plugin

A Ready! API plugin that generates [Retrofit](http://square.github.io/retrofit/) Java Interfaces for REST APIs defined 
in SoapUI Pro / Ready! API. Ready the [blog-post](http://olensmar.blogspot.com/2014/11/a-ready-api-soapui-pro-retrofit-plugin.html)
to get an more detailed walk through with screenshots.

## Installation

Download the plugin from Plugin Repository from the Plugin Manager inside SoapUI Pro 5.1.X or Ready! API 1.X.

## Usage

The plugin adds a "Generate Retrofit Interface" action to the REST API popup menu. Select it for any REST API that you have in
your project - no matter how it was created - and use the generated interface with Retrofit to quickly build clients in Java
(both Android and JSE). The dialog has the following options:

- Package : the package of the generated interface
- Name : the class name of the generated interface
- Target Folder : where to write the generated interface
- Prefix Method Name : if selected the name of a methods HTTP Verb will be prefixed to the method name
- Ignore HTTP Parameters : if selected HTTP parameters are not added to generated interface
- Generate Async : if selected the generated methods will use Callbacks instead of returning Response objects (see Retrofit
documentation)
- Use Resource Name : if selected the name of each method will be taken from the name of the Resource in SoapUI (and not
generated from the resource path

Together with the existing RAML and Swagger plugins - and with the REST Discovery functionality in Ready! API - this plugin makes it
extremely easy to generate java clients for more or less any REST API out there. Also - since the RAML plugin lets you browse the APIHub
directory you can import any API from there and then generate a corresponding Retrofit interface.

Have fun!

## Shortcomings

There are a bunch - this being an initial version. If there is interest I am happy to improve the plugin as requested (if
it makes sense that is..).

This is what I know of at this time:
- Only works with raw responses since SoapUI doesn't have any schema information on response messages
- No option to use the RxJava support in Retrofit for async calls
- Does not support form encoded and multipart requests

I'm sure there is more - please report bugs and issues here at GitHub

## Release History

- 2014-11-20 : 1.0 Initial release
- 2014-11-21 : 1.0.1 Added option to ignore header parameters

Feel free to suggest improvements!

/Ole