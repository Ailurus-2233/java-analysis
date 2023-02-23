# 一个Java依赖库的特征提取/检测工具

## 1. 介绍

一个Java依赖库的特征提取/检测工具，可以提取Java依赖库的特征，也可以通过特征来检测依赖库。

## 2. 使用说明

``` bash
java -jar java-analysis.jar -m [mode] -i [input] -o [output] <-b>
```

各个参数的具体说明如下表所示

| 参数名称 |                     参数含义                     |                    示例                     |   默认值    |
|:----:|:--------------------------------------------:|:-----------------------------------------:|:--------:|
|  -m  |            模式，提取特征，检测依赖库，或者下载依赖库             | -m analysis_1/analysis_2/predict/download | predict  |
|  -b  |    是否使用batch模式，如果使用batch模式，那么输入文件必须是CSV文件    |                    -b                     |  false   |
|  -i  | 输入文件，可以是一个jar包，也可以是一个CSV文件，在download模式中是下载链接 |         -i /home/xxx/xxx.jar/xxx/         |   null   |
|  -o  |                     输出文件                     |         -o /home/xxx/xxx.jar/xxx/         | ./output |

