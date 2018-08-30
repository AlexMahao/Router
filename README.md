## Android路由框架——Router

> 主要用于组件化的实现，以`url`的形式完成页面的跳转

### 支持功能

- 以url的形式进行跳转 :`router://app/params`
- 支持参数的传入和解析 : `router://app/params?index=2`
- 支持添加拦截器(Inteceptor)，自定义`url`扩展，例如登录拦截
- 支持添加`intent flag`
- 对于传入参数支持异常销毁和恢复
- 支持不同模块之间的页面跳转
- 支持以注解`@Route`的形式声明页面路径
- 支持以注解`@Autowired`的形式声明入参
- 支持生成对应的路由的清单文件
- 支持`Activity Result`回调
- 支持拦截器的阻断操作

### TODO

- 参数的编解码的支持
- 拦截器中优先级的优化
- webView的支持 （？？框架内支持还是应用扩展）
- `Fragment`跳转

### 使用方式

> 暂不提供jcenter等外部直接依赖的方式

#### 添加依赖以及引用`gradle`插件

在工程根目录的`build.gradle`中添加`gradle`插件

```java
classpath 'com.spearbothy:router-plugin:0.0.1'
```

在对应模块下的`build.gradle`下添加引用

```java
apply plugin: 'plugin.router'
```

添加对应`module`的唯一标识，用于标识该模块下的所有协议

```java
android {
    ...
    defaultConfig {
        ...
                javaCompileOptions {
                    annotationProcessorOptions {
                        arguments = [moduleName: 'app']
                    }
                }
    }
   ...
}
```

#### 框架初始化

在工程的`Application`的实现类的`onCreate()`中添加如下初始化代码

```java
// 初始化
Router.init(getApplicationContext(), new AutowiredJsonAdapter());
// 设置debug模式
Router.setDebug(BuildConfig.DEBUG);
```

如果有需要，可以添加自定义拦截器

```java
// 添加登录拦截
Router.addInterceptor(new LoginInterceptor());
```
**添加自定义解析**

抽离最初使用`fastJson`改为使用`adapter`的方式，完成`json`的解析。

自定义`json`适配器

```java
   class SimpleJsonAdapter implements AutowiredJsonAdapter {

        @Override
        public <T> T JSON2Object(String s, Class<T> clazz) {
            return JSON.parseObject(s, clazz);
        }

        @Override
        public String object2JSON(Object o) {
            return JSON.toJSONString(o);
        }
    }

```

在初始化的时候传入：

```java
Router.init(getApplicationContext(), new SimpleJsonAdapter());
```

#### 声明页面路径

在具体的`Activity`上，添加`@Route`注解，声明页面路径

```java
@Route(path = "/main", desc = "首页", version = "1.0.0")
public class MainActivity extends BaseDebugListActivity {
	//...
}

```

- `path`：页面路径
- `desc`：页面注释，用于查阅和生成清单文件
- `version`: 扩展字段，根据后期需要添加功能拓展

#### 页面跳转

提供两种方式跳转：全路径`url`和拆分方法。

- 全路径`url`:

```java
Router.with(this)
	.url("router://app/params")
	.start(new SimpleResultCallback());
```

- 拆分方法

```java
Router.with(this)
	.module("app")
	.path("/params")
	.start(new SimpleResultCallback());
```

#### 自定义参数

存在一些页面中需要参数，可以通过`@Autowired`声明，使用如下

```java
 @Autowired(value = "10", desc = "toast显示偏移值")
 public int index;
```

> 对于使用`@Autowired`标注的字段会默认支持异常销毁的恢复，不需要添加其他代码。


跳转时传入对应字段即可，两种方式如下：

```java
Router.with(this)
	.url("router://app/params?index=2")
	.start(new SimpleResultCallback());
```

```java
Router.with(this)
	.module("app")
	.path("/params")
	.param("index", "2")
	.start(new SimpleResultCallback());
```

> 其中有两个保留参数：`intentFlag`和`activityReqCode`,这两个参数不能用于参数的key。


