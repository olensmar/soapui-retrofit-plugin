package com.smartbear.soapui.plugin.retrofit

import com.eviware.soapui.impl.rest.RestMethod
import com.eviware.soapui.impl.rest.RestRequestInterface
import com.eviware.soapui.impl.rest.RestResource
import com.eviware.soapui.impl.rest.RestService
import com.eviware.soapui.impl.rest.support.RestParameter
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder
import org.modeshape.common.text.Inflector

/**
 * Created by ole on 14/03/14.
 */

class RetrofitGenerator {
    private RestService restService
    private PrintWriter writer
    private Set<String> names = new HashSet<String>()
    private boolean async
    private boolean useResourceName
    private boolean prefixMethodName
    private Inflector inflector = new Inflector()
    private List<RestResource> resources

    public RetrofitGenerator(RestService restService) {
        this.restService = restService
    }

    public void setAsync(boolean async) {
        this.async = async
    }

    public File generate(String packageName, String name, String folder) {
        names.clear()
        names.addAll( javaKeywords )

        def w = new StringWriter()
        writer = w.newPrintWriter()

        startInterface(packageName, name)

        restService.allResources.each {

            if( resources == null || resources.empty || resources.contains( it )) {

                it.restMethodList.each {
                    addMethod(it)
                }
            }
        }

        endInterface()

        File result = new File(folder, name + ".java")
        result.text = w.toString()

        return result
    }

    def endInterface() {
        writer.println("}")
    }

    def addMethod(RestMethod method) {
        def name = createNameForMethod(method)

        writer.println("   @" + method.getMethod().toString().toUpperCase() + "(\"" +
                method.resource.getFullPath(true) + "\")")

        def signature = ""

        method.overlayParams.each {
            RestParameter p = it.value
            def paramName = makeParameterName(p.name)

            switch (p.style) {
                case RestParamsPropertyHolder.ParameterStyle.TEMPLATE:
                    if (signature.length() > 0)
                        signature += ", "

                    signature += "@Path(\"" + p.name + "\") String " + paramName + " "
                    break

                case RestParamsPropertyHolder.ParameterStyle.QUERY:
                    if (signature.length() > 0)
                        signature += ", "

                    signature += "@Query(\"" + p.name + "\") String " + paramName + " "
                    break

                case RestParamsPropertyHolder.ParameterStyle.HEADER:
                    if (signature.length() > 0)
                        signature += ", "

                    signature += "@Header(\"" + p.name + "\") String " + paramName + " "
                    break
            }
        }

        if (method.getMethod() == RestRequestInterface.HttpMethod.PUT || method.getMethod() == RestRequestInterface.HttpMethod.POST) {
            if (signature.length() > 0)
                signature += ", "

            signature += "@Body TypedByteArray body "
        }

        if (async) {
            if (signature.length() > 0)
                signature += ", "

            signature += "Callback<Response> cb "

            writer.println("   public void $name(" + signature + ");")
        } else {
            writer.println("   public Response $name(" + signature + ");")
        }

        writer.println()
    }

    def makeParameterName(String str) {

        str = namify( str )
        str = str.length() > 1 ? str.substring(0,1).toLowerCase() + str.substring(1) : str.toLowerCase()

        if( javaKeywords.contains( str ))
            str = "_" + str

        return str
    }

    def createNameForMethod(RestMethod restMethod) {


        def name = useResourceName ? namify( restMethod.resource.name ) : ""
        def prefix = prefixMethodName ? restMethod.method.name().toLowerCase() : ""

        if (name.length() == 0 || names.contains( prefix + name.toLowerCase())) {

            name = ""

            def path = restMethod.resource.getFullPath(true)

            def parts = path.split("/");
            def ix = -1

            if (parts.length > 0) {
                ix = parts.length - 1
                def part = parts[ix]

                // find last path parameter
                while (ix > 0 && part.length() > 2 && part[0] == '{' && part[part.length() - 1] == '}') {
                    parts[ix] = part.substring(1, part.length() - 1)
                    part = parts[--ix]
                }

                // just one path parameter
                if (ix == parts.length - 2) {
                     path = inflector.singularize( namify(parts[ix])) + "By" + namify(parts[ix + 1])
                }
                // multiple path parameters
                else if (ix < parts.length - 2) {

                    path = inflector.singularize( namify(parts[ix])) + "By" + namify(parts[ix + 1])

                    while (ix < parts.length - 2) {
                        path += "And" + namify(parts[ix + 2])
                        ix++
                    }
                } else {
                    path = parts[parts.length - 1]
                }
            }

            name += namify(path)

            if (names.contains( prefix + name.toLowerCase())) {

                while( ix > 0 )
                {
                    ix--
                    name = namify(parts[ix]) + name

                    if( !names.contains( prefix + name.toLowerCase() ))
                        break
                }

                if( names.contains( prefix + name.toLowerCase() ))
                {
                    def cnt = 1
                    while (names.contains( prefix + (name + "_" + cnt).toLowerCase() ))
                        cnt++

                    name = name + "_" + cnt
                }
            }
        }

        name = prefix + name
        names.add( name.toLowerCase())

        return name
    }

    public static String namify(String s) {
        def result = ""
        def upperCase = true

        s.chars.each {
            char charValue = it.charValue()
            if (Character.isAlphabetic( (int) charValue)) {
                result += upperCase ? Character.toUpperCase(charValue) : charValue
                upperCase = false
            } else {
                upperCase = true
            }
        }

        return result
    }

    def startInterface(String packageName, String name) {
        writer.println("package $packageName;")
        writer.println()
        writer.println("import retrofit.client.Response;")
        writer.println("import retrofit.http.*;")
        writer.println("import retrofit.mime.TypedByteArray;")

        if( async )
            writer.println( "import retrofit.Callback;" )

        writer.println()
        writer.println("/**")
        writer.println(" * Generated with SoapUI Retrofit Plugin")
        writer.println(" **/")
        writer.println()
        writer.println("public interface $name {")
    }

    public void setUseResourceName(boolean useResourceName) {
        this.useResourceName = useResourceName;
    }

    void setPrefix(boolean prefix) {
        this.prefixMethodName = prefix
    }

    private static final Set<String> javaKeywords = new HashSet<String>(Arrays.asList(
            "abstract",     "assert",        "boolean",      "break",           "byte",
            "case",         "catch",         "char",         "class",           "const",
            "continue",     "default",       "do",           "double",          "else",
            "enum",         "extends",       "false",        "final",           "finally",
            "float",        "for",           "goto",         "if",              "implements",
            "import",       "instanceof",    "int",          "interface",       "long",
            "native",       "new",           "null",         "package",         "private",
            "protected",    "public",        "return",       "short",           "static",
            "strictfp",     "super",         "switch",       "synchronized",    "this",
            "throw",        "throws",        "transient",    "true",            "try",
            "void",         "volatile",      "while"
    ));

    public void setResources ( List < RestResource > resources )
    {
        this.resources = resources;
    }
}
