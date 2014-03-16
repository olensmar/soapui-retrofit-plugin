package com.smartbear.soapui.plugin.retrofit

import com.eviware.soapui.impl.rest.HttpMethod
import com.eviware.soapui.impl.rest.RestMethod
import com.eviware.soapui.impl.rest.RestService
import com.eviware.soapui.impl.rest.support.RestParameter
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder

/**
 * Created by ole on 14/03/14.
 */
class RetrofitGenerator
{
    private RestService restService
    private PrintWriter writer
    private Set<String> names = new HashSet<String>()
    private boolean async
    private boolean generateMethodNamesFromPath = true

    public RetrofitGenerator( RestService restService )
    {
        this.restService = restService
    }

    public void setAsync( boolean async )
    {
        this.async = async
    }

    public File generate( String packageName, String name, String folder )
    {
        def w = new StringWriter()
        writer = w.newPrintWriter()

        startInterface( packageName, name )

        restService.allResources.each {

            it.restMethodList.each {
                addMethod( it )
            }
        }

        endInterface()

        File result = new File( folder, name + ".java")
        result.text = w.toString()

        return result
    }

    def endInterface() {
        writer.println( "}" )
    }

    def addMethod(RestMethod method) {
        def name = createNameForMethod( method )

        writer.println( "   @" + method.getMethod().toString().toUpperCase() + "(\"" +
            method.resource.getFullPath( true) + "\")")

        def signature = ""

        method.overlayParams.each {
            RestParameter p = it.value
            switch( p.style )
            {
                case RestParamsPropertyHolder.ParameterStyle.TEMPLATE :
                    if( signature.length() > 0 )
                        signature += ", "

                    signature += "@Path(\"" + p.name + "\") String " + p.name + " "
                    break

                case RestParamsPropertyHolder.ParameterStyle.QUERY :
                    if( signature.length() > 0 )
                        signature += ", "

                    signature += "@Query(\"" + p.name + "\") String " + p.name + " "
                    break

                case RestParamsPropertyHolder.ParameterStyle.HEADER :
                    if( signature.length() > 0 )
                        signature += ", "

                    signature += "@Header(\"" + p.name + "\") String " + p.name + " "
                    break
            }
        }

        if( method.getMethod() == HttpMethod.PUT || method.getMethod() == HttpMethod.POST )
        {
            if( signature.length() > 0 )
                signature += ", "

            signature += "@Body TypedByteArray body "
        }

        if( async )
        {
            if( signature.length() > 0 )
                signature += ", "

            signature += "Callback<Response> cb "

            writer.println( "   public void $name(" + signature + ");")
        }
        else
        {
            writer.println( "   public Response $name(" + signature + ");")
        }

        writer.println()
    }

    def createNameForMethod(RestMethod restMethod) {

        def name = generateMethodNamesFromPath ? "" : restMethod.resource.name + restMethod.getMethod().name().toUpperCase()
        name = namify( name )
        if( name.length() == 0 || names.contains( name ))
        {
            name = restMethod.method.name().toLowerCase()

            def path = restMethod.resource.getFullPath( true )

            def parts = path.split( "/" );
            if( parts.length > 0 )
            {
                int ix = parts.length-1
                def part = parts[ix]

                // find last path parameter
                while( ix > 0 && part.length() > 2 && part[0] == '{' && part[part.length()-1] == '}')
                {
                    parts[ix] = part.substring( 1, part.length()-1 )
                    part = parts[--ix]
                }

                // just one path parameter
                if( ix == parts.length-2 )
                {
                    if( parts[ix].endsWith( "s") && parts[ix].length() > 1 )
                    {
                        path = parts[ix].substring( 0, parts[ix].length()-1 )
                    }
                    else
                    {
                        path = parts[ix] + "By" + namify(parts[ix+1])
                    }
                }
                // multiple path parameters
                else if( ix < parts.length-2 )
                {
                    path = parts[ix] + "By" + namify(parts[ix+1])
                    while( ix < parts.length-2 )
                    {
                        path += "And" + namify( parts[ix+2] )
                        ix++
                    }
                }
                else
                {
                    path = parts[parts.length-1]
                }
            }

            name += namify( path )

            if( names.contains( name ))
            {
                ix = 1
                while( names.contains( name + "_" + ix ))
                    ix++

                name = name + "_" + ix
            }
        }

        if( Character.isUpperCase(name.charAt( 0 )))
            name = Character.toLowerCase( name.charAt(0)) + name.substring(1)

        names.add( name )
        return name
    }

    String namify(String s)
    {
       def result = ""
       def upperCase = true

       s.chars.each {
          def charValue = it.charValue()
          if( Character.isJavaIdentifierPart(charValue))
          {
             result += upperCase ? Character.toUpperCase(charValue) : charValue
             upperCase = false
          }
          else
          {
             upperCase = true
          }
       }

       return result
    }

    def startInterface(String packageName, String name) {
        writer.println( "package $packageName;")
        writer.println()
        writer.println( "import retrofit.client.Response;" )
        writer.println( "import retrofit.http.*;" )
        writer.println( "import retrofit.mime.TypedByteArray;")
        writer.println()
        writer.println( "/**")
        writer.println( " * Generated with SoapUI Retrofit Plugin")
        writer.println( " **/" )
        writer.println()
        writer.println( "public interface $name {")
    }
}