##### 参数注解扩展支持

为了在使用时做到最小改动，兼容老版本，`@Autowired`添加`enable`属性，该属性默认值为`true`，如果为`false`，则在使用路由协议方式跳转时，仅做参数的校验和传入，不做自动获取和异常恢复，使用如下。

```java
@Route(path = "/user_params", desc = "测试所有类型数据", version = "1.0.0")
public class UserParamsActivity extends AppCompatActivity {

    @Autowired
    public User user;


    @Autowired(enable = false)
    public int index; // 不做自动获取

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_params);
        TextView info = (TextView) findViewById(R.id.info);
        info.setText(user.toString());

        // 手动获取参数
        int index = getIntent().getIntExtra("index", 0);
        Toast.makeText(getApplicationContext(), this.index + "--" + index, Toast.LENGTH_SHORT).show();
    }
}

```

#### 生成清单文件示例

```java
{
	"name":"test",
	"pathList":[
		{
			"className":"com.spearbothy.test.MainActivity",
			"desc":"module 测试页面",
			"params":[
				{
					"desc":"",
					"enable":true,
					"fieldName":"test",
					"fieldType":"java.lang.String",
					"value":"123"
				}
			],
			"path":"router://test/main",
			"version":"1.0.0"
		}
	]
}

```

清单文件中主要包含以下关键信息：

- 模块名称和标识
- 该模块下所有的路由协议
- 每一个协议对应跳转的`Activity`以及路径和描述信息。
- 该`Activity`下被`@Autowired`标识的所有字段以及字段的类型，字段的默认值和字段的描述。

#### 跳转时添加`intent flag`

- 以拆分方法的形式

```java
Router.with(MainActivity.this)
	.module("app")
	.path("/main")
	.addIntentFlag(Intent.FLAG_ACTIVITY_SINGLE_TOP)
	.addIntentFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
	.start();
```

- 以全路径`url`的方式

```
 Router.with(MainActivity.this)
 	.url("router://app/main?intentFlag=[67108864,536870912]")
 	.start(new SimpleResultCallback());
```

其中`intentFlag`为参数，其值为整形数组，为对应`flag`的整形值。


#### 使用`reqCode`获取`activity result`

在页面跳转时，通常会监听关闭页面的结果，该框架支持跳转页面时传入`reqCode`用来获取页面回调。

- 以全路径`url`的方式

```java
Router.with(this)
	.url("router://app/request_code?activityReqCode=" + REQUEST_CODE)
	.start(new SimpleResultCallback());

```

- 以拆分方法的形式

```java
Router.with(this)
	.module("app")
	.path("/request_code")
	.activityRequest(REQUEST_CODE)
	.start();

```

#### 添加自定义`Interceptor`

提供拦截器功能，类似于`okHttp`中的自定义拦截器，实现对请求或者响应的自定义。

编写类实现`Interceptor`

```java
public class LoginInterceptor implements Interceptor {

    private static final int CODE_FAIL_NO_LOGIN = -2001;

    @Override
    public Response intercept(Chain chain) {
        RouterRequest request = chain.request();
        // 获取自定义参数
        Map<String, String> params = request.getParams();
        // 获取登录自动
        String needLogin = params.get("needLogin");
        if ("true".equals(needLogin)) {
            // 如果需要登录，但当前未登录，则跳转登录页面
            if (!App.sIsLogin) {
                // 启动新的路由
                gotoLogin(request.getContext());
                // 取消此次请求
                Response response = new Response();
                response.setError(Response.CODE_CANCEL, "");
                return response;
            }
        }
        return chain.proceed(request);
    }

    private void gotoLogin(Context context) {
        Router.with(context)
                .url("router://app/login?version=1.0")
                .start(new ResultCallback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(Response response) {
                        Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


```

在`Router`初始化时添加拦截器

```java
 // 添加登录拦截
 Router.addInterceptor(new LoginInterceptor());

```

#### 协议分析

