/*
 * Copyright (c) 2015, Jared Rummler
 * Copyright (c) 2015, Liu Dong
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jaredrummler.apkparser;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jaredrummler.apkparser.exception.ParserException;
import com.jaredrummler.apkparser.model.AndroidManifest;
import com.jaredrummler.apkparser.model.ApkMeta;
import com.jaredrummler.apkparser.model.CertificateMeta;
import com.jaredrummler.apkparser.model.DexClass;
import com.jaredrummler.apkparser.model.DexInfo;
import com.jaredrummler.apkparser.model.Icon;
import com.jaredrummler.apkparser.parser.ApkMetaTranslator;
import com.jaredrummler.apkparser.parser.BinaryXmlParser;
import com.jaredrummler.apkparser.parser.CertificateParser;
import com.jaredrummler.apkparser.parser.CompositeXmlStreamer;
import com.jaredrummler.apkparser.parser.DexParser;
import com.jaredrummler.apkparser.parser.ResourceTableParser;
import com.jaredrummler.apkparser.parser.XmlStreamer;
import com.jaredrummler.apkparser.parser.XmlTranslator;
import com.jaredrummler.apkparser.struct.AndroidConstants;
import com.jaredrummler.apkparser.struct.dex.DexHeader;
import com.jaredrummler.apkparser.struct.resource.ResourceTable;
import com.jaredrummler.apkparser.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.security.cert.CertificateException;

public class ApkParser implements Closeable {

  private static final Locale DEFAULT_LOCALE = Locale.US;

  public static ApkParser create(PackageManager pm, String packageName)
      throws PackageManager.NameNotFoundException {
    return new ApkParser(new File(pm.getApplicationInfo(packageName, 0).sourceDir));
  }

  public static ApkParser create(PackageInfo packageInfo) {
    return new ApkParser(new File(packageInfo.applicationInfo.sourceDir));
  }

  public static ApkParser create(ApplicationInfo applicationInfo) {
    return new ApkParser(new File(applicationInfo.sourceDir));
  }

  public static ApkParser create(String path) {
    return new ApkParser(new File(path));
  }

  public static ApkParser create(File file) {
    return new ApkParser(file);
  }

  private List<DexInfo> dexInfos; // multi-dex
  private DexInfo dex;
  private ResourceTable resourceTable;
  private AndroidManifest androidManifest;
  private String manifestXml;
  private ApkMeta apkMeta;
  private Set<Locale> locales;
  private CertificateMeta certificate;
  private final ZipFile zipFile;
  private File apkFile;
  private Locale preferredLocale = DEFAULT_LOCALE;

  private ApkParser(File file) throws InvalidApkException {
    try {
      apkFile = file;
      zipFile = new ZipFile(file);
    } catch (IOException e) {
      throw new InvalidApkException(String.format("Invalid APK: %s", file.getAbsolutePath()), e);
    }
  }

  /**
   * @return decoded AndroidManifest.xml
   */
  public String getManifestXml() throws IOException {
    if (manifestXml == null) {
      parseManifestXml();
    }
    return manifestXml;
  }

  /**
   * @return Object holding information about the AndroidManifest and it's declared activities,
   * services, receivers, intent-filters, etc.
   * @throws IOException
   *     if parsing the AndroidManifest failed.
   */
  public AndroidManifest getAndroidManifest() throws IOException, ParseException {
    if (androidManifest == null) {
      // TODO: clean up. We are parsing XML twice.
      androidManifest = new AndroidManifest(getApkMeta(), manifestXml);
    }
    return androidManifest;
  }

  /**
   * @return decoded AndroidManifest.xml
   */
  public ApkMeta getApkMeta() throws IOException {
    if (apkMeta == null) {
      parseApkMeta();
    }
    return apkMeta;
  }

  /**
   * get locales supported from resource file
   *
   * @return decoded AndroidManifest.xml
   */
  public Set<Locale> getLocales() throws IOException {
    if (locales == null) {
      parseResourceTable();
    }
    return locales;
  }

  public CertificateMeta getCertificateMeta() throws IOException, CertificateException {
    if (certificate == null) {
      parseCertificate();
    }
    return certificate;
  }

  private void parseCertificate() throws IOException, CertificateException {
    ZipEntry entry = null;
    Enumeration<? extends ZipEntry> enu = zipFile.entries();
    while (enu.hasMoreElements()) {
      ZipEntry ne = enu.nextElement();
      if (ne.isDirectory()) {
        continue;
      }
      if (ne.getName().toUpperCase().endsWith(".RSA") ||
          ne.getName().toUpperCase().endsWith(".DSA")) {
        entry = ne;
        break;
      }
    }
    if (entry == null) {
      throw new ParserException("ApkParser certificate not found");
    }
    InputStream in = zipFile.getInputStream(entry);
    CertificateParser parser = new CertificateParser(in);
    certificate = parser.parse();
    in.close();
  }

  private void parseApkMeta() throws IOException {
    if (manifestXml == null) {
      parseManifestXml();
    }
  }

  private void parseManifestXml() throws IOException {
    XmlTranslator xmlTranslator = new XmlTranslator();
    ApkMetaTranslator translator = new ApkMetaTranslator();
    XmlStreamer xmlStreamer = new CompositeXmlStreamer(xmlTranslator, translator);
    transBinaryXml(AndroidConstants.MANIFEST_FILE, xmlStreamer);
    manifestXml = xmlTranslator.getXml();
    if (manifestXml == null) {
      throw new ParserException("manifest xml not exists");
    }
    apkMeta = translator.getApkMeta();
  }

  /**
   * trans binary xml file to text xml file.
   *
   * @param path
   *     the xml file path in apk file
   * @return the text. null if file not exists
   * @throws IOException
   */
  public String transBinaryXml(String path) throws IOException {
    ZipEntry entry = Utils.getEntry(zipFile, path);
    if (entry == null) {
      return null;
    }
    if (resourceTable == null) {
      parseResourceTable();
    }
    try {
      XmlTranslator xmlTranslator = new XmlTranslator();
      transBinaryXml(path, xmlTranslator);
      return xmlTranslator.getXml();
    } catch (ParserException e) {
      // plain text file
      InputStream in = zipFile.getInputStream(entry);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
      byte[] buffer = new byte[8192];
      int length;
      while ((length = in.read(buffer)) != -1) {
        baos.write(buffer, 0, length);
      }
      in.close();
      return baos.toString("UTF-8");
    }
  }

  /**
   * get the apk icon file as bytes.
   *
   * @return the apk icon data,null if icon not found
   * @throws IOException
   */
  public Icon getIconFile() throws IOException {
    ApkMeta apkMeta = getApkMeta();
    if (apkMeta.icon == null) {
      return null;
    }
    return new Icon(apkMeta.icon, getFileData(apkMeta.icon));
  }

  private void transBinaryXml(String path, XmlStreamer xmlStreamer) throws IOException {
    ZipEntry entry = Utils.getEntry(zipFile, path);
    if (entry == null) {
      return;
    }
    if (resourceTable == null) {
      parseResourceTable();
    }
    InputStream in = zipFile.getInputStream(entry);
    ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
    BinaryXmlParser binaryXmlParser = new BinaryXmlParser(buffer, resourceTable);
    binaryXmlParser.setLocale(preferredLocale);
    binaryXmlParser.setXmlStreamer(xmlStreamer);
    binaryXmlParser.parse();
  }

  /**
   * Return all classes.dex files. If an app is using multi-dex there will be more than one dex
   * file.
   *
   * @return list of information about dex files.
   * @throws IOException
   *     if an error occurs while parsing the DEX file(s).
   */
  public List<DexInfo> getDexInfos() throws IOException {
    if (dexInfos == null) {
      parseDexFiles();
    }
    return dexInfos;
  }

  /**
   * Get class info from DEX file. Currently only class name
   */
  public DexClass[] getDexClasses() throws IOException {
    if (dex == null) {
      dex = parseDexFile();
    }
    return dex.classes;
  }

  public DexHeader getDexHeader() throws IOException {
    if (dex == null) {
      dex = parseDexFile();
    }
    return dex.header;
  }

  private void parseDexFiles() throws IOException {
    dexInfos = new ArrayList<>();
    dexInfos.add(parseDexFile());
    for (int i = 2; i < 1_000; i++) {
      String path = String.format("classes%d.dex", i);
      ZipEntry entry = Utils.getEntry(zipFile, path);
      if (entry == null) {
        break;
      }
      InputStream in = zipFile.getInputStream(entry);
      ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
      DexParser dexParser = new DexParser(buffer);
      dexInfos.add(dexParser.parse());
    }
  }

  private DexInfo parseDexFile() throws IOException {
    ZipEntry entry = Utils.getEntry(zipFile, AndroidConstants.DEX_FILE);
    if (entry == null) {
      throw new ParserException(AndroidConstants.DEX_FILE + " not found");
    }
    InputStream in = zipFile.getInputStream(entry);
    ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
    DexParser dexParser = new DexParser(buffer);
    return dexParser.parse();
  }

  /**
   * read file in apk into bytes
   */
  public byte[] getFileData(String path) throws IOException {
    ZipEntry entry = Utils.getEntry(zipFile, path);
    if (entry == null) {
      return null;
    }
    InputStream inputStream = zipFile.getInputStream(entry);
    return Utils.toByteArray(inputStream);
  }

  /**
   * @return One of:
   * {@link ApkSignStatus#SIGNED},
   * {@link ApkSignStatus#NOT_SIGNED},
   * {@link ApkSignStatus#INCORRECT}
   * @throws IOException
   *     if reading the APK file failed.
   */
  public int verifyApk() throws IOException {
    ZipEntry entry = Utils.getEntry(zipFile, "META-INF/MANIFEST.MF");
    if (entry == null) {
      // apk is not signed;
      return ApkSignStatus.NOT_SIGNED;
    }

    JarFile jarFile = new JarFile(apkFile);
    Enumeration<JarEntry> entries = jarFile.entries();
    byte[] buffer = new byte[8192];

    while (entries.hasMoreElements()) {
      JarEntry e = entries.nextElement();
      if (e.isDirectory()) {
        continue;
      }
      try {
        // Read in each jar entry. A security exception will be thrown if a signature/digest check fails.
        InputStream in = jarFile.getInputStream(e);
        int count;
        while ((count = in.read(buffer, 0, buffer.length)) != -1) {
          // Don't care
        }
        in.close();
      } catch (SecurityException se) {
        return ApkSignStatus.INCORRECT;
      }
    }
    return ApkSignStatus.SIGNED;
  }

  private void parseResourceTable() throws IOException {
    ZipEntry entry = Utils.getEntry(zipFile, AndroidConstants.RESOURCE_FILE);
    if (entry == null) {
      // if no resource entry has been found, we assume it is not needed by this APK
      resourceTable = new ResourceTable();
      locales = Collections.emptySet();
      return;
    }
    resourceTable = new ResourceTable();
    locales = Collections.emptySet();
    InputStream in = zipFile.getInputStream(entry);
    ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
    ResourceTableParser resourceTableParser = new ResourceTableParser(buffer);
    resourceTableParser.parse();
    resourceTable = resourceTableParser.getResourceTable();
    locales = resourceTableParser.getLocales();
  }

  @Override public void close() {
    resourceTable = null;
    certificate = null;
    try {
      zipFile.close();
    } catch (Exception ignored) {
    }
  }

  public Locale getPreferredLocale() {
    return preferredLocale;
  }

  /**
   * The locale preferred. Will cause getManifestXml / getApkMeta to return different values.
   * The default value is from os default locale setting.
   */
  public void setPreferredLocale(Locale locale) {
    if (!Utils.equals(preferredLocale, locale)) {
      preferredLocale = locale;
      manifestXml = null;
      apkMeta = null;
    }
  }

  public static final class ApkSignStatus {

    public static final int NOT_SIGNED = 0x00;
    public static final int INCORRECT = 0x01;
    public static final int SIGNED = 0x02;
  }

  public static class InvalidApkException extends RuntimeException {

    public InvalidApkException(String detailMessage, Throwable throwable) {
      super(detailMessage, throwable);
    }
  }

}
