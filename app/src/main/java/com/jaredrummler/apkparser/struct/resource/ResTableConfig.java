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

package com.jaredrummler.apkparser.struct.resource;

public class ResTableConfig {

  // Number of bytes in this structure. uint32_t
  private long size;

  // Mobile country code (from SIM).  0 means "any". uint16_t
  private short mcc;
  // Mobile network code (from SIM).  0 means "any". uint16_t
  private short mnc;
  //uint32_t imsi;

  // 0 means "any".  Otherwise, en, fr, etc. char[2]
  private String language;
  // 0 means "any".  Otherwise, US, CA, etc.  char[2]
  private String country;
  // uint32_t locale;

  // uint8_t
  private short orientation;
  // uint8_t
  private short touchscreen;
  // uint16_t
  private int density;
  // uint32_t screenType;

  // uint8_t
  private short keyboard;
  // uint8_t
  private short navigation;
  // uint8_t
  private short inputFlags;
  // uint8_t
  private short inputPad0;
  // uint32_t input;

  // uint16_t
  private int screenWidth;
  // uint16_t
  private int screenHeight;
  // uint32_t screenSize;

  // uint16_t
  private int sdkVersion;
  // For now minorVersion must always be 0!!!  Its meaning is currently undefined.
  // uint16_t
  private int minorVersion;
  //uint32_t version;

  // uint8_t
  private short screenLayout;
  // uint8_t
  private short uiMode;
  // uint8_t
  private short screenConfigPad1;
  // uint8_t
  private short screenConfigPad2;
  //uint32_t screenConfig;

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public short getMcc() {
    return mcc;
  }

  public void setMcc(short mcc) {
    this.mcc = mcc;
  }

  public short getMnc() {
    return mnc;
  }

  public void setMnc(short mnc) {
    this.mnc = mnc;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public short getOrientation() {
    return orientation;
  }

  public void setOrientation(short orientation) {
    this.orientation = orientation;
  }

  public short getTouchscreen() {
    return touchscreen;
  }

  public void setTouchscreen(short touchscreen) {
    this.touchscreen = touchscreen;
  }

  public int getDensity() {
    return density;
  }

  public void setDensity(int density) {
    this.density = density;
  }

  public short getKeyboard() {
    return keyboard;
  }

  public void setKeyboard(short keyboard) {
    this.keyboard = keyboard;
  }

  public short getNavigation() {
    return navigation;
  }

  public void setNavigation(short navigation) {
    this.navigation = navigation;
  }

  public short getInputFlags() {
    return inputFlags;
  }

  public void setInputFlags(short inputFlags) {
    this.inputFlags = inputFlags;
  }

  public short getInputPad0() {
    return inputPad0;
  }

  public void setInputPad0(short inputPad0) {
    this.inputPad0 = inputPad0;
  }

  public int getScreenWidth() {
    return screenWidth;
  }

  public void setScreenWidth(int screenWidth) {
    this.screenWidth = screenWidth;
  }

  public int getScreenHeight() {
    return screenHeight;
  }

  public void setScreenHeight(int screenHeight) {
    this.screenHeight = screenHeight;
  }

  public int getSdkVersion() {
    return sdkVersion;
  }

  public void setSdkVersion(int sdkVersion) {
    this.sdkVersion = sdkVersion;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public void setMinorVersion(int minorVersion) {
    this.minorVersion = minorVersion;
  }

  public short getScreenLayout() {
    return screenLayout;
  }

  public void setScreenLayout(short screenLayout) {
    this.screenLayout = screenLayout;
  }

  public short getUiMode() {
    return uiMode;
  }

  public void setUiMode(short uiMode) {
    this.uiMode = uiMode;
  }

  public short getScreenConfigPad1() {
    return screenConfigPad1;
  }

  public void setScreenConfigPad1(short screenConfigPad1) {
    this.screenConfigPad1 = screenConfigPad1;
  }

  public short getScreenConfigPad2() {
    return screenConfigPad2;
  }

  public void setScreenConfigPad2(short screenConfigPad2) {
    this.screenConfigPad2 = screenConfigPad2;
  }
}