```
router://app/params?index=2&needLogin=true

```
对于路由框架中页面跳转的协议一般如下，`router://`为该框架的协议标识，`app`代表不同的`module`，在`module`下的`build.gradle`中进行标识，`/params`标识某一模块下的页面路径。`?`后面为传入该页面的参数，如果页面中添加有`@Autowired`字段，会自动匹配和解析，同时该字段会解析成`map`集合，在拦截器中可以根据需求进行不同功能的扩展。

### 实现原理

#### 项目结构

- `annotation` : 声明注解以及注解参数。
- `compiler` : 生成辅助类以及生成清单文件相关
- `plugin` : `gradle`插件，主要用于添加依赖，以及修改现有代码，添加异常恢复相关代码。
- `api` : 公开api，用于运行时期解析`url`，加载清单文件，完成页面跳转。


#### `annotation`分析

该库主要声明注解以及注解的参数，主要包含两个注解的声明

**`@Route`**

```java
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Route {

    String path();

    String desc();

    String version();
}
```

**`@Autowired`**

```java
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Autowired {

    String desc() default "";

    /**
     * @return 默认值，如果设置默认值则参数可以不传，会取默认值
     */
    String value() default "";
}
```

声明一个注解，主要关注两方面，注解的生命周期和注解的目标类型，主要由`@Retention`和`@Target`确定。


`@Retention`可取值如下：

```java
public enum RetentionPolicy {
    SOURCE,
    CLASS,
    RUNTIME;
}
```

`@Target`可取值如下：

```java

public enum ElementType {
    TYPE,
    FIELD,
    METHOD,
    PARAMETER,
    CONSTRUCTOR,
    LOCAL_VARIABLE,
    ANNOTATION_TYPE,
    PACKAGE,
    TYPE_PARAMETER,
    TYPE_USE;
}
```


#### `compiler`分析

该框架主要用于在编译器生成路由协议加载的辅助代码和参数的注入以及异常恢复的辅助代码。

该框架的核心类为`RouterProcess`，该类继承`AbstractProcessor`,该类会在编译时调用，获取代码中的注解声明，根据需求生成代码。

继承该类需要实现如下关键方法

```java
 @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 初始化方法，初始一些必要参数
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 获取注解，处理并生成相关辅助代码
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 声明支持的注解类型
        return super.getSupportedAnnotationTypes();
    }

```

那么，我们主要关注处理方法，对于该框架，其主要流程如下：

- 获取所有的`@Route`注解标注的类，并保存。
- 获取所有`@Autowired`注解的字段并保存。
- 生成所有路由协议的加载辅助类
- 生成参数的注入和异常销毁的辅助类
- 生成清单文件

基于如上流程，开始细节分析：

在分析之前看一下保存相关注解的类的结构

```java
public class Addition {
    /**
     * 注解所在的全路径类名
     */
    private String qualifiedName;
    /**
     * 被@Route注解的类以及相关信息
     */
    private RouteClass routeClass;
    /**
     * 被@Autowired注解的字段的相关信息
     */
    private List<AutowiredField> autowiredFields = new ArrayList<>();
}

```

##### 获取所有的`@Route`注解标注的类，并保存

```java
/**
     * 加载@Route注解的类信息
     * @param additions 保存的实体
     * @param roundEnv 获取注解的对象
     */
    private void initRoute(List<Addition> additions, RoundEnvironment roundEnv) {
        Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (routeElements != null && !routeElements.isEmpty()) {
            for (Element element : routeElements) {
                TypeElement typeElement = (TypeElement) element;
                Route route = typeElement.getAnnotation(Route.class);
                String qualifiedName = typeElement.getQualifiedName().toString();
                Addition addition = findAddition(additions, qualifiedName);
                if (addition == null) {
                    addition = new Addition();
                    addition.setQualifiedName(qualifiedName);
                    additions.add(addition);
                }
                RouteClass routeClass = new RouteClass();
                routeClass.setClassName(ClassName.get(typeElement));
                routeClass.setDesc(route.desc());
                routeClass.setPath(route.path());
                routeClass.setVersion(route.version());
                addition.setRouteClass(routeClass);
            }
        }
    }

```

