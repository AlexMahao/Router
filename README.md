## Router 路由框架

通过类似http协议的形式启动`activity`，减少对`activity`的直接依赖，利于后续组件化，插件化等的使用。

### 支持功能

- 自动生成辅助类
- 通过注解的形式声明`activity`的地址
- 通过协议的方式调用`activity`
- 不同module之间的页面跳转

### 版本列表

- 0.0.1:实现最基础的页面跳转功能 （处于学习阶段，便于理解）
- 0.0.2:重构项目

### TODO

- 支持传参
- 支持自动绑定参数
- 生成`activity`和对应地址的列表
- 优化代码生成方式
- 支持启动页面动画过渡

### 使用方式

> 暂不提供对外的仓库访问。仅是本地module的依赖说明。

#### 添加项目依赖

```groovy
 // 生成辅助类代码
    annotationProcessor project(':router-compiler')
    // 注解声明
    implementation project(':router-annotation')
    // 页面跳转的api
    implementation project(':router-api')

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

#### 初始化 （加载初始化累，加载地址）

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



### 其他

> 一直前行

