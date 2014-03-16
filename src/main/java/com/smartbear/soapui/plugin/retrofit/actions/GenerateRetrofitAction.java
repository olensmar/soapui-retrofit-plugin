package com.smartbear.soapui.plugin.retrofit.actions;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.support.Tools;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AForm;
import com.smartbear.soapui.plugin.retrofit.RetrofitGenerator;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by ole on 16/01/14.
 */

public class GenerateRetrofitAction extends AbstractSoapUIAction<RestService> {

    private XFormDialog dialog;

    public GenerateRetrofitAction()
    {
        super( "Generate Retrofit Interface", "Generates a Retrofit Java Interface for this REST Service");
    }

    @Override
    public void perform(RestService restService, Object o) {
        if( dialog == null )
        {
            dialog = ADialogBuilder.buildDialog( Form.class );
            dialog.setValue( Form.NAME, RetrofitGenerator.namify( restService.getName() ));
            dialog.setBooleanValue( Form.PREFIX, true );
        }

        if( dialog.show() )
        {
            RetrofitGenerator generator = new RetrofitGenerator( restService );
            generator.setAsync( dialog.getBooleanValue( Form.ASYNC ));
            generator.setUseResourceName( dialog.getBooleanValue( Form.USERESOURCENAME ));
            generator.setPrefix( dialog.getBooleanValue( Form.PREFIX));
            File file = generator.generate( dialog.getValue(Form.PACKAGE), dialog.getValue( Form.NAME), dialog.getValue(Form.FOLDER));
            if( file != null && file.exists() && UISupport.confirm("Open generated interface with system viewer", "Generate Retrofit Interface"))
            {
                try {
                    Tools.openURL( file.toURI().toURL().toString() );
                } catch (MalformedURLException e) {
                    SoapUI.logError(e);
                }
            }
        }
    }

    @AForm( name = "Generate Retrofit Interface", description = "Generates a Retrofit Java Interface for this REST Service" )
    public interface Form
    {
        @AField( name = "Package", description = "The package name for the generated interface", type = AField.AFieldType.STRING )
        public final static String PACKAGE = "Package";

        @AField( name = "Name", description = "The class name for the generated interface", type = AField.AFieldType.STRING )
        public final static String NAME = "Name";

        @AField( name = "Target Folder", description = "Where to save the interface", type = AField.AFieldType.FOLDER )
        public final static String FOLDER = "Target Folder";

        @AField( name = "Prefix Method Name", description = "Prefix generated Method names with HTTP VERB name", type = AField.AFieldType.BOOLEAN )
        public final static String PREFIX = "Prefix Method Name";


        @AField( name = "Generate Async", description = "Generate asynchronous method calls (using Callbacks)", type = AField.AFieldType.BOOLEAN )
        public final static String ASYNC = "Generate Async";

        @AField( name = "Use Resource Name", description =  "Use Resource names for method names when possible", type = AField.AFieldType.BOOLEAN)
        public final static String USERESOURCENAME = "User Resource Name";
    }
}
