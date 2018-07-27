## Router 路由框架

通过类似http协议的形式启动`activity`，减少对`activity`的直接依赖，利于后续组件化，插件化等的使用。

### 支持功能

- 通过注解的形式配置Activity的路径
- 通过http协议的方式跳转目标`Activity`
- 协议中支持添加参数，目标`Activity`中支持参数自动获取，并处理异常销毁，无需手动处理
- 自定义`Interceptor`，实现拦截器功能
- 生成路径的清单文件

### 版本列表

- 0.0.4:支持传参以及异常销毁和恢复 （未上传依赖和升级版本）
- 0.0.3:添加`interceptor`,并添加`VersionInterceptor` （未上传依赖和升级版本）
- 0.0.2:重构项目 （未上传依赖和升级版本）
- 0.0.1:实现最基础的页面跳转功能 （处于学习阶段，便于理解）（未上传依赖和升级版本）


### TODO

- 代码中调用时无需完整url构造，提供对应分解方法
- 拦截器实现打断方法，阻拦链式调用执行

### 使用方式

> 暂不提供对外的仓库访问。仅是本地module的依赖说明。

> 暂时忽略`version`相关字段，没想好怎么处理


#### 添加依赖

在工程根目录下的`build.gradle`添加依赖

```
classpath 'com.spearbothy:router-plugin:+'

```

在对应`module`下的`build.gradle`引入插件

```
apply plugin: 'plugin.router'

```


#### 声明辅助类的全路径包名（唯一）

```java
android {
    ...
    defaultConfig {
        ...
         // 声明唯一包名，用于确定辅助类生成位置
                javaCompileOptions {
                    annotationProcessorOptions {
                        arguments = [moduleName: 'app']
                    }
                }
    }
   ...
}

```

#### `activity`添加注解，确定地址

```java

Router(path = "/main", desc = "首页")
public class MainActivity extends AppCompatActivity {
}
```

#### 初始化

```java
         // 初始化module
         Router.init(getApplicationContext());
         // 设置debug模式
         Router.setDebug(BuildConfig.DEBUG);
```

#### 页面跳转

```
 Router.with(this)
                .url("router://test/main")
                .start(new ResultCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "页面跳转成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(RouterResponse response) {
                        Toast.makeText(getApplicationContext(), response.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                });
```

#### 地址说明

- `router`协议标识
-  `test`:模块标识，和greadle中声明的统一
- `/main2`：注解上声明的路径名


#### 参数使用


`Activity`中对应字段添加注解 `@Autowired`

```java
@Route(path = "/params", desc = "演示参数传值页面", version = "1.0.0")
public class ParamsActivity extends AppCompatActivity {

    @Autowired
    public int index = 1;

    // ...
}

```

协议中添加对应参数

```
"router://app/params?version=1.0&index=2"
```

#### 添加`Interceptor`

> 登录和未登录进行页面拦截

```
router://app/params?version=1.0&index=2&needLogin=true
```

自定义登录拦截器

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
            if (!MainActivity.sIsLogin) {
                // 启动新的路由
                gotoLogin(request.getContext());

                Response response = new Response();
                response.setError(CODE_FAIL_NO_LOGIN, "请先登录");
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



#### 生成清单文件

在工程目录下会创建`router`文件夹，里面包含配置的协议信息，具体内容如下

```
{
	"name":"app",
	"pathList":[
		{
			"className":"com.spearbothy.router.MainActivity",
			"desc":"首页",
			"params":[],
			"path":"router://app/main",
			"version":"1.0.0"
		},
		{
			"className":"com.spearbothy.router.ParamsActivity",
			"desc":"演示参数传值页面",
			"params":[
				{
					"fieldName":"index",
					"fieldType":"int"
				}
			],
			"path":"router://app/params",
			"version":"1.0.0"
		}
	]
}


```

### 其他

> 一直前行