因为一个类的信息可能已经存在，只需要添加对应的注解信息，所以需要先从集合中查询该类是否存在，如果存在，则只需要添加对应注解信息，如果不存在，则创建。

查询方法如下：

```java
 /**
     * 查询当前类的注解信息是否已存在
     * @param additions 查询的集合
     * @param qualifiedName 全路径类名
     * @return 如果查询到，则返回对应信息，如果未查询到，则返回null
     */
    public Addition findAddition(List<Addition> additions, String qualifiedName) {
        for (Addition addition : additions) {
            if (addition.getQualifiedName().equals(qualifiedName)) {
                return addition;
            }
        }
        return null;
    }

```

##### 获取所有`@Autowired`注解的字段并保存

```
/**
     * 加载@Route注解的类信息
     * @param additions 保存的实体
     * @param roundEnv 获取注解的对象
     */
    private void initRoute(List<Addition> additions, RoundEnvironment roundEnv) {
        Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (routeElements != null && !routeElements.isEmpty()) {
            for (Element element : routeElements) {
                // 检查element的类型
                TypeElement typeElement = (TypeElement) element;
                Route route = typeElement.getAnnotation(Route.class);
                String qualifiedName = typeElement.getQualifiedName().toString();
                Addition addition = findAddition(additions, qualifiedName);
                if (addition == null) {
                    addition = new Addition();
                    addition.setQualifiedName(qualifiedName);
                    additions.add(addition);
                }
                RouteClass routeClass = new RouteClass();
                routeClass.setClassName(ClassName.get(typeElement));
                routeClass.setDesc(route.desc());
                routeClass.setPath(route.path());
                routeClass.setVersion(route.version());
                addition.setRouteClass(routeClass);
            }
        }
    }
```

因为注解是作用于字段上，而异常恢复和参数注入只能作用于`Activity`上，所以需要检查当前类是否是`Activity`的基类.

```java
 /**
     * 检查一个类是否是一些类中其中一个的自雷
     * @param element 需要检查的类
     * @param superClasses 目标父类
     * @return 如果是则返回true
     */
    private boolean checkIsSubClassOf(Element element, String... superClasses) {
        Elements elementUtils = processingEnv.getElementUtils();
        Types typeUtils = processingEnv.getTypeUtils();
        for (String clazz : superClasses) {
            try {
                boolean isSubType = typeUtils.isSubtype(element.asType(), elementUtils.getTypeElement(clazz).asType());
                if (isSubType) return true;
            } catch (Throwable throwable) {
                logger.info(throwable.getMessage());
                continue;
            }
        }
        return false;
    }

```

##### 生成所有路由协议的加载辅助类

看一下生成的目标辅助类如下：

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.spearbothy.router.loader;

import com.spearbothy.router.api.entity.RouteAddition;
import com.spearbothy.router.api.router.IRouterLoader;
import com.spearbothy.router.test.MainActivity;
import java.util.Map;

public class RouterLoader$$test implements IRouterLoader {
    public RouterLoader$$test() {
    }

    public void loadInto(Map<String, RouteAddition> root) {
        RouteAddition routeAddition = null;
        routeAddition = new RouteAddition(MainActivity.class, "module 测试页面", "1.0.0");
        routeAddition.addAutowiredField("test", "java.lang.String", "", "123");
        root.put("/main", routeAddition);
    }

