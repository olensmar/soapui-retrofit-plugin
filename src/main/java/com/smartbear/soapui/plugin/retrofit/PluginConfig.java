package com.smartbear.soapui.plugin.retrofit;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

/**
 * Created by ole on 08/06/14.
 */

@PluginConfiguration( groupId = "com.smartbear.soapui.plugins", name = "Retrofit Plugin", version = "0.7",
    autoDetect = true, description = "Generates Retrofit Interfaces from REST APIs",
    infoUrl = "https://github.com/olensmar/soapui-retrofit-plugin")
public class PluginConfig extends PluginAdapter {
}
