APK parser for Android based on [CaoQianLi's apk-parser](https://github.com/CaoQianLi/apk-parser)

[![Software License](https://img.shields.io/badge/license-BSD%203%20Clause-blue.svg)](LICENSE.txt) 
#### Features
* Retrieve basic apk metas, such as title, icon, package name, version, etc.
* Parse and convert binary xml file to text 
* Classes from dex file
* Get certificate metas and verify apk signature

#### Get apk-parser
You can download the [latest JAR](latest/apk-parser.jar) here or build the project yourself.

#### Usage
The easiest way is to use the ApkParser class, which contains convenient methods to get AndroidManifest.xml, apk meta infos, etc.
#####1. Apk meta info
ApkMeta contains name(label), packageName, version, sdk, used features, etc.
```java
PackageManager pm = getPackageManager();
ApplicationInfo appInfo = pm.getApplicationInfo("com.facebook.katana", 0);
ApkParser apkParser = ApkParser.create(appInfo);
ApkMeta meta = apkParser.getApkMeta();
String packageName = meta.packageName;
long versionCode = meta.versionCode;
List<UseFeature> usesFeatures = meta.usesFeatures;
List<String> requestedPermissions = meta.usesPermissions;
```
#####2. Get binary xml and manifest xml file
```java
ApplicationInfo appInfo = getPackageManager().getApplicationInfo("some.package.name", 0);
ApkParser apkParser = ApkParser.create(appInfo);
String readableAndroidManifest = apkParser.getManifestXml();
String xml = apkParser.transBinaryXml("res/layout/activity_main.xml");
```
#####3. Get dex classes
```java
ApplicationInfo appInfo = getPackageManager().getApplicationInfo("com.instagram.android", 0);
ApkParser apkParser = ApkParser.create(appInfo);
for (DexClass dexClass : apkParser.getDexClasses()) {
  System.out.println(dexClass.classType);
}
```

#####4. Get certificate and verify apk signature
```java
ApplicationInfo appInfo = getPackageManager().getApplicationInfo("com.instagram.android", 0);
ApkParser apkParser = ApkParser.create(appInfo);
if (apkParser.verifyApk() == ApkParser.ApkSignStatus.SIGNED) {
  System.out.println(apkParser.getCertificateMeta().signAlgorithm);
}
```

#####5. Locales
Apk may return different infos(title, icon, etc.) for different region and language, which is 
determined by Locales.
If the locale is not set, the "en_US" locale(<code>Locale.US</code>) is used. You can set the 
locale like this:
```java
ApkParser apkParser = ApkParser.create(filePath);
apkParser.setPreferredLocale(Locale.SIMPLIFIED_CHINESE);
ApkMeta apkMeta = apkParser.getApkMeta();
```
The PreferredLocale parameter work for getApkMeta, getManifestXml, and other binary xmls.
Apk parser will find best match languages with locale you specified.

If locale is set to null, ApkParser will not translate resource tag, just give the resource id.
For example, apk title will be '@string/app_name' instead of 'WeChat'.