    public String getModuleName() {
        return "test";
    }
}
```

实现的`IRouterLoader`接口，该接口定义于`api`库中，主要是便于运行时加载清单文件。


提供两个方法：

- `loadInto`: 加载所有的协议信息
- `getModuleName` ： 标识当前模块


怎么生成如下方法，可以借助`javapoet`框架，该框架是一个生成`java`文件的第三方库，

```java
/**
     * 生成加载路由协议辅助类，目标路径pcom.spearbothy.router.loader.RouterLoader$$XXX.java
     */
    private void generatorRouteFile(List<Addition> additionList) throws IOException {

        ClassName InterfaceName = ClassName.get(Constants.LOADER_INTERFACE_PACKAGE, Constants.LOADER_INTERFACE_NAME);

        ClassName routeAddition = ClassName.get(Constants.ROUTER_PACKAGE + ".api.entity", "RouteAddition");

        ParameterizedTypeName routerMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                routeAddition
        );

        ParameterSpec parameterSpec = ParameterSpec.builder(routerMapType, "root").build();

        MethodSpec.Builder loadIntoBuilder = MethodSpec.methodBuilder(Constants.LOADER_INTERFACE_LOADINTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);

        loadIntoBuilder.addStatement("$T routeAddition = null", routeAddition);
        for (Addition addition : additionList) {
            RouteClass routeClass = addition.getRouteClass();
            if (routeClass != null) {
                loadIntoBuilder.addStatement("routeAddition = new $T($T.class, $S, $S)", routeAddition, routeClass.getClassName(), routeClass.getDesc(), routeClass.getVersion());
                for (AutowiredField field : addition.getAutowiredFields()) {
                    loadIntoBuilder.addStatement("routeAddition.addAutowiredField($S, $S, $S, $S)", field.getFieldName(), field.getFieldType(), field.getDesc(), field.getValue());
                }
                loadIntoBuilder.addStatement("root.put($S, routeAddition)", routeClass.getPath());
            }
        }

        // 写入当前编译
        MethodSpec loadInto = loadIntoBuilder.build();

        MethodSpec getModuleName = MethodSpec.methodBuilder(Constants.LOADER_INTERFACE_GET_MODULE_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", moduleName)
                .returns(String.class)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(Constants.ROUTER_LOADER_CLASS_NAME + moduleName)
                .addSuperinterface(InterfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(loadInto)
                .addMethod(getModuleName)
                .build();

        JavaFile javaFile = JavaFile.builder(Constants.ROUTER_LOADER_PACKAGE, typeSpec)
                .build();

        javaFile.writeTo(filer);

        // 添加logger信息
        logger.infoLine("生成类信息");
        logger.info(typeSpec.toString());
        logger.infoLine("");
        logger.info(moduleName + " success !!! ");
    }

```

- 构造一些基于`javapoet`的类类型。
- 构造`loadInto`方法
- 构造`getModuleName`方法
- 构造辅助类，并生成文件
- 输出logger信息

##### 生成所有路由协议的加载辅助类

生成辅助类如下

```java
package com.spearbothy.router.test;

import android.os.Bundle;

public final class MainActivity$$RouteAutowired {
    public MainActivity$$RouteAutowired() {
    }

    public static void onSaveInstanceState(MainActivity instance, Bundle outState) {
        outState.putString("test", instance.test);
    }

    public static void onRestoreInstanceState(MainActivity instance, Bundle outState) {
        instance.test = outState.getString("test");
    }
}
```

和上一个方法类似，不在重复。


##### 生成清单文件

```java
/**
     * 根据所有类的注解信息，生成清单文件，便于查阅
     */
    private void generatorMainfest(List<Addition> additionList) {
        RouterDetail detail = new RouterDetail();
        detail.setName(moduleName);

        for (Addition addition : additionList) {
            RouterDetail.Path path = new RouterDetail.Path(addition.getQualifiedName());
            path.setPath(addition.getRouteClass().getPath(), moduleName, Constants.ROUTER_PROTOCOL);
            path.setDesc(addition.getRouteClass().getDesc());
            path.setVersion(addition.getRouteClass().getVersion());
            path.setParams(addition.getAutowiredFields());
            detail.addPath(path);
        }

        try {
            List<String> lines = Arrays.asList(JSON.toJSONString(detail, true));
            Files.createDirectories(Paths.get(Constants.ROUTER_DETAIL_DIR));
            Path file = Paths.get(Constants.ROUTER_DETAIL_DIR + File.separator + moduleName + ".json");
            logger.info("协议清单文件 PATH: " + file.toAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

```



#### `api`框架分析
