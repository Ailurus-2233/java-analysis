# 一个Java依赖库的特征提取/检测工具

## 1. 介绍

一个Java依赖库的特征提取/检测工具，可以提取Java依赖库的特征，也可以通过特征来检测依赖库。

## 2. 使用说明

``` bash
java -jar java-analysis.jar -m [mode] -i [input] -o [output] <-b>
```

各个参数的具体说明如下表所示

|     参数名称      |                     参数含义                     |              示例              |      默认值      |
|:-------------:|:--------------------------------------------:|:----------------------------:|:-------------:|
|  -m/--model   |            模式，提取特征，检测依赖库，或者下载依赖库             | -m analysis/predict/download |    predict    |
|  -b/--batch   |    是否使用batch模式，如果使用batch模式，那么输入文件必须是CSV文件    |              -b              |     false     |
|  -i/--input   | 输入文件，可以是一个jar包，也可以是一个CSV文件，在download模式中是下载链接 |     -i /home/xxx/xxx.jar     |     null      |
|  -o/--output  |            输出文件，可以指定文件名，也可以指定文件夹             |    -o /home/xxx/xxx.json     |   ./output    |
|  -c/--config  |     配置文件，如果不指定，那么会使用默认的配置文件，配置文件的格式为JSON     |     -c /xxx/config.json      | ./config.json |
|  -g/--group   |              分析模式需要记录文件的groupId              |    -g org.apache.commons     |      ""       |
| -a/--artifact |            分析模式需要记录文件的artifactId             |       -a commons-lang3       |      ""       |
| -v/--version  |              分析模式需要记录文件的version              |            -v 3.4            |      ""       |
|  -t/--thread  |                 下载模式中启用的线程数量                 |            -t 10             |      10       |

