package com.smartbear.ready.plugin.retrofit.actions;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.SoapUITools;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.support.types.StringList;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormOptionsField;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AForm;
import com.smartbear.analytics.Analytics;
import com.smartbear.analytics.AnalyticsManager;
import com.smartbear.ready.plugin.retrofit.RetrofitGenerator;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@ActionConfiguration( actionGroup = "RestServiceActions" )
public class GenerateRetrofitAction extends AbstractSoapUIAction<RestService> {

    private XFormDialog dialog;

    public GenerateRetrofitAction()
    {
        super( "Generate Retrofit Interface", "Generates a Retrofit Java Interface for this REST API");
    }

    @Override
    public void perform(RestService restService, Object o) {
        if( dialog == null )
        {
            dialog = ADialogBuilder.buildDialog( Form.class );
            dialog.setValue( Form.NAME, RetrofitGenerator.namify( restService.getName() ));
            dialog.setBooleanValue( Form.PREFIX, true );
        }

        XFormOptionsField resources = (XFormOptionsField) dialog.getFormField( Form.RESOURCES);
        StringToObjectMap nameToResourceMap = new StringToObjectMap();
        StringList names = new StringList();

        for(RestResource resource : restService.getAllResources())
        {
            String fullPath = resource.getFullPath(false);
            nameToResourceMap.put(fullPath, resource);
            names.add(fullPath);
        }

        resources.setOptions( names.toStringArray() );
        resources.setSelectedOptions( names.toStringArray() );

        if( dialog.show() )
        {
            RetrofitGenerator generator = new RetrofitGenerator( restService );
            generator.setAsync( dialog.getBooleanValue( Form.ASYNC ));
            generator.setUseResourceName( dialog.getBooleanValue( Form.USERESOURCENAME ));
            generator.setPrefix( dialog.getBooleanValue( Form.PREFIX));
            generator.setIgnoreHeaders( dialog.getBooleanValue(Form.IGNORE_HEADERS));

            List<RestResource> selectedResources = new ArrayList<RestResource>();

            for( Object name : resources.getSelectedOptions())
            {
                RestResource e = (RestResource) nameToResourceMap.get(String.valueOf(name));
                if( e != null )
                    selectedResources.add(e);
            }

            if( !selectedResources.isEmpty() )
                generator.setResources( selectedResources );

            File file = generator.generate( dialog.getValue(Form.PACKAGE), dialog.getValue( Form.NAME), dialog.getValue(Form.FOLDER));
            if( file != null && file.exists() && UISupport.confirm("Open generated interface with system viewer", "Generate Retrofit Interface"))
            {
                try {
                    SoapUITools.openURL(file.toURI().toURL().toString());
                } catch (MalformedURLException e) {
                    SoapUI.logError(e);
                }
            }

            Analytics.trackAction(AnalyticsManager.Category.CUSTOM_PLUGIN_ACTION, "GenerateRetrofitInterface");
        }
    }

    @AForm( name = "Generate Retrofit Interface", description = "Generates a Retrofit Java Interface for this REST Service" )
    public interface Form
    {
        @AField( name = "Resources", description = "Select which resources to include", type = AField.AFieldType.MULTILIST )
        public final static String RESOURCES = "Resources";

        @AField( name = "Package", description = "The package name for the generated interface", type = AField.AFieldType.STRING )
        public final static String PACKAGE = "Package";

        @AField( name = "Name", description = "The class name for the generated interface", type = AField.AFieldType.STRING )
        public final static String NAME = "Name";

        @AField( name = "Target Folder", description = "Where to save the interface", type = AField.AFieldType.FOLDER )
        public final static String FOLDER = "Target Folder";

        @AField( name = "Prefix Method Name", description = "Prefix generated Method names with HTTP VERB name", type = AField.AFieldType.BOOLEAN )
        public final static String PREFIX = "Prefix Method Name";

        @AField( name = "Ignore header parameters", description = "Ignores HTTP Header parameters when generating methods", type = AField.AFieldType.BOOLEAN )
        public final static String IGNORE_HEADERS = "Ignore header parameters";

        @AField( name = "Generate Async", description = "Generate asynchronous method calls (using Callbacks)", type = AField.AFieldType.BOOLEAN )
        public final static String ASYNC = "Generate Async";

        @AField( name = "Use Resource Name", description =  "Use Resource names for method names when possible", type = AField.AFieldType.BOOLEAN)
        public final static String USERESOURCENAME = "User Resource Name";
    }
}
