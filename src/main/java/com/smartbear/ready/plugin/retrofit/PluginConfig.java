package com.smartbear.ready.plugin.retrofit;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

/**
 * Created by ole on 08/06/14.
 */

@PluginConfiguration( groupId = "com.smartbear.ready.plugins", name = "Retrofit Plugin", version = "1.0.1",
    autoDetect = true, description = "Generates Retrofit Interfaces from REST APIs",
    infoUrl = "https://github.com/olensmar/soapui-retrofit-plugin")
public class PluginConfig extends PluginAdapter {
}
