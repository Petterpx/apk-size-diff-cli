# APK Size Diff CLI

**APK Size Diff CLI** 是一个用于比较两个APK文件大小差异的cli，该工具可用于帮助你了解应用程序大小的变化情况，从而实现apk体积监控。

## 简单用法

```shell
java -jar apk_size_diff_cli -b test/base.apk -c test/current.apk -d test/result/ -tss 102410
```

> jar包下载地址请前往最新的[release版本](https://github.com/Petterpx/apk-size-diff-cli/releases)下载.

**效果示例：**

![image-20230428161644957](https://img.tucang.cc/api/image/show/34a16a5711a83e3158021078244a4286)

最终会在给定的输出路径，生成一个名为 `apk_size_diff.md` 的文件。

## Tips

当然你也可以使用 **exec** 可执行文件，从而免除 `java -jar` 命令。

> 前往最新的 [release](https://github.com/Petterpx/apk-size-diff-cli/releases) 版本，下载 **apk-size-diff-cli.zip** 解压即可，路径 bin/apk-size-diff-cli

**使用方式：**

```shell
apk_size_diff_cli -b test/base.apk -c test/current.apk -d test/result/ -tss 102410
```

## 配置选项

默认提供了一些配置选项，从而便于更好的使用。你可以使用 `--help` 选项查看所有选项的列表和说明。

```shell
Options:
  -h, --help    
  -b, --baseline_apk PATH  Baseline Apk Path
  -c, --current_apk PATH   Current Apk Path
  -d, --diff_output PATH   Diff Output Path
  -t, --threshold TEXT     Apk threshold. Input example: apk:102400
  -ts, --thresholds TEXT   Apk threshold. Input example: apk:102400,res:102400
  -tss, --thresholdsBase INT  The basic threshold will be applied to all sub-levels.      
```



## 设置阈值

可以通过命令传参的方式，设置一系列阈值，从而实现在CI中断流程。

```shell
// 方式1，多参数
java ... -t apk:102400 -t res:102400 

// 方式2，以 xx:number,xx2:number 这种方式
java ... -ts apk:102400,res:102400

// 方式3，这种方式将默认所有分类都是同一种
java ... -tss 102400

- 优先级
方式2>方式1>方式3，后者会覆盖前者(如果该类型存在)
```

支持的阈值有：

- apk
- dex
- arsc
- manifest
- lib
- res
- assets
- META_INF
- other

**示例如下：**

```shell
java -jar apk_size_diff_cli -b test/base.apk -c test/current.apk -d test/result/ -t apk:102400
```



## CI 联动

[当然更好的使用场景是](https://img.tucang.cc/api/image/show/86e197b78322cec6300e73780fb06b3a)，你可以在Github Action中使用该Cli，从而实现apk流水线体积监控。

**示例如下：**

```yml
...
- name: Run Diff apk size
  continue-on-error: true
  run: |
    java -jar check/apk-size-diff-cli.jar -b $APK_PATH/android-base.apk -c $APK_PATH/android-new.apk -d $APK_DIFF_OUTPUT_PATH -tss $KB500

- uses: marocchino/sticky-pull-request-comment@v2.6.2
  with:
    recreate: true
    path: ${{ env.APK_DIFF_OUTPUT_PATH }}/apk_size_diff.md
```

![image-20230428162303488](https://img.tucang.cc/api/image/show/86e197b78322cec6300e73780fb06b3a)

## 需要更多功能

目前功能比较单一，如果需要更多支持，欢迎提 issues。
