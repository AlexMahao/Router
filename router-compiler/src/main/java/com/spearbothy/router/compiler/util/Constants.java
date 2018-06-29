package com.spearbothy.router.compiler.util;

/**
 * Created by android-dev on 2018/6/26.
 */

public class Constants {

    // 在gradle声明，生成router的唯一标识
    public static final String MODULE_NAME_KEY = "moduleName";

    public static final String ROUTER_PACKAGE = "com.spearbothy.router";

    public static final String ROUTER_LOADER_PACKAGE = ROUTER_PACKAGE + ".loader";

    public static final String ROUTER_LOADER_CLASS_NAME = "RouterLoader$$";

    public static final String LOADER_INTERFACE_PACKAGE = ROUTER_PACKAGE + ".api.router";

    public static final String LOADER_INTERFACE_NAME = "IRouterLoader";

    public static final String LOADER_INTERFACE_LOADINTO = "loadInto";

    public static final String LOADER_INTERFACE_GET_MODULE_NAME = "getModuleName";


}
