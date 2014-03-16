package com.smartbear.soapui.plugin.retrofit

import com.eviware.soapui.impl.rest.RestService
import com.eviware.soapui.impl.rest.RestServiceFactory
import com.eviware.soapui.impl.wsdl.WsdlProject

/**
 * Created by ole on 14/03/14.
 */
class RetrofitGeneratorTestCase extends GroovyTestCase {

    public void testGenerator()
    {
        def project = new WsdlProject();
        RestService service = project.addNewInterface( "REST API", RestServiceFactory.REST_TYPE )

        RetrofitGenerator generator = new RetrofitGenerator( service )

        File file = generator.generate( "com.test", "MyInterface", "." )

        assertTrue( file.exists())

        file.deleteOnExit()

        assertTrue( file.text.startsWith( "package com.test;"))
    }

    public void testGeneratorWithProjectFile()
    {
        def project = new WsdlProject( "src/test/resources/ALM-REST-soapui-project.xml")
        RestService service = project.getInterfaceAt( 0 )

        RetrofitGenerator generator = new RetrofitGenerator( service )
        File file = generator.generate( "com.test", "RESTInterface", "." )

        assertTrue( file.exists())

        file.deleteOnExit()

    }
}
